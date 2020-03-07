package com.github.dirtpowered.releasetobeta.network.translator.betatomodern;

import com.github.dirtpowered.betaprotocollib.packet.data.UpdateTimePacketData;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerUpdateTimePacket;
import com.github.steveice10.packetlib.Session;

public class UpdateTimeTranslator implements BetaToModern<UpdateTimePacketData> {

    @Override
    public void translate(UpdateTimePacketData packet, BetaClientSession session, Session modernSession) {
        long time = packet.getTime();

        modernSession.send(new ServerUpdateTimePacket(time, time));
    }
}
