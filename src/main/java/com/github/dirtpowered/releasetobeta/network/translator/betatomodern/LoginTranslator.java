package com.github.dirtpowered.releasetobeta.network.translator.betatomodern;

import com.github.dirtpowered.betaprotocollib.packet.data.LoginPacketData;
import com.github.dirtpowered.releasetobeta.configuration.R2BConfiguration;
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

        session.setProtocolState(ProtocolState.PLAY);
        modernSession.send(new ServerJoinGamePacket(
                entityId,
                false,
                GameMode.SURVIVAL,
                dimension,
                Difficulty.EASY,
                R2BConfiguration.maxPlayers,
                WorldType.DEFAULT,
                false
        ));

        player.setEntityId(entityId);
        player.setDimension(dimension);

        session.joinPlayer();
    }
}
