package com.github.dirtpowered.releasetobeta.data.command.model;

import com.github.dirtpowered.releasetobeta.data.player.ModernPlayer;

public abstract class Command {
    public abstract void execute(ModernPlayer sender, String[] args);
}
