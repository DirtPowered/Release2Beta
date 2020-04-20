/*
 * Copyright (c) 2020 Dirt Powered
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
import com.github.steveice10.mc.protocol.packet.handshake.client.HandshakePacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientPluginMessagePacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientSettingsPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerAbilitiesPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerMovementPacket;
import com.github.steveice10.mc.protocol.packet.login.client.LoginStartPacket;
import com.github.steveice10.mc.protocol.packet.status.client.StatusPingPacket;
import com.github.steveice10.mc.protocol.packet.status.client.StatusQueryPacket;
import com.github.steveice10.packetlib.Server;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.event.server.ServerAdapter;
import com.github.steveice10.packetlib.event.server.SessionAddedEvent;
import com.github.steveice10.packetlib.event.server.SessionRemovedEvent;
import com.github.steveice10.packetlib.event.session.DisconnectingEvent;
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent;
import com.github.steveice10.packetlib.event.session.SessionAdapter;
import com.github.steveice10.packetlib.packet.Packet;
import com.github.steveice10.packetlib.tcp.TcpSessionFactory;
import lombok.Getter;
import org.pmw.tinylog.Logger;

import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ServerConnection implements Tickable {
    private final Queue<ServerQueuedPacket> packetQueue = new ConcurrentLinkedQueue<>();

    @Getter
    private ReleaseToBeta main;

    @Getter
    private PlayerList playerList;

    private Class[] notNeededPackets = new Class[]{
            ClientPlayerMovementPacket.class,
            ClientPlayerAbilitiesPacket.class,
            ClientSettingsPacket.class,
            StatusPingPacket.class,
            ClientPluginMessagePacket.class
    };

    ServerConnection(ModernServer modernServer) {
        main = modernServer.getMain();
        playerList = new PlayerList(this);

        Server server = new Server(R2BConfiguration.bindAddress, R2BConfiguration.bindPort, MinecraftProtocol.class, new TcpSessionFactory());

        server.setGlobalFlag(MinecraftConstants.VERIFY_USERS_KEY, R2BConfiguration.onlineMode);
        server.setGlobalFlag(MinecraftConstants.SERVER_COMPRESSION_THRESHOLD, 256);
        server.setGlobalFlag(MinecraftConstants.SERVER_INFO_BUILDER_KEY, new ServerInfoListener(this));
        server.setGlobalFlag(MinecraftConstants.SERVER_LOGIN_HANDLER_KEY, new LoginHandler(main));

        server.addListener(new ServerAdapter() {
            @Override
            public void sessionAdded(SessionAddedEvent event) {
                event.getSession().addListener(new SessionAdapter() {

                    @Override
                    public void disconnecting(DisconnectingEvent event) {
                        //just to get more detailed stacktrace about all possible errors
                        event.getCause().printStackTrace();
                    }

                    @Override
                    public void packetReceived(PacketReceivedEvent event) {
                        Packet packet = event.getPacket();
                        if (packet instanceof StatusQueryPacket) {
                            Logger.info("{} has pinged", event.getSession().getLocalAddress());
                            return;
                        } else if (packet instanceof HandshakePacket) {
                            return;
                        }

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
        } else if (Arrays.stream(notNeededPackets).noneMatch(aClass -> aClass.equals(queuedPacket.packet.getClass()))) {
            Logger.warn("skipped {}", queuedPacket.packet);
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

    public void UNSAFE_addPacketToQueue(Session session, Packet packet) {
        packetQueue.add(new ServerQueuedPacket(session, packet, false));
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
