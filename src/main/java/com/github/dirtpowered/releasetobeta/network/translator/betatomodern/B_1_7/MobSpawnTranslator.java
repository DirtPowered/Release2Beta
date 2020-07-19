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

import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.MobSpawnPacketData;
import com.github.dirtpowered.betaprotocollib.utils.Location;
import com.github.dirtpowered.releasetobeta.ReleaseToBeta;
import com.github.dirtpowered.releasetobeta.data.entity.model.Entity;
import com.github.dirtpowered.releasetobeta.data.mapping.StaticValues;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.dirtpowered.releasetobeta.utils.Utils;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.EntityMetadata;
import com.github.steveice10.mc.protocol.data.game.entity.type.MobType;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityMetadataPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnMobPacket;
import com.github.steveice10.packetlib.Session;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

public class MobSpawnTranslator implements BetaToModern<MobSpawnPacketData> {

    @Override
    public void translate(ReleaseToBeta main, MobSpawnPacketData packet, BetaClientSession session, Session modernSession) {
        int entityId = packet.getEntityId();
        UUID uuid = UUID.randomUUID();

        double x = Utils.toModernPos(packet.getX());
        double y = Utils.toModernPos(packet.getY());
        double z = Utils.toModernPos(packet.getZ());

        float yaw = packet.getYaw();
        float pitch = packet.getPitch();

        MobType type = StaticValues.getMobType((int) packet.getType());

        try {
            Class<? extends Entity> c = main.getServer().getEntityRegistry().getEntityFromMobType(type);
            Constructor<? extends Entity> cons = c.getDeclaredConstructor(int.class);

            Entity object = cons.newInstance(entityId);

            object.setLocation(new Location(x, y, z, yaw, pitch));
            session.getEntityCache().addEntity(entityId, object);

            EntityMetadata[] metadata = main.getServer().getMetadataTranslator().toModernMetadata(session.getPlayer(), modernSession, object, packet.getMetadata());

            modernSession.send(new ServerSpawnMobPacket(entityId, uuid, type, x, y, z, yaw, pitch, yaw, 0, 0, 0));
            modernSession.send(new ServerEntityMetadataPacket(entityId, metadata));

            //Logger.info("spawning {} [entityId={}]", c.getSimpleName(), entityId);
            object.onSpawn(modernSession);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
