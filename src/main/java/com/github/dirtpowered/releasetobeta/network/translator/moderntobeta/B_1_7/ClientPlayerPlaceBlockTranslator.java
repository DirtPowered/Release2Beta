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

package com.github.dirtpowered.releasetobeta.network.translator.moderntobeta.B_1_7;

import com.github.dirtpowered.betaprotocollib.data.BetaItemStack;
import com.github.dirtpowered.betaprotocollib.data.version.MinecraftVersion;
import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.BlockPlacePacketData;
import com.github.dirtpowered.releasetobeta.ReleaseToBeta;
import com.github.dirtpowered.releasetobeta.configuration.R2BConfiguration;
import com.github.dirtpowered.releasetobeta.data.Constants;
import com.github.dirtpowered.releasetobeta.data.inventory.PlayerInventory;
import com.github.dirtpowered.releasetobeta.data.item.ItemFood;
import com.github.dirtpowered.releasetobeta.data.player.ModernPlayer;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.ModernToBeta;
import com.github.steveice10.mc.protocol.data.MagicValues;
import com.github.steveice10.mc.protocol.data.game.MessageType;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.ItemStack;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.Position;
import com.github.steveice10.mc.protocol.data.message.TextMessage;
import com.github.steveice10.mc.protocol.data.message.TranslationMessage;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPlaceBlockPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerHealthPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.window.ServerSetSlotPacket;
import com.github.steveice10.packetlib.Session;

public class ClientPlayerPlaceBlockTranslator implements ModernToBeta<ClientPlayerPlaceBlockPacket> {

    @Override
    public void translate(ReleaseToBeta main, ClientPlayerPlaceBlockPacket packet, Session modernSession, BetaClientSession betaSession) {
        ModernPlayer player = betaSession.getPlayer();
        Position pos = packet.getPosition();
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        int face = MagicValues.value(Integer.class, packet.getFace());

        PlayerInventory inventory = player.getInventory();

        BlockPlacePacketData blockPlacePacketData = new BlockPlacePacketData(x, y, z, face, new BetaItemStack()); //item-stack is ignored

        int height = (R2BConfiguration.version == MinecraftVersion.B_1_8_1 ? 256 : 128);
        if (y >= height - 2)
            player.sendRawMessage(new TranslationMessage("build.tooHigh", TextMessage.fromString(String.valueOf(height))), MessageType.NOTIFICATION);

        betaSession.sendPacket(blockPlacePacketData);

        if (R2BConfiguration.disableSprinting) {
            modernSession.send(new ServerPlayerHealthPacket(player.getHealth(), Constants.NO_SPRING_FOOD_LEVEL, 0));
        }

        ItemStack itemStack = inventory.getItemInHand();
        if (itemStack == null)
            return;
        /*
         * Special note on using buckets: When using buckets, the Notchian client might send two packets: first a normal and then a special case
         * https://wiki.vg/index.php?title=Protocol&oldid=689#Player_Block_Placement_.280x0F.29
         */
        if (itemStack.getId() == 597 || itemStack.getId() == 596 || itemStack.getId() == 595 | itemStack.getId() == 604)
            betaSession.sendPacket(blockPlacePacketData);

        // b1.7.3 server sometimes fails to update current slot
        if (ItemFood.isFoodItem(itemStack.getId()) || itemStack.getId() == 589) {
            int currentSlot = inventory.getCurrentSlot();
            inventory.decrement(currentSlot);

            modernSession.send(new ServerSetSlotPacket(0, currentSlot, inventory.getItem(currentSlot)));
        }

        player.onBlockPlace(face, x, y, z, itemStack);
    }
}
