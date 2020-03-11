package com.github.dirtpowered.releasetobeta.network.session;

import com.github.steveice10.packetlib.Session;

public class MultiSession {

    private BetaClientSession betaClientSession;
    private Session modernSession;

    MultiSession(BetaClientSession betaClientSession, Session modernSession) {
        this.betaClientSession = betaClientSession;
        this.modernSession = modernSession;
    }

    public BetaClientSession getBetaClientSession() {
        return betaClientSession;
    }

    public Session getModernSession() {
        return modernSession;
    }
}
