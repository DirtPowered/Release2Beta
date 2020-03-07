package com.github.dirtpowered.releasetobeta.network.translator.model;

import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.packet.Packet;

public interface ModernToBeta<T extends Packet> {

    void translate(T packet, Session modernSession, BetaClientSession betaSession);
}