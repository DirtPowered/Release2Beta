package com.github.dirtpowered.releasetobeta.network.translator.betatomodern;

import com.github.dirtpowered.betaprotocollib.packet.data.NamedEntitySpawnPacketData;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.dirtpowered.releasetobeta.utils.Utils;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.EntityMetadata;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnPlayerPacket;
import com.github.steveice10.packetlib.Session;

import java.util.UUID;

public class NamedEntitySpawnTranslator implements BetaToModern<NamedEntitySpawnPacketData> {

    @Override
    public void translate(NamedEntitySpawnPacketData packet, BetaClientSession session, Session modernSession) {
        Utils.debug(packet);

        int entityId = packet.getEntityId();
        UUID uuid = session.getMain().getServer().getUUIDFromUsername(packet.getName());

        double x = Utils.toModernPos(packet.getX());
        double y = Utils.toModernPos(packet.getY());
        double z = Utils.toModernPos(packet.getZ());

        float yaw = packet.getRotation();
        float pitch = packet.getPitch();

        modernSession.send(new ServerSpawnPlayerPacket(entityId, uuid, x, y, z, yaw, pitch, new EntityMetadata[0]));
    }
}
