package com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_7;

import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.ChatPacketData;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerChatPacket;
import com.github.steveice10.packetlib.Session;

public class ChatTranslator implements BetaToModern<ChatPacketData> {

    @Override
    public void translate(ChatPacketData packet, BetaClientSession session, Session modernSession) {
        modernSession.send(new ServerChatPacket(packet.getMessage()));
    }
}
