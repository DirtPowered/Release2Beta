package com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_7;

import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.MapDataPacketData;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.dirtpowered.releasetobeta.utils.Utils;
import com.github.steveice10.mc.protocol.data.game.world.map.MapIcon;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerMapDataPacket;
import com.github.steveice10.packetlib.Session;

public class MapDataTranslator implements BetaToModern<MapDataPacketData> {

    @Override
    public void translate(MapDataPacketData packet, BetaClientSession session, Session modernSession) {
        Utils.debug(packet);

        int mapId = packet.getMapId();
        //TODO: Translate old data to new format
        modernSession.send(new ServerMapDataPacket(mapId, (byte) 0, true, new MapIcon[0], null));
    }
}
