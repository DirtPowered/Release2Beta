package com.github.dirtpowered.releasetobeta.network.server.ping;

import com.github.dirtpowered.releasetobeta.configuration.R2BConfiguration;
import com.github.dirtpowered.releasetobeta.data.Constants;
import com.github.dirtpowered.releasetobeta.network.server.ServerConnection;
import com.github.dirtpowered.releasetobeta.network.server.ping.LegacyPing.model.PingMessage;
import com.github.steveice10.mc.protocol.data.status.ServerStatusInfo;
import com.github.steveice10.mc.protocol.data.status.handler.ServerInfoBuilder;
import com.github.steveice10.packetlib.Session;

public class ServerInfoListener implements ServerInfoBuilder {

    private ServerConnection serverConnection;
    private ServerListPing serverListPing;

    public ServerInfoListener(ServerConnection serverConnection) {
        this.serverConnection = serverConnection;
        serverListPing = new ServerListPing();
    }

    @Override
    public ServerStatusInfo buildInfo(Session session) {
        serverListPing.setProtocolVersion(Constants.PING_PROTOCOL);
        serverListPing.setVersionString(Constants.PING_VERSION_STRING);

        serverListPing.setMaxPlayers(R2BConfiguration.maxPlayers);
        serverListPing.setMotd(R2BConfiguration.motd);

        if (R2BConfiguration.ver1_8PingPassthrough) {
            PingMessage pingMessage = serverConnection.getMain().getPingPassthroughThread().getPingMessage();

            if (pingMessage == null) {
                return serverListPing.get();
            }

            serverListPing.setMotd(pingMessage.getMotd());
            serverListPing.setOnlinePlayers(pingMessage.getOnlinePlayers());
            serverListPing.setMaxPlayers(pingMessage.getMaxPlayers());
            serverListPing.setPlayerListSample(serverConnection.getPlayerList().getProfiles());
        } else {
            serverListPing.setOnlinePlayers(serverConnection.getPlayerList().getPlayers().size());
            serverListPing.setPlayerListSample(serverConnection.getPlayerList().getProfiles());
        }

        return serverListPing.get();
    }
}
