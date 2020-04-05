package com.github.dirtpowered.releasetobeta.network.session;

import com.github.steveice10.packetlib.Session;
import org.pmw.tinylog.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionRegistry {

    private Map<String, MultiSession> sessions = new ConcurrentHashMap<>();

    void addSession(String clientId, MultiSession multiSession) {
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

    public MultiSession getSession(String clientId) {
        return sessions.get(clientId);
    }

    public BetaClientSession getClientSessionFromServerSession(Session modernSession) {
        return sessions.values().stream()
                .filter(multiSession -> multiSession.getModernSession() == modernSession).findFirst()
                .map(MultiSession::getBetaClientSession).orElse(null);
    }
}