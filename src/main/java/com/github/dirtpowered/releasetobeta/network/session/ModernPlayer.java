package com.github.dirtpowered.releasetobeta.network.session;

import com.github.steveice10.mc.protocol.packet.ingame.server.ServerChatPacket;

public class ModernPlayer {
    private int entityId;
    private BetaClientSession session;

    ModernPlayer(BetaClientSession session) {
        this.session = session;
    }

    public BetaClientSession getSession() {
        return session;
    }

    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public void sendMessage(String message) {
        session.getMain().getServer().getClientFromBetaSession(session).send(new ServerChatPacket(message));
    }
}
