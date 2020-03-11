package com.github.dirtpowered.releasetobeta.network.session;

import com.github.steveice10.packetlib.Session;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.pmw.tinylog.Logger;

public class SessionRegistry {

    private final BiMap<Session, BetaClientSession> sessions = HashBiMap.create();

    void addSession(BetaClientSession client, Session session) {
        sessions.put(session, client);
        Logger.info("[count={}] adding new session (id: {})", sessions.size(), client.getClientId());
    }

    public void removeSession(BetaClientSession client) {
        sessions.inverse().remove(client);
        Logger.info("[count={}] removing session (id: {})", sessions.size(), client.getClientId());
    }

    public BiMap<Session, BetaClientSession> getSessions() {
        return sessions;
    }
}