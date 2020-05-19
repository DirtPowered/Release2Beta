/*
 * Copyright (c) 2020 Dirt Powered
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.dirtpowered.releasetobeta.network.server;

import com.github.dirtpowered.releasetobeta.data.player.ModernPlayer;
import com.github.dirtpowered.releasetobeta.network.session.MultiSession;
import com.github.dirtpowered.releasetobeta.utils.collections.MapUtil;
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
        return MapUtil.transform(getPlayers(), player -> {
            if (player.getGameProfile() == null) {
                return null;
            }
            return player.getTabEntry();
        });
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

    public int getOnlineCount() {
        return getPlayers().size();
    }
}
