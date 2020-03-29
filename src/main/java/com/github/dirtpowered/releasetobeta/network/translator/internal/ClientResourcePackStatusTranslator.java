package com.github.dirtpowered.releasetobeta.network.translator.internal;

import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.ModernToBeta;
import com.github.dirtpowered.releasetobeta.utils.Utils;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientResourcePackStatusPacket;
import com.github.steveice10.packetlib.Session;

public class ClientResourcePackStatusTranslator implements ModernToBeta<ClientResourcePackStatusPacket> {

    @Override
    public void translate(ClientResourcePackStatusPacket packet, Session modernSession, BetaClientSession betaSession) {
        Utils.debug(packet);

        //TODO: command & message when client reject resourcepack
    }
}
