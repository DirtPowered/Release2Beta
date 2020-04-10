package com.github.dirtpowered.releasetobeta.network.server;

import com.github.dirtpowered.releasetobeta.ReleaseToBeta;
import com.github.dirtpowered.releasetobeta.configuration.R2BConfiguration;
import com.github.dirtpowered.releasetobeta.network.server.login.LoginHandler;
import com.github.dirtpowered.releasetobeta.network.server.ping.ServerInfoListener;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.ModernToBeta;
import com.github.dirtpowered.releasetobeta.utils.Tickable;
import com.github.steveice10.mc.protocol.MinecraftConstants;
import com.github.steveice10.mc.protocol.MinecraftProtocol;
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
import lombok.Getter;
import org.pmw.tinylog.Logger;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ServerConnection implements Tickable {
    private final Queue<ServerQueuedPacket> packetQueue = new ConcurrentLinkedQueue<>();

    @Getter
    private ReleaseToBeta main;

    @Getter
    private PlayerList playerList;

    ServerConnection(ModernServer modernServer) {
        main = modernServer.getMain();
        playerList = new PlayerList(this);

        Server server = new Server(R2BConfiguration.bindAddress, R2BConfiguration.bindPort, MinecraftProtocol.class, new TcpSessionFactory());

        server.setGlobalFlag(MinecraftConstants.VERIFY_USERS_KEY, false);
        server.setGlobalFlag(MinecraftConstants.SERVER_COMPRESSION_THRESHOLD, 256);
        server.setGlobalFlag(MinecraftConstants.SERVER_INFO_BUILDER_KEY, new ServerInfoListener(this));
        server.setGlobalFlag(MinecraftConstants.SERVER_LOGIN_HANDLER_KEY, new LoginHandler(main));

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
        playerList.updateInternalTabList();
    }

    void broadcastPacket(Packet packet) {
        main.getSessionRegistry().getSessions().forEach((s, multiSession) -> {
            if (multiSession.getModernSession().isConnected()) {
                multiSession.getModernSession().send(packet);
            }
        });
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
