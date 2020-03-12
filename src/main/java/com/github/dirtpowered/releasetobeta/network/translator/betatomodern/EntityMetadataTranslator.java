package com.github.dirtpowered.releasetobeta.network.translator.betatomodern;

import com.github.dirtpowered.betaprotocollib.data.WatchableObject;
import com.github.dirtpowered.betaprotocollib.packet.data.EntityMetadataPacketData;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.dirtpowered.releasetobeta.utils.Utils;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.EntityMetadata;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.MetadataType;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityMetadataPacket;
import com.github.steveice10.packetlib.Session;

import java.util.ArrayList;
import java.util.List;

public class EntityMetadataTranslator implements BetaToModern<EntityMetadataPacketData> {

    @Override
    public void translate(EntityMetadataPacketData packet, BetaClientSession session, Session modernSession) {
        Utils.debug(packet);
        int entityId = packet.getEntityId();
        List<EntityMetadata> metadataList = new ArrayList<>();

        for (WatchableObject watchableObject : packet.getMetadata()) {
            MetadataType type = IntegerToDataType(watchableObject.getType());
            int index = watchableObject.getIndex();
            Object value = watchableObject.getValue();

            if (type == MetadataType.BYTE && index == 0) {
                //sneaking
                metadataList.add(new EntityMetadata(0, MetadataType.BYTE, value));
            }

            //Creeper fuse:
            // [entityId=11023,metadata=
            // [WatchableObject [type=0, index=0, value=0],
            // WatchableObject [type=0, index=16, value=1],
            // WatchableObject [type=0, index=17, value=0]]]

            //Sheep color:
            // [entityId=11764,metadata=
            // WatchableObject [type=0, index=16, value=15]]]
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
