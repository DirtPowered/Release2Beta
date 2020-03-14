package com.github.dirtpowered.releasetobeta.data.player;

import com.github.dirtpowered.releasetobeta.data.Constants;
import com.github.dirtpowered.releasetobeta.data.entity.model.Entity;
import com.github.dirtpowered.releasetobeta.utils.Utils;
import com.github.steveice10.mc.auth.data.GameProfile;
import com.github.steveice10.mc.protocol.data.game.PlayerListEntry;
import com.github.steveice10.mc.protocol.data.game.entity.player.GameMode;
import com.github.steveice10.mc.protocol.data.message.Message;
import com.github.steveice10.packetlib.Session;

import java.util.UUID;

public class BetaPlayer extends Entity {

    private String username;
    private GameProfile gameProfile;

    public BetaPlayer(String username, int entityId) {
        super(entityId, true);

        this.username = username;
        this.gameProfile = new GameProfile(Utils.getOfflineUUID(username), username);
    }

    public PlayerListEntry getTabEntry() {
        return new PlayerListEntry(gameProfile, GameMode.SURVIVAL, 0, Message.fromString(Constants.BETA_PLAYER_PREFIX + username));
    }

    public UUID getUUID() {
        return gameProfile.getId();
    }

    public GameProfile getGameProfile() {
        return gameProfile;
    }

    @Override
    public void onSpawn(Session session) {

    }
}
