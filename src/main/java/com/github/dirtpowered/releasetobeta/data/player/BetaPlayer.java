package com.github.dirtpowered.releasetobeta.data.player;

import com.github.dirtpowered.releasetobeta.configuration.R2BConfiguration;
import com.github.dirtpowered.releasetobeta.data.entity.model.Entity;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.utils.Callback;
import com.github.dirtpowered.releasetobeta.utils.TextColor;
import com.github.dirtpowered.releasetobeta.utils.Utils;
import com.github.steveice10.mc.auth.data.GameProfile;
import com.github.steveice10.mc.protocol.data.game.PlayerListEntry;
import com.github.steveice10.mc.protocol.data.game.entity.player.GameMode;
import com.github.steveice10.mc.protocol.data.message.Message;
import com.github.steveice10.packetlib.Session;
import lombok.Getter;

import java.util.UUID;

public class BetaPlayer extends Entity {
    @Getter
    private String username;
    @Getter
    private GameProfile gameProfile;

    public BetaPlayer(BetaClientSession session, String username, int entityId, Callback<BetaPlayer> callback) {
        super(entityId, true);

        this.username = username;
        if (R2BConfiguration.skinFix) {
            session.getMain().getServer().getProfileCache().getSkin(username).whenComplete((profile, throwable) -> {
                gameProfile = profile;
                callback.onComplete(this);
            });
        } else {
            gameProfile = new GameProfile(Utils.getOfflineUUID(username), username);
            callback.onComplete(this);
        }
    }

    public PlayerListEntry getTabEntry() {
        return new PlayerListEntry(gameProfile, GameMode.SURVIVAL, 0,
                Message.fromString(TextColor.translate("&9[BETA] &r" + username)));
    }

    public UUID getUUID() {
        return gameProfile.getId();
    }

    @Override
    public void onSpawn(Session session) {

    }
}
