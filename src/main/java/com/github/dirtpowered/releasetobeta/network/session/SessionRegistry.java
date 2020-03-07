package com.github.dirtpowered.releasetobeta.network.session;

import com.github.steveice10.packetlib.Session;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class SessionRegistry {

    private final BiMap<Session, BetaClientSession> sessions = HashBiMap.create();

    public void addSession(final BetaClientSession client, Session session) {
        sessions.put(session, client);
    }

    public void removeSession(final Session session) {
        sessions.remove(session);
    }

    public BiMap<Session, BetaClientSession> getSessions() {
        return sessions;
    }
}