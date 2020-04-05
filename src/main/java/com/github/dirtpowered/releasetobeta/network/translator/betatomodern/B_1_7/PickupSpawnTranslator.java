package com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_7;

import com.github.dirtpowered.betaprotocollib.data.BetaItemStack;
import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.PickupSpawnPacketData;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.dirtpowered.releasetobeta.utils.Utils;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.EntityMetadata;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.MetadataType;
import com.github.steveice10.mc.protocol.data.game.entity.type.object.ObjectType;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityMetadataPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnObjectPacket;
import com.github.steveice10.packetlib.Session;

import java.util.Arrays;
import java.util.UUID;

public class PickupSpawnTranslator implements BetaToModern<PickupSpawnPacketData> {

    @Override
    public void translate(PickupSpawnPacketData packet, BetaClientSession session, Session modernSession) {
        int entityId = packet.getEntityId();
        UUID uuid = UUID.randomUUID();

        double x = Utils.toModernPos(packet.getX());
        double y = Utils.toModernPos(packet.getY());
        double z = Utils.toModernPos(packet.getZ());

        float yaw = packet.getYaw();
        float pitch = packet.getPitch();

        BetaItemStack itemStack = packet.getItemStack();
        itemStack.setBlockId(session.remapBlock(itemStack.getBlockId()));

        EntityMetadata[] metadata = Arrays.asList(
                new EntityMetadata(6, MetadataType.ITEM, Utils.betaItemStackToItemStack(itemStack)),
                new EntityMetadata(4, MetadataType.BOOLEAN, false),
                new EntityMetadata(1, MetadataType.INT, 300),
                new EntityMetadata(3, MetadataType.BOOLEAN, false),
                new EntityMetadata(5, MetadataType.BOOLEAN, false)
        ).toArray(new EntityMetadata[0]);

        modernSession.send(new ServerSpawnObjectPacket(entityId, uuid, ObjectType.ITEM, x, y, z, yaw, pitch));
        modernSession.send(new ServerEntityMetadataPacket(entityId, metadata));
    }
}
