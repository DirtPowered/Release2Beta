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
import com.github.dirtpowered.releasetobeta.utils.interfaces.Tickable;
import com.github.steveice10.mc.protocol.MinecraftConstants;
import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.mc.protocol.packet.handshake.client.HandshakePacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientPluginMessagePacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientSettingsPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerAbilitiesPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerMovementPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.world.ClientSteerBoatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.world.ClientVehicleMovePacket;
import com.github.steveice10.mc.protocol.packet.login.client.LoginStartPacket;
import com.github.steveice10.mc.protocol.packet.status.client.StatusPingPacket;
import com.github.steveice10.mc.protocol.packet.status.client.StatusQueryPacket;
import com.github.steveice10.packetlib.Server;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.event.server.ServerAdapter;
import com.github.steveice10.packetlib.event.server.ServerClosingEvent;
import com.github.steveice10.packetlib.event.server.SessionAddedEvent;
import com.github.steveice10.packetlib.event.server.SessionRemovedEvent;
import com.github.steveice10.packetlib.event.session.DisconnectingEvent;
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent;
import com.github.steveice10.packetlib.event.session.SessionAdapter;
import com.github.steveice10.packetlib.packet.Packet;
import com.github.steveice10.packetlib.tcp.TcpSessionFactory;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.UUID;

public class ServerConnection implements Tickable {

    @Getter
    ModernServer modernServer;

    private Server server;

    @Getter
    private ReleaseToBeta main;

    @Getter
    private PlayerList playerList;

    private Class[] notNeededPackets = new Class[]{
            ClientPlayerMovementPacket.class,
            ClientPlayerAbilitiesPacket.class,
            ClientSettingsPacket.class,
            StatusPingPacket.class,
            ClientPluginMessagePacket.class,
            ClientSteerBoatPacket.class,
            ClientVehicleMovePacket.class
    };

    ServerConnection(ModernServer modernServer) {
        this.modernServer = modernServer;

        main = modernServer.getMain();
        playerList = new PlayerList(this);

        server = new Server(R2BConfiguration.bindAddress, R2BConfiguration.bindPort, MinecraftProtocol.class, new TcpSessionFactory());

        server.setGlobalFlag(MinecraftConstants.VERIFY_USERS_KEY, R2BConfiguration.onlineMode);
        server.setGlobalFlag(MinecraftConstants.SERVER_COMPRESSION_THRESHOLD, R2BConfiguration.compressionThreshold);
        server.setGlobalFlag(MinecraftConstants.SERVER_INFO_BUILDER_KEY, new ServerInfoListener(this));
        server.setGlobalFlag(MinecraftConstants.SERVER_LOGIN_HANDLER_KEY, new LoginHandler(main));

        server.addListener(new ServerAdapter() {

            @Override
            public void serverClosing(ServerClosingEvent event) {
                event.getServer().getSessions().forEach(session -> {
                    if (session != null)
                        session.disconnect("Server closed", true);
                });
            }

            @Override
            public void sessionAdded(SessionAddedEvent event) {
                event.getSession().addListener(new SessionAdapter() {

                    @Override
                    public void disconnecting(DisconnectingEvent event) {
                        getMain().getLogger().warning("[" + event.getSession().getLocalAddress() + "] " + event.getCause().getMessage());
                    }

                    @Override
                    public void packetReceived(PacketReceivedEvent event) {
                        Packet packet = event.getPacket();

                        if (packet instanceof StatusQueryPacket) {
                            if (R2BConfiguration.logPings)
                                main.getLogger().info(event.getSession().getLocalAddress() + " has pinged");
                            return;
                        } else if (packet instanceof HandshakePacket) {
                            return;
                        }

                        ServerQueuedPacket queuedPacket = new ServerQueuedPacket(event.getSession(), packet);
                        translatePacket(queuedPacket);
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

    @SuppressWarnings("unchecked")
    public void translatePacket(ServerQueuedPacket queuedPacket) {
        ModernToBeta handler = main.getModernToBetaTranslatorRegistry().getByPacket(queuedPacket.packet);
        if (handler != null) {
            if (queuedPacket.session.getFlag("ready") != null) {
                UUID uuid = queuedPacket.session.getFlag("uniqueId");
                BetaClientSession clientSession = main.getSessionRegistry().getSession(uuid).getBetaClientSession();
                handler.translate(queuedPacket.packet, queuedPacket.session, clientSession);
            } else {
                if (queuedPacket.packet instanceof LoginStartPacket && !queuedPacket.session.hasFlag("login_packet")) {
                    queuedPacket.session.setFlag("login_packet", queuedPacket.packet);
                    return;
                }

                main.getLogger().warning(queuedPacket.packet + " was not handled");
            }
        } else if (Arrays.stream(notNeededPackets).noneMatch(aClass -> aClass.equals(queuedPacket.packet.getClass()))) {
            main.getLogger().warning("skipped " + queuedPacket.packet);
        }
    }

    @Override
    public void tick() {
        playerList.updateInternalTabList();
    }

    void broadcastPacket(Packet packet) {
        main.getSessionRegistry().getSessions().forEach((s, multiSession) -> {
            if (multiSession.getModernSession().isConnected()) {
                multiSession.getModernSession().send(packet);
            }
        });
    }

    public void shutdown() {
        server.close(true);
    }

    @AllArgsConstructor
    public static class ServerQueuedPacket {
        private final Session session;
        private final Packet packet;
    }
}
