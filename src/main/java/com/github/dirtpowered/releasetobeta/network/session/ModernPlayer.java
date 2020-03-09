package com.github.dirtpowered.releasetobeta.network.session;

import com.github.dirtpowered.releasetobeta.data.inventory.Slot;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerDisconnectPacket;

public class ModernPlayer {
    private int entityId;
    private BetaClientSession session;
    private Slot lastClickedSlot;

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

    void sendMessage(String message) {
        session.getMain().getServer().getServerSession(session).send(new ServerChatPacket(message));
    }

    void kick(String reason) {
        session.getMain().getServer().getServerSession(session).send(new ServerDisconnectPacket(reason));
    }

    public Slot getLastClickedSlot() {
        return lastClickedSlot;
    }

    public void setLastClickedSlot(Slot lastClickedSlot) {
        this.lastClickedSlot = lastClickedSlot;
    }
}
