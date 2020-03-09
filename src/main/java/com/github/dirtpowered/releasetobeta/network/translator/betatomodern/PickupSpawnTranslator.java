package com.github.dirtpowered.releasetobeta.network.translator.betatomodern;

import com.github.dirtpowered.betaprotocollib.data.BetaItemStack;
import com.github.dirtpowered.betaprotocollib.packet.data.PickupSpawnPacketData;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.dirtpowered.releasetobeta.utils.Utils;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.EntityMetadata;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.MetadataType;
import com.github.steveice10.mc.protocol.data.game.entity.type.object.ObjectType;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityMetadataPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnObjectPacket;
import com.github.steveice10.packetlib.Session;

import java.util.UUID;

public class PickupSpawnTranslator implements BetaToModern<PickupSpawnPacketData> {

    @Override
    public void translate(PickupSpawnPacketData packet, BetaClientSession session, Session modernSession) {
        Utils.debug(packet);
        int entityId = packet.getEntityId();
        UUID uuid = UUID.randomUUID();

        double x = Utils.toModernPos(packet.getX());
        double y = Utils.toModernPos(packet.getY());
        double z = Utils.toModernPos(packet.getZ());

        float yaw = packet.getYaw();
        float pitch = packet.getPitch();

        BetaItemStack itemStack = packet.getItemStack();
        EntityMetadata metadata = new EntityMetadata(5, MetadataType.ITEM, Utils.betaItemStackToItemStack(itemStack));

        //modernSession.send(new ServerSpawnObjectPacket(entityId, uuid, ObjectType.ITEM, x, y, z, yaw, pitch));
       // modernSession.send(new ServerEntityMetadataPacket(entityId, new EntityMetadata[] { metadata }));
    }
}
