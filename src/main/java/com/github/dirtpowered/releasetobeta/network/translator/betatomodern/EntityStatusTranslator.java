package com.github.dirtpowered.releasetobeta.network.translator.betatomodern;

import com.github.dirtpowered.betaprotocollib.packet.data.EntityStatusPacketData;
import com.github.dirtpowered.releasetobeta.data.entity.EntityCache;
import com.github.dirtpowered.releasetobeta.data.entity.model.Entity;
import com.github.dirtpowered.releasetobeta.data.entity.model.Mob;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.steveice10.mc.protocol.data.game.entity.EntityStatus;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityStatusPacket;
import com.github.steveice10.packetlib.Session;

public class EntityStatusTranslator implements BetaToModern<EntityStatusPacketData> {

    @Override
    public void translate(EntityStatusPacketData packet, BetaClientSession session, Session modernSession) {
        int entityId = packet.getEntityId();
        int status = packet.getStatus();
        EntityStatus entityStatus;

        /*
         * 2 	Entity hurt
         * 3 	Entity dead
         * 6 	Wolf taming
         * 7 	Wolf tamed
         * 8 	Wolf shaking water off itself
         */

        switch (status) {
            case 2:
                entityStatus = EntityStatus.LIVING_HURT;
                onDamage(entityId, session.getEntityCache(), modernSession);
                break;
            case 3:
                entityStatus = EntityStatus.LIVING_DEATH;
                break;
            case 6:
                entityStatus = EntityStatus.TAMEABLE_TAMING_FAILED;
                break;
            case 7:
                entityStatus = EntityStatus.TAMEABLE_TAMING_SUCCEEDED;
                break;
            case 8:
                entityStatus = EntityStatus.WOLF_SHAKE_WATER;
                break;
            default:
                entityStatus = EntityStatus.LIVING_HURT;
                break;
        }

        modernSession.send(new ServerEntityStatusPacket(entityId, entityStatus));
    }

    private void onDamage(int entityId, EntityCache entityCache, Session session) {
        Entity e = entityCache.getEntityById(entityId);
        if (e != null) {
            if (e instanceof Mob) {
                Mob mob = (Mob) e;
                mob.onDamage(session);
            }
        }
    }
}
