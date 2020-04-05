package com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_7;

import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.KickDisconnectPacketData;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerDisconnectPacket;
import com.github.steveice10.packetlib.Session;
import org.apache.commons.lang3.StringUtils;
import org.pmw.tinylog.Logger;

public class KickDisconnectTranslator implements BetaToModern<KickDisconnectPacketData> {

    @Override
    public void translate(KickDisconnectPacketData packet, BetaClientSession session, Session modernSession) {
        String username = session.getPlayer().getUsername();
        String reason = packet.getDisconnectReason();

        Logger.warn("[{}] disconnected: {}", username == null ? session.getClientId() : username, reason);
        modernSession.send(new ServerDisconnectPacket(reason == null ? StringUtils.EMPTY : reason));
    }
}
