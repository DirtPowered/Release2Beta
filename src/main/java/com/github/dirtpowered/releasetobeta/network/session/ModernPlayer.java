package com.github.dirtpowered.releasetobeta.network.session;

import com.github.dirtpowered.releasetobeta.data.inventory.Slot;
import com.github.steveice10.mc.auth.data.GameProfile;
import com.github.steveice10.mc.protocol.data.game.PlayerListEntry;
import com.github.steveice10.mc.protocol.data.game.entity.player.GameMode;
import com.github.steveice10.mc.protocol.data.message.Message;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerDisconnectPacket;
import com.github.steveice10.packetlib.packet.Packet;

import java.util.UUID;

public class ModernPlayer {
    private String username;
    private int entityId;
    private BetaClientSession session;
    private Slot lastClickedSlot;

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

    public String getUsername() {
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

    void sendMessage(String message) {
        sendPacket(new ServerChatPacket(message));
    }

    void kick(String reason) {
        sendPacket(new ServerDisconnectPacket(reason));
    }

    void sendPacket(Packet packet) {
        session.getMain().getServer().getServerSession(session).send(packet);
    }

    public Slot getLastClickedSlot() {
        return lastClickedSlot;
    }

    public void setLastClickedSlot(Slot lastClickedSlot) {
        this.lastClickedSlot = lastClickedSlot;
    }
}
