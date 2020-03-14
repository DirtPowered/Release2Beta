package com.github.dirtpowered.releasetobeta.network.translator.betatomodern;

import com.github.dirtpowered.betaprotocollib.packet.data.EntityDestroyPacketData;
import com.github.dirtpowered.releasetobeta.data.entity.model.Entity;
import com.github.dirtpowered.releasetobeta.data.entity.model.Mob;
import com.github.dirtpowered.releasetobeta.data.player.BetaPlayer;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityDestroyPacket;
import com.github.steveice10.packetlib.Session;
import org.pmw.tinylog.Logger;

public class EntityDestroyTranslator implements BetaToModern<EntityDestroyPacketData> {

    @Override
    public void translate(EntityDestroyPacketData packet, BetaClientSession session, Session modernSession) {
        int entityId = packet.getEntityId();
        Entity e = (session.getMain().getEntityCache().getEntityById(entityId));
        if (e != null) {
            if (e instanceof Mob) {
                Mob mob = (Mob) e;
                mob.onDeath(modernSession);
            } else if (e instanceof BetaPlayer) {
                session.getMain().getServer().removeBetaTabEntry((BetaPlayer) e);
                Logger.info("removing beta player: {}/eid={}", ((BetaPlayer) e).getTabEntry().getProfile().getName(), entityId);
            }
        }

        session.getMain().getEntityCache().removeEntity(entityId);
        modernSession.send(new ServerEntityDestroyPacket(entityId));
    }
}
