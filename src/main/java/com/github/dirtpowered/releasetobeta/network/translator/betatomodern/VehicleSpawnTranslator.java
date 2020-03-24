package com.github.dirtpowered.releasetobeta.network.translator.betatomodern;

import com.github.dirtpowered.betaprotocollib.packet.data.VehicleSpawnPacketData;
import com.github.dirtpowered.releasetobeta.configuration.R2BConfiguration;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.dirtpowered.releasetobeta.utils.Utils;
import com.github.steveice10.mc.protocol.data.game.entity.type.object.FallingBlockData;
import com.github.steveice10.mc.protocol.data.game.entity.type.object.MinecartType;
import com.github.steveice10.mc.protocol.data.game.entity.type.object.ObjectData;
import com.github.steveice10.mc.protocol.data.game.entity.type.object.ObjectType;
import com.github.steveice10.mc.protocol.data.game.entity.type.object.ProjectileData;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityVelocityPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnObjectPacket;
import com.github.steveice10.packetlib.Session;

import java.util.UUID;

public class VehicleSpawnTranslator implements BetaToModern<VehicleSpawnPacketData> {

    @Override
    public void translate(VehicleSpawnPacketData packet, BetaClientSession session, Session modernSession) {
        int ownerId = session.getPlayer().getEntityId();
        int entityId = packet.getEntityId();
        int hasVelocity = packet.getVelocity();
        UUID uuid = UUID.randomUUID();

        ObjectType type = null;
        ObjectData data = null;

        double x = Utils.toModernPos(packet.getX());
        double y = Utils.toModernPos(packet.getY());
        double z = Utils.toModernPos(packet.getZ());

        double vecX = 0;
        double vecY = 0;
        double vecZ = 0;

        if (hasVelocity > 0) {
            vecX = packet.getVelocityX() / 8000.0D;
            vecY = packet.getVelocityY() / 8000.0D;
            vecZ = packet.getVelocityZ() / 8000.0D;
        }

        if (packet.getType() == 10) {
            type = ObjectType.MINECART;
            data = MinecartType.NORMAL;
        } else if (packet.getType() == 11) {
            type = ObjectType.MINECART;
            data = MinecartType.CHEST;
        } else if (packet.getType() == 12) {
            type = ObjectType.MINECART;
            data = MinecartType.POWERED;
        } else if (packet.getType() == 60) {
            type = R2BConfiguration.arrowFix ? ObjectType.SNOWBALL : ObjectType.TIPPED_ARROW;
            data = new ProjectileData(ownerId);
        } else if (packet.getType() == 50) {
            type = ObjectType.PRIMED_TNT;
        } else if (packet.getType() == 61) {
            type = ObjectType.SNOWBALL;
            data = new ProjectileData(ownerId);
        } else if (packet.getType() == 62) {
            type = ObjectType.EGG;
            data = new ProjectileData(ownerId);
        } else if (packet.getType() == 90) {
            type = ObjectType.FISH_HOOK;
        } else if (packet.getType() == 1) {
            type = ObjectType.BOAT;
        } else if (packet.getType() == 63) {
            type = ObjectType.GHAST_FIREBALL;
        } else if (packet.getType() == 70) {
            type = ObjectType.FALLING_BLOCK;
            data = new FallingBlockData(12, 0);
        } else if (packet.getType() == 71) {
            type = ObjectType.FALLING_BLOCK;
            data = new FallingBlockData(13, 0);
        }

        if (type == null) //server sends weird IDs sometimes
            return;

        modernSession.send(new ServerSpawnObjectPacket(entityId, uuid, type, data, x, y, z, 0, 0));
        modernSession.send(new ServerEntityVelocityPacket(entityId, vecX, vecY, vecZ));
    }
}
