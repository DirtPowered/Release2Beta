package com.github.dirtpowered.releasetobeta.network.translator.model;

import com.github.dirtpowered.betaprotocollib.model.Packet;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.steveice10.packetlib.Session;

public interface BetaToModern<T extends Packet> {

    void translate(T packet, BetaClientSession session, Session modernSession);
}
