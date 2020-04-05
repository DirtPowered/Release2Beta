package com.github.dirtpowered.releasetobeta.network.translator.betatomodern;

import com.github.dirtpowered.betaprotocollib.packet.data.NamedEntitySpawnPacketData;
import com.github.dirtpowered.releasetobeta.data.player.BetaPlayer;
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
        int entityId = packet.getEntityId();
        String username = packet.getName();

        double x = Utils.toModernPos(packet.getX());
        double y = Utils.toModernPos(packet.getY());
        double z = Utils.toModernPos(packet.getZ());

        float yaw = Utils.toModernYaw(packet.getRotation());
        float pitch = Utils.toModernPitch(packet.getPitch());
        UUID uuid = session.getMain().getServer().getServerConnection().getPlayerList().getUUIDFromUsername(username);

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

        spawn(modernSession, entityId, uuid, x, y, z, yaw, pitch);
    }

    private void spawn(Session session, int entityId, UUID uuid, double x, double y, double z, float yaw, float pitch) {
        session.send(new ServerSpawnPlayerPacket(entityId, uuid, x, y, z, yaw, pitch, new EntityMetadata[0]));
    }
}
