package com.github.dirtpowered.releasetobeta.network;

import com.github.dirtpowered.releasetobeta.ReleaseToBeta;
import com.github.dirtpowered.releasetobeta.network.codec.PipelineFactory;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.ModernToBeta;
import com.github.dirtpowered.releasetobeta.utils.Tickable;
import com.github.steveice10.mc.auth.data.GameProfile;
import com.github.steveice10.mc.protocol.MinecraftConstants;
import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.mc.protocol.ServerLoginHandler;
import com.github.steveice10.mc.protocol.data.message.TextMessage;
import com.github.steveice10.mc.protocol.data.status.PlayerInfo;
import com.github.steveice10.mc.protocol.data.status.ServerStatusInfo;
import com.github.steveice10.mc.protocol.data.status.VersionInfo;
import com.github.steveice10.mc.protocol.data.status.handler.ServerInfoBuilder;
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
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.pmw.tinylog.Logger;

import java.net.InetSocketAddress;
import java.util.AbstractMap;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class InternalServer implements Tickable {

    /*
     * NOTE: Everything is broken here...
     */

    private final Queue<AbstractMap.SimpleEntry<Session, Packet>> packetQueue = new LinkedBlockingQueue<>();
    private final VersionInfo versionInfo = new VersionInfo(MinecraftConstants.GAME_VERSION, MinecraftConstants.PROTOCOL_VERSION);
    private final PlayerInfo playerInfo = new PlayerInfo(0, 0, new GameProfile[0]);
    private final TextMessage motd = new TextMessage("ReleaseToBeta server");
    private Server server;
    private ReleaseToBeta releaseToBeta;

    public InternalServer(ReleaseToBeta releaseToBeta) {
        this.releaseToBeta = releaseToBeta;

        createServer();
    }

    @SuppressWarnings("unchecked")
    private void handleIncomingPackets(BetaClientSession betaSession, Session modernSession) {
        AbstractMap.SimpleEntry<Session, Packet> entries;
        while ((entries = packetQueue.poll()) != null) {
            Packet packet = entries.getValue();

            ModernToBeta handler = releaseToBeta.getModernToBetaTranslatorRegistry().getByPacket(packet);
            if (handler != null) {
                if (entries.getKey().equals(modernSession)) {
                    handler.translate(packet, modernSession, betaSession);
                }
            } else {
                Logger.warn("[server] missing 'ModernToBeta' translator for {}", packet.getClass().getSimpleName());
            }
        }
    }

    private void createServer() {
        server = new Server("localhost", 25565, MinecraftProtocol.class, new TcpSessionFactory());
        server.setGlobalFlag(MinecraftConstants.VERIFY_USERS_KEY, false);
        server.setGlobalFlag(MinecraftConstants.SERVER_COMPRESSION_THRESHOLD, 256);
        server.setGlobalFlag(MinecraftConstants.SERVER_INFO_BUILDER_KEY, (ServerInfoBuilder) session -> {
            return new ServerStatusInfo(versionInfo, playerInfo, motd, null);
        });

        server.setGlobalFlag(MinecraftConstants.SERVER_LOGIN_HANDLER_KEY, (ServerLoginHandler) session -> {
            try {
                if (session.isConnected()) {
                    createClientSession(session);
                }

            } catch (InterruptedException e) {
                Logger.error("Error: {}", e.getMessage());
            }
        });

        server.addListener(new ServerAdapter() {
            @Override
            public void sessionAdded(SessionAddedEvent event) {
                event.getSession().addListener(new SessionAdapter() {
                    @Override
                    public void packetReceived(PacketReceivedEvent event) {
                        packetQueue.add(new AbstractMap.SimpleEntry<>(event.getSession(), event.getPacket()));
                    }
                });
            }

            @Override
            public void sessionRemoved(SessionRemovedEvent event) {
                BetaClientSession session = getClientFromSession(event.getSession());

                releaseToBeta.getSessionRegistry().removeSession(session);
                session.disconnect();
            }
        });

        server.bind();
    }

    private BetaClientSession getClientFromSession(Session session) {
        return releaseToBeta.getSessionRegistry().getSessions().get(session);
    }

    public Session getServerSession(BetaClientSession session) {
        return releaseToBeta.getSessionRegistry().getSessions().inverse().get(session);
    }

    public void broadcastPacket(Packet packet) {
        releaseToBeta.getSessionRegistry().getSessions().forEach((key, value) -> {
            key.send(packet);
        });
    }

    @Override
    public void tick() {
        releaseToBeta.getSessionRegistry().getSessions().forEach((modernSession, betaSession) -> {
            handleIncomingPackets(betaSession, modernSession);
        });
    }

    public Server getServer() {
        return server;
    }

    private void createClientSession(Session session) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap clientBootstrap = new Bootstrap();

            clientBootstrap.group(group);
            clientBootstrap.channel(NioSocketChannel.class);
            clientBootstrap
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 30000);

            clientBootstrap.remoteAddress(new InetSocketAddress("localhost", 25567));
            clientBootstrap.handler(new ChannelInitializer<SocketChannel>() {

                @Override
                protected void initChannel(SocketChannel ch) {
                    ch.pipeline().addLast("mc_pipeline", new PipelineFactory());
                    ch.pipeline().addLast("client_connection_handler", new BetaClientSession(releaseToBeta, ch, session));
                }
            });

            ChannelFuture channelFuture = clientBootstrap.connect().sync();
            channelFuture.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }
}
