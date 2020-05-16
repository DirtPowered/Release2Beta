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
import com.github.dirtpowered.releasetobeta.utils.ChatUtils;
import com.github.steveice10.mc.protocol.data.status.ServerStatusInfo;
import com.github.steveice10.mc.protocol.data.status.handler.ServerInfoBuilder;
import com.github.steveice10.packetlib.Session;
import org.apache.commons.lang3.StringUtils;

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

        int onlineCount = serverConnection.getPlayerList().getOnlineCount();
        serverListPing.setOnlinePlayers(onlineCount);

        serverListPing.setPlayerListSample(serverConnection.getPlayerList().getProfiles().subList(0, onlineCount < 20 ? onlineCount : 20));
        serverListPing.setIcon(serverConnection.getModernServer().getServerIcon());

        if (R2BConfiguration.ver1_8PingPassthrough && R2BConfiguration.version.isNewerOrEqual(MinecraftVersion.B_1_8_1)) {
            PingMessage pingMessage = serverConnection.getMain().getPingPassthroughThread().getPingMessage();

            if (pingMessage == null) {
                return serverListPing.get();
            }

            if (System.currentTimeMillis() - serverConnection.getMain().getPingPassthroughThread().getLastStatusUpdate() < Constants.PING_INTERVAL + 1000) {
                serverListPing.setMotd(pingMessage.getMotd());
                serverListPing.setOnlinePlayers(pingMessage.getOnlinePlayers());
                serverListPing.setMaxPlayers(pingMessage.getMaxPlayers());
            } else {
                serverListPing.setMotd(ChatUtils.colorize("&9Can't connect to remote server"));
                serverListPing.setOnlinePlayers(0);
                serverListPing.setMaxPlayers(0);

                serverListPing.setProtocolVersion(-1);
                serverListPing.setVersionString(StringUtils.EMPTY);
            }
        }

        return serverListPing.get();
    }
}
