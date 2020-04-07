package com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_8;

import com.github.dirtpowered.betaprotocollib.packet.Version_B1_8.data.RemoveEntityEffectPacketData;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.dirtpowered.releasetobeta.utils.Utils;
import com.github.steveice10.mc.protocol.data.game.entity.Effect;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityRemoveEffectPacket;
import com.github.steveice10.packetlib.Session;

public class RemoveEntityEffectTranslator implements BetaToModern<RemoveEntityEffectPacketData> {

    @Override
    public void translate(RemoveEntityEffectPacketData packet, BetaClientSession session, Session modernSession) {
        Utils.debug(packet);
        int entityId = packet.getEntityId();
        Effect effect = session.getMain().getEntityEffectMap().getFromId(packet.getEffectId());

        modernSession.send(new ServerEntityRemoveEffectPacket(entityId, effect));
    }
}
