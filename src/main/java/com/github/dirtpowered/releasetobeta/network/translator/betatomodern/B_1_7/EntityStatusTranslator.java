/*
 * Copyright (c) 2020 Dirt Powered
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_7;

import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.EntityStatusPacketData;
import com.github.dirtpowered.betaprotocollib.utils.Location;
import com.github.dirtpowered.releasetobeta.data.Constants;
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
                onDamage(entityId, session.getEntityCache(), modernSession, session.getPlayer().getLocation());
                break;
            case 3:
                entityStatus = EntityStatus.LIVING_DEATH;
                Entity e = (session.getEntityCache().getEntityById(entityId));

                if (e != null) {
                    if (e instanceof Mob) {
                        double dist = session.getPlayer().getLocation().distanceTo(e.getLocation());
                        if (dist < Constants.SOUND_RANGE) {
                            ((Mob) e).onDeath(modernSession);
                        }
                    }
                }
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
            case 9:
                //this status belongs to b1.8+
                //TODO: Move it to own class
                entityStatus = EntityStatus.PLAYER_FINISH_USING_ITEM;
                break;
            default:
                entityStatus = EntityStatus.LIVING_HURT;
                break;
        }

        modernSession.send(new ServerEntityStatusPacket(entityId, entityStatus));
    }

    private void onDamage(int entityId, EntityCache entityCache, Session session, Location l) {
        Entity e = entityCache.getEntityById(entityId);
        if (e != null) {
            if (e instanceof Mob) {
                Mob mob = (Mob) e;
                double dist = l.distanceTo(e.getLocation());
                if (dist < Constants.SOUND_RANGE) {
                    mob.onDamage(session);
                }
            }
        }
    }
}
