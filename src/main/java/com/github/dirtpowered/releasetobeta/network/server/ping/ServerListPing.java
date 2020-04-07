package com.github.dirtpowered.releasetobeta.network.server.ping;

import com.github.dirtpowered.releasetobeta.utils.TextColor;
import com.github.steveice10.mc.auth.data.GameProfile;
import com.github.steveice10.mc.protocol.data.message.ChatColor;
import com.github.steveice10.mc.protocol.data.message.Message;
import com.github.steveice10.mc.protocol.data.message.MessageStyle;
import com.github.steveice10.mc.protocol.data.message.TextMessage;
import com.github.steveice10.mc.protocol.data.status.PlayerInfo;
import com.github.steveice10.mc.protocol.data.status.ServerStatusInfo;
import com.github.steveice10.mc.protocol.data.status.VersionInfo;

import java.util.List;

public class ServerListPing {
    private int maxPlayers;
    private int onlinePlayers;
    private Message motd;
    private int protocolVersion;
    private String versionString;
    private GameProfile[] playerListSample;

    public ServerListPing() {
        protocolVersion = 340;
        versionString = "ReleaseToBeta 1.12.2";
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public void setOnlinePlayers(int onlinePlayers) {
        this.onlinePlayers = onlinePlayers;
    }

    public void setMotd(String motd) {
        this.motd = TextMessage.fromString(TextColor.translate(motd))
                .setStyle(new MessageStyle().setColor(ChatColor.RESET));
    }

    public void setPlayerListSample(List<GameProfile> playerListSample) {
        this.playerListSample = playerListSample.toArray(new GameProfile[0]);
    }

    public ServerStatusInfo get() {
        return new ServerStatusInfo(
                new VersionInfo(versionString, protocolVersion),
                new PlayerInfo(maxPlayers, onlinePlayers, playerListSample),
                motd, null
        );
    }
}
