package com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_8;

import com.github.dirtpowered.betaprotocollib.packet.Version_B1_8.data.EntityEffectPacketData;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.dirtpowered.releasetobeta.utils.Utils;
import com.github.steveice10.mc.protocol.data.game.entity.Effect;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityEffectPacket;
import com.github.steveice10.packetlib.Session;

public class EntityEffectTranslator implements BetaToModern<EntityEffectPacketData> {

    @Override
    public void translate(EntityEffectPacketData packet, BetaClientSession session, Session modernSession) {
        Utils.debug(packet);
        int entityId = packet.getEntityId();
        int duration = packet.getDuration();
        int amplifier = packet.getAmplifier();

        Effect effect = session.getMain().getEntityEffectMap().getFromId(packet.getEffectId());

        modernSession.send(new ServerEntityEffectPacket(entityId, effect, amplifier, duration, false, true));
    }
}
