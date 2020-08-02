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

package com.github.dirtpowered.releasetobeta.data.player;

import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.BlockDigPacketData;
import com.github.dirtpowered.releasetobeta.data.block.HardnessTable;
import com.github.dirtpowered.releasetobeta.data.entity.model.PlayerAction;
import com.github.dirtpowered.releasetobeta.data.mapping.flattening.DataConverter;
import com.github.dirtpowered.releasetobeta.utils.interfaces.Tickable;
import com.github.steveice10.mc.protocol.data.game.entity.Effect;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.ItemStack;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.Position;
import com.github.steveice10.mc.protocol.data.game.entity.player.BlockBreakStage;
import com.github.steveice10.mc.protocol.data.game.window.WindowType;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityEffectPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityRemoveEffectPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerBlockBreakAnimPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerOpenTileEntityEditorPacket;

public class PlayerEvent implements PlayerAction, Tickable {

    private ModernPlayer player;
    private int currentMiningTicks;
    private Position pos;
    private int blockId;

    PlayerEvent(ModernPlayer player) {
        this.player = player;
        this.currentMiningTicks = 0;
    }

    private int getItemInHand() {
        ItemStack itemStack = player.getInventory().getItemInHand();
        if (itemStack == null) {
            return 0;
        }

        return itemStack.getId();
    }

    private void sendSlownessEffect() {
        player.sendPacket(new ServerEntityEffectPacket(
                player.getEntityId(), Effect.SLOWER_DIG, Integer.MAX_VALUE, Integer.MAX_VALUE, false, false)
        );
    }

    private void removeSlownessEffect() {
        player.sendPacket(new ServerEntityRemoveEffectPacket(player.getEntityId(), Effect.SLOWER_DIG));
    }

    private void finishBreaking() {
        player.getSession().sendPacket(new BlockDigPacketData(pos.getX(), pos.getY(), pos.getZ(), 0, 2));
    }

    @Override
    public void onBlockStartBreaking(Position p) {
        int typeId = DataConverter.getNewBlockId(player.getSession().getChunkCache().getBlockAt(p.getX(), p.getY(), p.getZ()), 0);

        if (HardnessTable.exist(typeId)) {
            this.currentMiningTicks = HardnessTable.getMiningTicks(typeId, getItemInHand());
            this.pos = p;
            this.blockId = typeId;

            sendSlownessEffect();
        }
    }

    @Override
    public void onBlockCancelBreaking(Position p) {
        if (HardnessTable.exist(blockId)) {
            removeSlownessEffect();
            cancelCracks();

            this.currentMiningTicks = 0;
            this.pos = null;
        }
    }

    @Override
    public void onInventoryClose() {
        player.setOpenedInventoryType(WindowType.GENERIC_3X3);
    }

    @Override
    public void onInventoryOpen(WindowType windowType) {
        player.setOpenedInventoryType(windowType);
    }

    @Override
    public void tick() {
        if (currentMiningTicks != 0) {
            currentMiningTicks--;

            sendBlockCracks();
        } else {
            if (pos != null) {
                cancelCracks();
                finishBreaking();
                removeSlownessEffect();

                this.pos = null;
            }
        }
    }

    private void cancelCracks() {
        if (pos != null) {
            player.sendPacket(new ServerBlockBreakAnimPacket(player.getEntityId(), pos, BlockBreakStage.RESET));
        }
    }

    private void sendBlockCracks() {
        int originalTime = HardnessTable.getMiningTicks(blockId, getItemInHand());
        int flag = (currentMiningTicks * 9 / originalTime);

        BlockBreakStage stage = BlockBreakStage.RESET;

        switch (flag) {
            case 9:
                stage = BlockBreakStage.STAGE_1;
                break;
            case 8:
                stage = BlockBreakStage.STAGE_2;
                break;
            case 7:
                stage = BlockBreakStage.STAGE_3;
                break;
            case 6:
                stage = BlockBreakStage.STAGE_4;
                break;
            case 5:
                stage = BlockBreakStage.STAGE_5;
                break;
            case 4:
                stage = BlockBreakStage.STAGE_6;
                break;
            case 3:
                stage = BlockBreakStage.STAGE_7;
                break;
            case 2:
                stage = BlockBreakStage.STAGE_8;
                break;
            case 1:
                stage = BlockBreakStage.STAGE_9;
                break;
            case 0:
                stage = BlockBreakStage.STAGE_9;
                break;
        }

        if (pos != null) {
            player.sendPacket(new ServerBlockBreakAnimPacket(player.getEntityId(), pos, stage));
        }
    }

    @Override
    public void onBlockPlace(int face, int x, int y, int z, ItemStack itemstack) {
        switch (face) {
            case 1:
                ++y;
                break;
            case 2:
                --z;
                break;
            case 3:
                ++z;
                break;
            case 4:
                --x;
                break;
            case 5:
                ++x;
                break;
        }

        int itemId = itemstack.getId();

        if (itemId == 589) {
            player.sendPacket(new ServerOpenTileEntityEditorPacket(new Position(x, y, z)));
        }
    }
}
