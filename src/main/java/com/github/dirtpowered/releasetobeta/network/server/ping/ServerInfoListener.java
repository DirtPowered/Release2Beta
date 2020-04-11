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

package com.github.dirtpowered.releasetobeta.network.server.ping;

import com.github.dirtpowered.betaprotocollib.data.version.MinecraftVersion;
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

        if (R2BConfiguration.ver1_8PingPassthrough && R2BConfiguration.version == MinecraftVersion.B_1_8_1) {
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
