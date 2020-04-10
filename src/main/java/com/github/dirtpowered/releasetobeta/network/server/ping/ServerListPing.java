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
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
public class ServerListPing {
    private Message motd;
    private GameProfile[] playerListSample;

    @Setter
    private int maxPlayers;

    @Setter
    private int onlinePlayers;

    @Setter
    private int protocolVersion;

    @Setter
    private String versionString;

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
