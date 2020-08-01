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

import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.NamedEntitySpawnPacketData;
import com.github.dirtpowered.releasetobeta.ReleaseToBeta;
import com.github.dirtpowered.releasetobeta.data.player.BetaPlayer;
import com.github.dirtpowered.releasetobeta.data.player.ModernPlayer;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.dirtpowered.releasetobeta.utils.Utils;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.EntityMetadata;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnPlayerPacket;
import com.github.steveice10.packetlib.Session;

import java.util.UUID;

public class NamedEntitySpawnTranslator implements BetaToModern<NamedEntitySpawnPacketData> {

    @Override
    public void translate(ReleaseToBeta main, NamedEntitySpawnPacketData packet, BetaClientSession session, Session modernSession) {
        int entityId = packet.getEntityId();
        String username = packet.getName();

        double x = Utils.toModernPos(packet.getX());
        double y = Utils.toModernPos(packet.getY());
        double z = Utils.toModernPos(packet.getZ());

        float yaw = Utils.toModernYaw(packet.getRotation());
        float pitch = Utils.toModernPitch(packet.getPitch());
        UUID uuid = main.getServer().getServerConnection().getPlayerList().getUUIDFromUsername(username);

        if (uuid == null) {
            //spawn players using beta client too
            new BetaPlayer(session, username, entityId, completePlayer -> {
                session.addBetaTabEntry(completePlayer);
                completePlayer.onSpawn(modernSession);

                session.getEntityCache().addEntity(entityId, completePlayer);
                spawn(modernSession, entityId, completePlayer.getUUID(), x, y, z, yaw, pitch);
            });

            return;
        }

        ModernPlayer player = main.getServer().getPlayer(entityId);

        if (player != null) {
            player.onSpawn(modernSession);
            spawn(modernSession, entityId, uuid, x, y, z, yaw, pitch);

            session.getEntityCache().addEntity(entityId, player);
        }
    }

    private void spawn(Session session, int entityId, UUID uuid, double x, double y, double z, float yaw, float pitch) {
        session.send(new ServerSpawnPlayerPacket(entityId, uuid, x, y, z, yaw, pitch, new EntityMetadata[0]));
    }
}
