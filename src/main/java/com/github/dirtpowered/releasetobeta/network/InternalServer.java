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
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.pmw.tinylog.Logger;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class InternalServer implements Tickable {

    private final Queue<Packet> packetQueue = new LinkedBlockingQueue<>();
    private Server server;
    private ReleaseToBeta releaseToBeta;

    public InternalServer(ReleaseToBeta releaseToBeta) {
        this.releaseToBeta = releaseToBeta;
        createServer();
    }

    @SuppressWarnings("unchecked")
    private void handleIncomingPackets(BetaClientSession betaSession, Session modernSession) {
        Packet packet;
        while ((packet = packetQueue.poll()) != null) {
            ModernToBeta handler = releaseToBeta.getModernToBetaTranslatorRegistry().getByPacket(packet);
            if (handler != null) {
                handler.translate(packet, modernSession, betaSession);
                //Logger.info("[server] translating {}", packet.getClass().getSimpleName());
            } else {
                Logger.warn("[server] missing 'ModernToBeta' translator for {}", packet.getClass().getSimpleName());
            }
        }
    }

    private void createServer() {
        this.server = new Server("localhost", 25565, MinecraftProtocol.class, new TcpSessionFactory());
        this.server.setGlobalFlag(MinecraftConstants.VERIFY_USERS_KEY, false);

        server.setGlobalFlag(MinecraftConstants.SERVER_COMPRESSION_THRESHOLD, 100);

        this.server.setGlobalFlag(MinecraftConstants.SERVER_INFO_BUILDER_KEY, (ServerInfoBuilder) session ->
                new ServerStatusInfo(new VersionInfo(MinecraftConstants.GAME_VERSION, MinecraftConstants.PROTOCOL_VERSION),
                        new PlayerInfo(1, 0, new GameProfile[0]),
                        new TextMessage("DirtPowered 1.7.3 beta server"), null));

        server.setGlobalFlag(MinecraftConstants.SERVER_LOGIN_HANDLER_KEY, (ServerLoginHandler) this::createClientSession);

        server.addListener(new ServerAdapter() {

            @Override
            public void sessionAdded(SessionAddedEvent event) {
                event.getSession().addListener(new SessionAdapter() {
                    @Override
                    public void packetReceived(PacketReceivedEvent event) {
                        packetQueue.add(event.getPacket());
                    }
                });
            }

            @Override
            public void sessionRemoved(SessionRemovedEvent event) {
                releaseToBeta.getSessionRegistry().removeSession(event.getSession());
                getClientFromSession(event.getSession()).disconnect();
            }
        });

        server.bind();
    }

    public Server getServer() {
        return server;
    }

    private void createClientSession(Session session) {
        //TODO: make it correct
        Bootstrap b = new Bootstrap();
        b.group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 30000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        BetaClientSession betaClientSession = new BetaClientSession(releaseToBeta, ch);

                        ch.pipeline().addLast("mc_pipeline", new PipelineFactory());
                        ch.pipeline().addLast("handler", betaClientSession);

                        releaseToBeta.getSessionRegistry().addSession(betaClientSession, session);
                    }
                });
        b.connect("localhost", 25567);
    }

    private BetaClientSession getClientFromSession(Session session) {
        return releaseToBeta.getSessionRegistry().getSessions().get(session);
    }

    public Session getServerSession(BetaClientSession session) {
        return releaseToBeta.getSessionRegistry().getSessions().inverse().get(session);
    }

    @Override
    public void tick() {
        for (Map.Entry<Session, BetaClientSession> entry : releaseToBeta.getSessionRegistry().getSessions().entrySet()) {
            Session modernSession = entry.getKey();
            BetaClientSession betaSession = entry.getValue();
            handleIncomingPackets(betaSession, modernSession);
        }
    }
}
