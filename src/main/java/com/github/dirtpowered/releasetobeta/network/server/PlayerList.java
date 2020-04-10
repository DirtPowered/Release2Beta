package com.github.dirtpowered.releasetobeta.network.server;

import com.github.dirtpowered.releasetobeta.data.player.ModernPlayer;
import com.github.dirtpowered.releasetobeta.network.session.MultiSession;
import com.github.dirtpowered.releasetobeta.utils.MapUtil;
import com.github.steveice10.mc.auth.data.GameProfile;
import com.github.steveice10.mc.protocol.data.game.PlayerListEntry;
import com.github.steveice10.mc.protocol.data.game.PlayerListEntryAction;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerPlayerListEntryPacket;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class PlayerList {
    private ServerConnection serverConnection;
    private long lastTabUpdate;

    PlayerList(ServerConnection serverConnection) {
        this.serverConnection = serverConnection;
    }

    public List<ModernPlayer> getPlayers() {
        Map<String, MultiSession> sessionMap = serverConnection.getMain().getSessionRegistry().getSessions();
        List<MultiSession> players = new ArrayList<>(sessionMap.values());

        return MapUtil.transform(players, multiSession -> Objects.requireNonNull(multiSession).getBetaClientSession().getPlayer());
    }

    public List<GameProfile> getProfiles() {
        return MapUtil.transform(getPlayers(), player -> Objects.requireNonNull(player).getGameProfile());
    }

    private List<PlayerListEntry> getTabEntries() {
        return MapUtil.transform(getPlayers(), player -> Objects.requireNonNull(player).getTabEntry());
    }

    public void removeTabEntry(ModernPlayer player) {
        if (player.getGameProfile() == null)
            return;

        ServerPlayerListEntryPacket entryPacket =
                new ServerPlayerListEntryPacket(PlayerListEntryAction.REMOVE_PLAYER, new PlayerListEntry[]{
                        new PlayerListEntry(player.getGameProfile())
                });

        serverConnection.broadcastPacket(entryPacket);
    }

    public void addTabEntry(ModernPlayer player) {
        if (player.getTabEntry() == null)
            return;

        player.sendPacket(new ServerPlayerListEntryPacket(PlayerListEntryAction.ADD_PLAYER, getTabEntries().toArray(new PlayerListEntry[0])));
        ServerPlayerListEntryPacket entryPacket =
                new ServerPlayerListEntryPacket(PlayerListEntryAction.ADD_PLAYER, new PlayerListEntry[]{
                        player.getTabEntry()
                });

        serverConnection.broadcastPacket(entryPacket);
    }

    public UUID getUUIDFromUsername(String username) {
        for (ModernPlayer player : getPlayers()) {
            if (player.getUsername() != null) {
                if (player.getUsername().equals(username)) {
                    return player.getGameProfile().getId();
                }
            } else {
                player.getSession().disconnect();
            }
        }

        return null;
    }

    void updateInternalTabList() {
        if (System.currentTimeMillis() - lastTabUpdate > 3000L) {
            getPlayers().forEach(player -> {
                player.sendPacket(new ServerPlayerListEntryPacket(PlayerListEntryAction.UPDATE_LATENCY, getTabEntries().toArray(new PlayerListEntry[0])));
            });

            lastTabUpdate = System.currentTimeMillis();
        }
    }
}
