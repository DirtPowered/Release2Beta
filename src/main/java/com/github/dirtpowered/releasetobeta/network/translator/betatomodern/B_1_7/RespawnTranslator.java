package com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_7;

import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.RespawnPacketData;
import com.github.dirtpowered.releasetobeta.data.player.ModernPlayer;
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
        ModernPlayer player = session.getPlayer();

        int dimension = packet.getDimension();
        player.setDimension(dimension);

        modernSession.send(new ServerRespawnPacket(dimension, Difficulty.EASY, GameMode.SURVIVAL, WorldType.DEFAULT));
    }
}
