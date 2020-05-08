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

import com.github.dirtpowered.releasetobeta.utils.ChatUtils;
import com.github.steveice10.mc.auth.data.GameProfile;
import com.github.steveice10.mc.protocol.data.message.ChatColor;
import com.github.steveice10.mc.protocol.data.message.Message;
import com.github.steveice10.mc.protocol.data.message.MessageStyle;
import com.github.steveice10.mc.protocol.data.message.TextMessage;
import com.github.steveice10.mc.protocol.data.status.PlayerInfo;
import com.github.steveice10.mc.protocol.data.status.ServerStatusInfo;
import com.github.steveice10.mc.protocol.data.status.VersionInfo;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.awt.image.BufferedImage;
import java.util.List;

@NoArgsConstructor
public class ServerListPing {
    private Message motd;
    private GameProfile[] playerListSample;

    @Setter
    private BufferedImage icon;

    @Setter
    private int maxPlayers;

    @Setter
    private int onlinePlayers;

    @Setter
    private int protocolVersion;

    @Setter
    private String versionString;

    public void setMotd(String motd) {
        this.motd = TextMessage.fromString(ChatUtils.colorize(motd))
                .setStyle(new MessageStyle().setColor(ChatColor.RESET));
    }

    public void setPlayerListSample(List<GameProfile> playerListSample) {
        this.playerListSample = playerListSample.toArray(new GameProfile[0]);
    }

    public ServerStatusInfo get() {
        return new ServerStatusInfo(
                new VersionInfo(versionString, protocolVersion),
                new PlayerInfo(maxPlayers, onlinePlayers, playerListSample),
                motd, icon
        );
    }
}
