package com.github.dirtpowered.releasetobeta.network.translator.moderntobeta.B_1_8;

import com.github.dirtpowered.betaprotocollib.packet.Version_B1_8.data.RespawnPacketData;
import com.github.dirtpowered.releasetobeta.data.player.ModernPlayer;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.ModernToBeta;
import com.github.steveice10.mc.protocol.data.game.ClientRequest;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientRequestPacket;
import com.github.steveice10.packetlib.Session;

public class ClientRequestTranslator implements ModernToBeta<ClientRequestPacket> {

    @Override
    public void translate(ClientRequestPacket packet, Session modernSession, BetaClientSession betaSession) {
        ClientRequest request = packet.getRequest();
        if (request != ClientRequest.RESPAWN)
            return;

        ModernPlayer player = betaSession.getPlayer();

        betaSession.sendPacket(new RespawnPacketData(player.getDimension(),
                player.getDifficulty(), player.getGamemode(), player.getWorldHeight(), player.getSeed()));
    }
}
