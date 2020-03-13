package com.github.dirtpowered.releasetobeta.network.session;

import org.pmw.tinylog.Logger;

import java.util.LinkedHashMap;
import java.util.Map;

public class SessionRegistry {

    private Map<String, MultiSession> sessions = new LinkedHashMap<>();

    public void addSession(String clientId, MultiSession multiSession) {
        sessions.put(clientId, multiSession);

        Logger.info("[count={}/id={}] adding new session", sessions.size(), clientId);
    }

    public void removeSession(String clientId) {
        sessions.remove(clientId);
        Logger.info("[count={}/id={}] removing session", sessions.size(), clientId);
    }

    public Map<String, MultiSession> getSessions() {
        return sessions;
    }

    MultiSession getSession(String clientId) {
        return sessions.get(clientId);
    }
}