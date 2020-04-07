package com.github.dirtpowered.releasetobeta.network.server;

import com.github.dirtpowered.releasetobeta.ReleaseToBeta;
import com.github.dirtpowered.releasetobeta.configuration.R2BConfiguration;
import com.github.dirtpowered.releasetobeta.network.codec.PipelineFactory;
import com.github.dirtpowered.releasetobeta.network.server.ping.LegacyPing.model.PingMessage;
import com.github.dirtpowered.releasetobeta.network.server.ping.ServerListPing;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.ModernToBeta;
import com.github.dirtpowered.releasetobeta.utils.Tickable;
import com.github.steveice10.mc.protocol.MinecraftConstants;
import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.mc.protocol.ServerLoginHandler;
import com.github.steveice10.mc.protocol.data.status.handler.ServerInfoBuilder;
import com.github.steveice10.mc.protocol.packet.login.client.LoginStartPacket;
import com.github.steveice10.packetlib.Server;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.event.server.ServerAdapter;
import com.github.steveice10.packetlib.event.server.SessionAddedEvent;
import com.github.steveice10.packetlib.event.server.SessionRemovedEvent;
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent;
import com.github.steveice10.packetlib.event.session.SessionAdapter;
import com.github.steveice10.packetlib.packet.Packet;
import com.github.steveice10.packetlib.tcp.TcpSessionFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.commons.lang3.RandomStringUtils;
import org.pmw.tinylog.Logger;

import java.net.InetSocketAddress;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ServerConnection implements Tickable {
    private final Queue<ServerQueuedPacket> packetQueue = new ConcurrentLinkedQueue<>();
    private ReleaseToBeta main;
    private PlayerList playerList;
    private ServerListPing serverListPing;

    ServerConnection(ModernServer modernServer) {
        this.main = modernServer.getMain();
        this.playerList = new PlayerList(this);

        serverListPing = new ServerListPing();

        serverListPing.setMaxPlayers(R2BConfiguration.maxPlayers);
        serverListPing.setMotd(R2BConfiguration.motd);

        Server server = new Server(R2BConfiguration.bindAddress, R2BConfiguration.bindPort, MinecraftProtocol.class, new TcpSessionFactory());

        server.setGlobalFlag(MinecraftConstants.VERIFY_USERS_KEY, false);
        server.setGlobalFlag(MinecraftConstants.SERVER_COMPRESSION_THRESHOLD, 256);
        server.setGlobalFlag(MinecraftConstants.SERVER_INFO_BUILDER_KEY, (ServerInfoBuilder) session -> {

            if (R2BConfiguration.ver1_8PingPassthrough) {
                PingMessage pingMessage = main.getPingPassthroughThread().getPingMessage();
                if (pingMessage == null) {
                    return serverListPing.get();
                }

                serverListPing.setMotd(pingMessage.getMotd());
                serverListPing.setOnlinePlayers(pingMessage.getOnlinePlayers());
                serverListPing.setMaxPlayers(pingMessage.getMaxPlayers());
                serverListPing.setPlayerListSample(playerList.getProfiles());
            } else {
                serverListPing.setPlayerListSample(playerList.getProfiles());
                serverListPing.setOnlinePlayers(playerList.getPlayers().size());
            }

            return serverListPing.get();
        });

        server.setGlobalFlag(MinecraftConstants.SERVER_LOGIN_HANDLER_KEY, (ServerLoginHandler) session -> {
            try {
                if (session.isConnected()) {
                    createClientSession(RandomStringUtils.randomAlphabetic(8), session);
                }
            } catch (InterruptedException e) {
                Logger.error(e.getMessage());
            }
        });

        server.addListener(new ServerAdapter() {
            @Override
            public void sessionAdded(SessionAddedEvent event) {
                event.getSession().addListener(new SessionAdapter() {
                    @Override
                    public void packetReceived(PacketReceivedEvent event) {
                        Packet packet = event.getPacket();

                        ServerQueuedPacket queuedPacket = new ServerQueuedPacket(event.getSession(), packet, packet instanceof LoginStartPacket);

                        packetQueue.add(queuedPacket);
                    }
                });
            }

            @Override
            public void sessionRemoved(SessionRemovedEvent event) {
                main.getSessionRegistry().getSessions().forEach((clientId, multiSession) -> {
                    if (multiSession.getModernSession().equals(event.getSession())) {
                        multiSession.getBetaClientSession().disconnect();
                    }
                });
            }
        });

        server.bind();
    }

    private void createClientSession(String clientId, Session session) throws InterruptedException {
        NioEventLoopGroup loopGroup = new NioEventLoopGroup();

        try {
            Bootstrap clientBootstrap = new Bootstrap();

            clientBootstrap.group(loopGroup);
            clientBootstrap.channel(NioSocketChannel.class);
            clientBootstrap
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.TCP_NODELAY, true);

            clientBootstrap.remoteAddress(new InetSocketAddress(R2BConfiguration.remoteAddress, R2BConfiguration.remotePort));
            clientBootstrap.handler(new ChannelInitializer<SocketChannel>() {

                @Override
                protected void initChannel(SocketChannel ch) {
                    ch.pipeline().addLast("mc_pipeline", new PipelineFactory());
                    BetaClientSession clientSession = new BetaClientSession(main, ch, session, clientId);

                    clientSession.createSession();
                    ch.pipeline().addLast("client_connection_handler", clientSession);
                }
            });

            ChannelFuture channelFuture = clientBootstrap.connect().sync();
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            session.disconnect(e.getMessage());
            main.getSessionRegistry().removeSession(main.getSessionRegistry().getClientSessionFromServerSession(session).getClientId());
        } finally {
            loopGroup.shutdownGracefully().sync();
        }
    }

    private void handlePackets() {
        while (!packetQueue.isEmpty()) {
            ServerQueuedPacket queuedPacket = packetQueue.peek();
            if (queuedPacket != null) {
                packetQueue.poll();
                translatePacket(queuedPacket);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void translatePacket(ServerQueuedPacket queuedPacket) {
        ModernToBeta handler = main.getModernToBetaTranslatorRegistry().getByPacket(queuedPacket.packet);
        if (handler != null) {
            BetaClientSession clientSession = main.getSessionRegistry().getClientSessionFromServerSession(queuedPacket.session);
            if (clientSession != null) {
                handler.translate(queuedPacket.packet, queuedPacket.session, clientSession);
            } else {
                if (queuedPacket.loginPacket && queuedPacket.session.isConnected()) {
                    packetQueue.add(queuedPacket);
                    return;
                }
                Logger.error("{} was not handled", queuedPacket.packet);
            }
        }
    }

    @Override
    public void tick() {
        handlePackets();
    }

    void broadcastPacket(Packet packet) {
        main.getSessionRegistry().getSessions().forEach((s, multiSession) -> {
            if (multiSession.getModernSession().isConnected()) {
                multiSession.getModernSession().send(packet);
            }
        });
    }

    public ReleaseToBeta getMain() {
        return main;
    }

    public PlayerList getPlayerList() {
        return playerList;
    }

    static class ServerQueuedPacket {
        private final Session session;
        private final Packet packet;
        private final boolean loginPacket;

        ServerQueuedPacket(Session session, Packet packet, boolean loginPacket) {
            this.session = session;
            this.packet = packet;
            this.loginPacket = loginPacket;
        }
    }
}
