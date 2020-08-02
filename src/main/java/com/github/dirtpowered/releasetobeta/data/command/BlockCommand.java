package com.github.dirtpowered.releasetobeta.data.command;

import com.github.dirtpowered.releasetobeta.data.command.model.Command;
import com.github.dirtpowered.releasetobeta.data.mapping.flattening.DataConverter;
import com.github.dirtpowered.releasetobeta.data.player.ModernPlayer;

public class BlockCommand extends Command {

    @Override
    public void execute(ModernPlayer sender, String[] args) {
        sender.sendMessage("newId: " + DataConverter.getNewItemId(Integer.parseInt(args[1]), 0));
    }
}
