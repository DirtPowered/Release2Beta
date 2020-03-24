package com.github.dirtpowered.releasetobeta.network.translator.betatomodern;

import com.github.dirtpowered.betaprotocollib.data.WatchableObject;
import com.github.dirtpowered.betaprotocollib.packet.data.EntityMetadataPacketData;
import com.github.dirtpowered.releasetobeta.data.entity.model.Entity;
import com.github.dirtpowered.releasetobeta.data.entity.monster.EntityCreeper;
import com.github.dirtpowered.releasetobeta.data.player.BetaPlayer;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.dirtpowered.releasetobeta.utils.Utils;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.EntityMetadata;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.MetadataType;
import com.github.steveice10.mc.protocol.data.game.entity.type.MobType;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityMetadataPacket;
import com.github.steveice10.packetlib.Session;

import java.util.ArrayList;
import java.util.List;

public class EntityMetadataTranslator implements BetaToModern<EntityMetadataPacketData> {

    @Override
    public void translate(EntityMetadataPacketData packet, BetaClientSession session, Session modernSession) {
        Utils.debug(packet);
        int entityId = packet.getEntityId();
        Entity e = session.getEntityCache().getEntityById(entityId);
        MobType mobType = null;

        if (e != null)
            mobType = e.getMobType();

        List<EntityMetadata> metadataList = new ArrayList<>();

        for (WatchableObject watchableObject : packet.getMetadata()) {
            MetadataType type = IntegerToDataType(watchableObject.getType());
            int index = watchableObject.getIndex();
            Object value = watchableObject.getValue();

            if (type == MetadataType.BYTE && index == 0) {
                //sneaking
                metadataList.add(new EntityMetadata(0, MetadataType.BYTE, value));
            }

            if (!(e instanceof BetaPlayer)) {

                if (type == MetadataType.BYTE && index == 16) {
                    //sheep color
                    if (mobType == MobType.SHEEP) {
                        metadataList.add(new EntityMetadata(13, MetadataType.BYTE, value));
                    } else if (mobType == MobType.CREEPER) {
                        //creeper fuse
                        Byte b = (Byte) value;
                        if (e instanceof EntityCreeper)
                            ((EntityCreeper) e).onPrime(modernSession);

                        metadataList.add(new EntityMetadata(12, MetadataType.INT, b.intValue()));
                    } else if (mobType == MobType.WOLF) {
                        metadataList.add(new EntityMetadata(13, MetadataType.BYTE, value));
                    } else if (mobType == MobType.PIG) {
                        boolean hasSaddle = ((Byte) value).intValue() == 1;

                        metadataList.add(new EntityMetadata(13, MetadataType.BOOLEAN, hasSaddle));
                    } else if (mobType == MobType.SLIME) {
                        Byte b = (Byte) value;

                        metadataList.add(new EntityMetadata(12, MetadataType.INT, b.intValue()));
                    } else if (mobType == MobType.GHAST) {
                        boolean isAggressive = ((Byte) value).intValue() == 1;

                        metadataList.add(new EntityMetadata(12, MetadataType.BOOLEAN, isAggressive));
                    }
                }

                if (type == MetadataType.BYTE && index == 17) {
                    if (mobType == MobType.CREEPER) {
                        //is powered
                        boolean isPowered = ((Byte) value).intValue() == 1;
                        metadataList.add(new EntityMetadata(13, MetadataType.BOOLEAN, isPowered));
                    }
                }
            }
        }

        modernSession.send(new ServerEntityMetadataPacket(entityId, metadataList.toArray(new EntityMetadata[0])));
    }

    private MetadataType IntegerToDataType(int type) {
        MetadataType metadataType;

        switch (type) {
            case 0:
                metadataType = MetadataType.BYTE;
                break;
            case 2:
                metadataType = MetadataType.INT;
                break;
            case 3:
                metadataType = MetadataType.FLOAT;
                break;
            case 4:
                metadataType = MetadataType.STRING;
                break;
            case 5:
                metadataType = MetadataType.ITEM;
                break;
            case 6:
                metadataType = MetadataType.POSITION;
                break;
            default:
                metadataType = null;
                break;
        }
        return metadataType;
    }
}
