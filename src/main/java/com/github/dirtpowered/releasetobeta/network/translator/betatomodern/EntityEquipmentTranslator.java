package com.github.dirtpowered.releasetobeta.network.translator.betatomodern;

import com.github.dirtpowered.betaprotocollib.data.BetaItemStack;
import com.github.dirtpowered.betaprotocollib.packet.data.EntityEquipmentPacketData;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.dirtpowered.releasetobeta.utils.Utils;
import com.github.steveice10.mc.protocol.data.game.entity.EquipmentSlot;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.ItemStack;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityEquipmentPacket;
import com.github.steveice10.packetlib.Session;

public class EntityEquipmentTranslator implements BetaToModern<EntityEquipmentPacketData> {

    @Override
    public void translate(EntityEquipmentPacketData packet, BetaClientSession session, Session modernSession) {
        Utils.debug(packet);
        int entityId = packet.getEntityId();
        int itemId = packet.getItemId();

        if (itemId == -1) itemId = 0;
        ItemStack itemStack = new ItemStack(itemId, 1, packet.getItemData());

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
