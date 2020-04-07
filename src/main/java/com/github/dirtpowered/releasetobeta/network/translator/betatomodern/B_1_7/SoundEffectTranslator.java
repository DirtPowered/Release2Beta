package com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_7;

import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.SoundEffectPacketData;
import com.github.dirtpowered.releasetobeta.data.mapping.SoundEffectMap;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.dirtpowered.releasetobeta.utils.Utils;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.Position;
import com.github.steveice10.mc.protocol.data.game.world.block.BlockState;
import com.github.steveice10.mc.protocol.data.game.world.effect.BreakBlockEffectData;
import com.github.steveice10.mc.protocol.data.game.world.effect.ParticleEffect;
import com.github.steveice10.mc.protocol.data.game.world.effect.RecordEffectData;
import com.github.steveice10.mc.protocol.data.game.world.effect.SoundEffect;
import com.github.steveice10.mc.protocol.data.game.world.effect.WorldEffect;
import com.github.steveice10.mc.protocol.data.game.world.effect.WorldEffectData;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerPlayEffectPacket;
import com.github.steveice10.packetlib.Session;

public class SoundEffectTranslator implements BetaToModern<SoundEffectPacketData> {

    @Override
    public void translate(SoundEffectPacketData packet, BetaClientSession session, Session modernSession) {
        Utils.debug(packet);
        SoundEffectMap soundEffectMap = session.getMain().getSoundEffectMap();

        int id = packet.getSoundType();
        int data = packet.getData();

        Position pos = new Position(packet.getX(), packet.getY(), packet.getZ());
        WorldEffect worldEffect = soundEffectMap.getFromId(id);

        if (worldEffect instanceof SoundEffect) {
            if (worldEffect == SoundEffect.RECORD) {
                modernSession.send(new ServerPlayEffectPacket(worldEffect, pos, new RecordEffectData(data) {

                }));
                return;
            }

            modernSession.send(new ServerPlayEffectPacket(worldEffect, pos, new WorldEffectData() {

            }));
        } else if (worldEffect instanceof ParticleEffect) {
            modernSession.send(new ServerPlayEffectPacket(worldEffect, pos, new BreakBlockEffectData(new BlockState(data, 0))));
        }
    }
}
