package com.github.dirtpowered.releasetobeta.configuration;

import com.github.steveice10.mc.protocol.data.message.ChatColor;
import com.github.steveice10.mc.protocol.data.message.Message;
import com.github.steveice10.mc.protocol.data.message.MessageStyle;
import com.github.steveice10.mc.protocol.data.message.TextMessage;

public class R2BConfiguration {

    private String motd;
    private int maxPlayers;
    private boolean skinFixEnabled;

    public R2BConfiguration() {
        //TODO: load config
        this.motd = "Release2Beta server";
        this.maxPlayers = 20;
        this.skinFixEnabled = true;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public Message getMotd() {
        return TextMessage.fromString(motd)
                .setStyle(new MessageStyle().setColor(ChatColor.RESET));
    }

    public boolean isSkinFixEnabled() {
        return skinFixEnabled;
    }
}
