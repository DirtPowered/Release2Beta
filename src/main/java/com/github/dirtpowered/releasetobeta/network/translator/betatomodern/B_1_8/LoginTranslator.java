package com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_8;

import com.github.dirtpowered.betaprotocollib.packet.Version_B1_8.data.LoginPacketData;
import com.github.dirtpowered.releasetobeta.data.ProtocolState;
import com.github.dirtpowered.releasetobeta.data.player.ModernPlayer;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.steveice10.mc.protocol.data.game.entity.player.GameMode;
import com.github.steveice10.mc.protocol.data.game.setting.Difficulty;
import com.github.steveice10.mc.protocol.data.game.world.WorldType;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerJoinGamePacket;
import com.github.steveice10.packetlib.Session;

public class LoginTranslator implements BetaToModern<LoginPacketData> {

    @Override
    public void translate(LoginPacketData packet, BetaClientSession session, Session modernSession) {
        ModernPlayer player = session.getPlayer();
        int entityId = packet.getEntityId();
        int dimension = packet.getDimension();
        int maxPlayers = packet.getMaxPlayers();
        GameMode gameMode = packet.getGamemode() == 0 ? GameMode.SURVIVAL : GameMode.CREATIVE;
        Difficulty difficulty;

        switch (packet.getDifficulty()) {
            case 0:
                difficulty = Difficulty.PEACEFUL;
                break;
            case 1:
                difficulty = Difficulty.EASY;
                break;
            case 2:
                difficulty = Difficulty.NORMAL;
                break;
            case 3:
                difficulty = Difficulty.HARD;
                break;
            default:
                difficulty = Difficulty.NORMAL;
                break;
        }

        session.setProtocolState(ProtocolState.PLAY);
        modernSession.send(new ServerJoinGamePacket(
                entityId,
                false,
                gameMode,
                dimension,
                difficulty,
                maxPlayers,
                WorldType.DEFAULT,
                false
        ));

        player.setEntityId(entityId);
        player.setDimension(dimension);
        player.setDifficulty(packet.getDifficulty());
        player.setGamemode(packet.getGamemode());
        player.setWorldHeight(packet.getWorldHeight());
        player.setSeed(packet.getSeed());
    }
}
