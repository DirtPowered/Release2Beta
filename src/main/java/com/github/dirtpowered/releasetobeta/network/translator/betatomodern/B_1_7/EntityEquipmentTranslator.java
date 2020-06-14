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

package com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_7;

import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.EntityEquipmentPacketData;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.steveice10.mc.protocol.data.game.entity.EquipmentSlot;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.ItemStack;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityEquipmentPacket;
import com.github.steveice10.packetlib.Session;

public class EntityEquipmentTranslator implements BetaToModern<EntityEquipmentPacketData> {

    @Override
    public void translate(EntityEquipmentPacketData packet, BetaClientSession session, Session modernSession) {
        int entityId = packet.getEntityId();
        int itemId = session.remapBlock(packet.getItemId(), packet.getItemData(), false);
        int data = session.remapMetadata(packet.getItemId(), packet.getItemData(), itemId == 54);

        if (itemId == -1) itemId = 0;
        ItemStack itemStack = new ItemStack(itemId, 1, data);

        int slot = packet.getSlot();
        EquipmentSlot equipmentSlot;

        switch (slot) {
            case 0:
                equipmentSlot = EquipmentSlot.MAIN_HAND;
                break;
            case 1:
                equipmentSlot = EquipmentSlot.BOOTS;
                break;
            case 2:
                equipmentSlot = EquipmentSlot.LEGGINGS;
                break;
            case 3:
                equipmentSlot = EquipmentSlot.CHESTPLATE;
                break;
            case 4:
                equipmentSlot = EquipmentSlot.HELMET;
                break;
            default:
                equipmentSlot = null;
                break;
        }

        if (equipmentSlot == null)
            return;

        modernSession.send(new ServerEntityEquipmentPacket(entityId, equipmentSlot, itemStack));
    }
}
