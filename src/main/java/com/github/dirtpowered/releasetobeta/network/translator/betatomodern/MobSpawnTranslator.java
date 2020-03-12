package com.github.dirtpowered.releasetobeta.network.translator.betatomodern;

import com.github.dirtpowered.betaprotocollib.packet.data.MobSpawnPacketData;
import com.github.dirtpowered.releasetobeta.data.entity.DummyEntity;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.dirtpowered.releasetobeta.utils.Utils;
import com.github.steveice10.mc.protocol.data.MagicValues;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.EntityMetadata;
import com.github.steveice10.mc.protocol.data.game.entity.type.MobType;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnMobPacket;
import com.github.steveice10.packetlib.Session;

import java.util.UUID;

public class MobSpawnTranslator implements BetaToModern<MobSpawnPacketData> {

    @Override
    public void translate(MobSpawnPacketData packet, BetaClientSession session, Session modernSession) {
        int entityId = packet.getEntityId();
        UUID uuid = UUID.randomUUID();

        double x = Utils.toModernPos(packet.getX());
        double y = Utils.toModernPos(packet.getY());
        double z = Utils.toModernPos(packet.getZ());

        float yaw = packet.getYaw();
        float pitch = packet.getPitch();
        MobType type = MagicValues.key(MobType.class, packet.getType());

        session.getMain().getEntityCache().addEntity(entityId, new DummyEntity(entityId, type));
        modernSession.send(new ServerSpawnMobPacket(entityId, uuid, type, x, y, z, yaw,
                pitch, yaw, 0, 0, 0, new EntityMetadata[0]));
    }
}
