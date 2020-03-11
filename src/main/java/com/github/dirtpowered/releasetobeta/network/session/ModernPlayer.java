package com.github.dirtpowered.releasetobeta.network.session;

import com.github.dirtpowered.releasetobeta.data.inventory.Slot;
import com.github.steveice10.mc.auth.data.GameProfile;
import com.github.steveice10.mc.protocol.data.game.PlayerListEntry;
import com.github.steveice10.mc.protocol.data.game.entity.player.GameMode;
import com.github.steveice10.mc.protocol.data.message.Message;

import java.util.UUID;

public class ModernPlayer {
    private String username;
    private int entityId;
    private BetaClientSession session;
    private Slot lastClickedSlot;
    private String clientId;

    ModernPlayer(BetaClientSession session) {
        this.session = session;
    }

    public PlayerListEntry getTabEntry() {
        return new PlayerListEntry(
                new GameProfile(UUID.randomUUID(), username),
                GameMode.SURVIVAL, 0, Message.fromString(username));
    }

    public BetaClientSession getSession() {
        return session;
    }

    String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public Slot getLastClickedSlot() {
        return lastClickedSlot;
    }

    public void setLastClickedSlot(Slot lastClickedSlot) {
        this.lastClickedSlot = lastClickedSlot;
    }

    String getClientId() {
        return clientId;
    }

    void setClientId(String clientId) {
        this.clientId = clientId;
    }
}
