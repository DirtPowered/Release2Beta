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

package com.github.dirtpowered.releasetobeta.network.translator.moderntobeta.B_1_8;

import com.github.dirtpowered.betaprotocollib.data.BetaItemStack;
import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.BlockPlacePacketData;
import com.github.dirtpowered.releasetobeta.data.item.ItemFood;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.ModernToBeta;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerRotationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerUseItemPacket;
import com.github.steveice10.packetlib.Session;

public class ClientPlayerUseItemTranslator implements ModernToBeta<ClientPlayerUseItemPacket> {

    @Override
    public void translate(ClientPlayerUseItemPacket packet, Session modernSession, BetaClientSession betaSession) {
        betaSession.sendPacket(new BlockPlacePacketData(-1, -1, -1, -1, new BetaItemStack()));

        /*
         * Looks bad, but beta server needs movement packets more frequently.
         * Beta client behaves similarly - sends up to ~30 movement packets while eating.
         */
        if (ItemFood.isFoodItem(betaSession.getPlayer().getInventory().getItemInHand().getId())) {
            for (int i = 0; i < 28; i++) {
                betaSession.getMain().getServer().getServerConnection().UNSAFE_addPacketToQueue(modernSession, new ClientPlayerRotationPacket(true, 0, 0));
            }
        }
    }
}
