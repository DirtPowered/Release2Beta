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
import com.github.dirtpowered.releasetobeta.data.inventory.PlayerInventory;
import com.github.dirtpowered.releasetobeta.data.player.ModernPlayer;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.ModernToBeta;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.ItemStack;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerUseItemPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.window.ServerSetSlotPacket;
import com.github.steveice10.packetlib.Session;

public class ClientPlayerUseItemTranslator implements ModernToBeta<ClientPlayerUseItemPacket> {

    /**
     * This packet has a special case where X, Y, Z, and Direction are all -1.
     * This special packet indicates that the currently held item for the player
     * should have its state updated such as eating food, shooting bows, using buckets, etc.
     * <p>
     * https://wiki.vg/index.php?title=Protocol&oldid=689#Player_Block_Placement_.280x0F.29
     */

    @Override
    public void translate(ReleaseToBeta main, ClientPlayerUseItemPacket packet, Session modernSession, BetaClientSession betaSession) {
        betaSession.sendPacket(new BlockPlacePacketData(-1, -1, -1, -1, new BetaItemStack()));

        ModernPlayer player = betaSession.getPlayer();
        PlayerInventory inventory = player.getInventory();
        ItemStack itemStack = inventory.getItemInHand();

        // Updating item amount in beta is done client-side (modern client still do that, but not for arrows)
        //TODO: Use new IDs
        if (!MinecraftVersion.B_1_8_1.isNewerOrEqual(R2BConfiguration.version) && itemStack != null && itemStack.getId() == 261 /* bow */) {
            int slot = inventory.removeItem(262 /* arrow */);
            if (slot != -1) {
                modernSession.send(new ServerSetSlotPacket(0, slot, inventory.getItem(slot)));
            }
        }
    }
}
