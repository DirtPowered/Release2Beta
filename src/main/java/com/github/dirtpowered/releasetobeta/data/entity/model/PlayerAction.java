package com.github.dirtpowered.releasetobeta.data.entity.model;

import com.github.steveice10.mc.protocol.data.game.entity.metadata.ItemStack;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.Position;
import com.github.steveice10.mc.protocol.data.game.window.WindowType;

public interface PlayerAction {

    void onBlockPlace(Position pos, ItemStack itemstack);

    void onInventoryClose();

    void onInventoryOpen(WindowType windowType);
}
