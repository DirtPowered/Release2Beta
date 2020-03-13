package com.github.dirtpowered.releasetobeta.network.translator.betatomodern;

import com.github.dirtpowered.betaprotocollib.packet.data.RespawnPacketData;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.steveice10.mc.protocol.data.game.entity.player.GameMode;
import com.github.steveice10.mc.protocol.data.game.setting.Difficulty;
import com.github.steveice10.mc.protocol.data.game.world.WorldType;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerRespawnPacket;
import com.github.steveice10.packetlib.Session;

public class RespawnTranslator implements BetaToModern<RespawnPacketData> {

    @Override
    public void translate(RespawnPacketData packet, BetaClientSession session, Session modernSession) {
        int dimension = packet.getDimension();

        modernSession.send(new ServerRespawnPacket(dimension, Difficulty.EASY, GameMode.SURVIVAL, WorldType.DEFAULT));
    }
}
