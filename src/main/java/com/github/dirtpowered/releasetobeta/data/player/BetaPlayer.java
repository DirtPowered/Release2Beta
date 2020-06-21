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

package com.github.dirtpowered.releasetobeta.data.player;

import com.github.dirtpowered.releasetobeta.configuration.R2BConfiguration;
import com.github.dirtpowered.releasetobeta.data.entity.model.Entity;
import com.github.dirtpowered.releasetobeta.data.entity.model.Mob;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.utils.Utils;
import com.github.dirtpowered.releasetobeta.utils.chat.ChatUtils;
import com.github.dirtpowered.releasetobeta.utils.interfaces.Callback;
import com.github.steveice10.mc.auth.data.GameProfile;
import com.github.steveice10.mc.protocol.data.game.PlayerListEntry;
import com.github.steveice10.mc.protocol.data.game.entity.player.GameMode;
import com.github.steveice10.mc.protocol.data.game.world.sound.BuiltinSound;
import com.github.steveice10.mc.protocol.data.game.world.sound.SoundCategory;
import com.github.steveice10.mc.protocol.data.message.Message;
import com.github.steveice10.packetlib.Session;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
public class BetaPlayer extends Entity implements Mob {

    @Setter
    private boolean inVehicle;

    @Setter
    private int vehicleEntityId;

    private String username;
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
                Message.fromString(ChatUtils.colorize("&9[BETA] &r" + username)));
    }

    public UUID getUUID() {
        return gameProfile.getId();
    }

    @Override
    public void onSpawn(Session session) {

    }

    @Override
    public void onDeath(Session session) {
        playSound(session, BuiltinSound.ENTITY_PLAYER_DEATH, SoundCategory.PLAYER);
    }

    @Override
    public void onDamage(Session session) {
        playSound(session, BuiltinSound.ENTITY_PLAYER_HURT, SoundCategory.PLAYER);
    }

    @Override
    public void onUpdate(Session session) {
        // do nothing
    }
}
