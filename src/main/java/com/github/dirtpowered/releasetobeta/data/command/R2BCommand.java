package com.github.dirtpowered.releasetobeta.data.command;

import com.github.dirtpowered.releasetobeta.data.command.model.Command;
import com.github.dirtpowered.releasetobeta.data.player.ModernPlayer;
import com.github.dirtpowered.releasetobeta.utils.ChatColor;

public class R2BCommand extends Command {

    @Override
    public void execute(ModernPlayer sender, String[] args) {
        sender.sendMessage(ChatColor.translate("&7This server is using ReleaseToBeta&8: &agithub.com/DirtPowered"));
    }
}
