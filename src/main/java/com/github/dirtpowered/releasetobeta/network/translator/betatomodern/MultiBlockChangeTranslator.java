package com.github.dirtpowered.releasetobeta.network.translator.betatomodern;

import com.github.dirtpowered.betaprotocollib.packet.data.MultiBlockChangePacketData;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.steveice10.packetlib.Session;

public class MultiBlockChangeTranslator implements BetaToModern<MultiBlockChangePacketData> {

    //[
    //  x=18
    //  z=11
    //  coordinateArray={28992,28993,28994,28995,28996,28997}
    //  typeArray={0,0,0,0,0,0}
    //  metadataArray={0,0,0,0,0,0}
    //  size=6
    //]

    // top 4 bits is X, next 4 bits is Z, bottom 8 bits is Y

    @Override
    public void translate(MultiBlockChangePacketData packet, BetaClientSession session, Session modernSession) {
        int size = packet.getSize();
        short[] coordinateArray = packet.getCoordinateArray();
        int chunkX = packet.getX();
        int chunkZ = packet.getZ();

        //Logger.info("data: {}", ReflectionToStringBuilder.toString(packet, ToStringStyle.MULTI_LINE_STYLE));
        for (int index = 0; index < size; ++index) {
            int blockY = coordinateArray[index] & 255;
            //help?
        }
        //BlockChangeRecord record = new BlockChangeRecord()
        //modernSession.send(new ServerMultiBlockChangePacket());
    }
}
