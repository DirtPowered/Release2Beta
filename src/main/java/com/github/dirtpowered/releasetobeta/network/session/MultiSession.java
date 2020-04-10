package com.github.dirtpowered.releasetobeta.network.session;

import com.github.steveice10.packetlib.Session;
import lombok.Getter;

@Getter
public class MultiSession {

    private BetaClientSession betaClientSession;
    private Session modernSession;

    MultiSession(BetaClientSession betaClientSession, Session modernSession) {
        this.betaClientSession = betaClientSession;
        this.modernSession = modernSession;
    }
}
