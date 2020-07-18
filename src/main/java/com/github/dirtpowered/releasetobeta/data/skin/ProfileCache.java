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

package com.github.dirtpowered.releasetobeta.data.skin;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.dirtpowered.releasetobeta.ReleaseToBeta;
import com.github.dirtpowered.releasetobeta.utils.Utils;
import com.github.steveice10.mc.auth.data.GameProfile;
import com.github.steveice10.mc.auth.exception.profile.ProfileException;
import com.github.steveice10.mc.auth.service.ProfileService;
import com.github.steveice10.mc.auth.service.SessionService;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class ProfileCache {
    private static final ProfileService PROFILE_SERVICE = new ProfileService();
    private static final SessionService SESSION_SERVICE = new SessionService();
    private AsyncLoadingCache<String, GameProfile> profileCache;
    private ReleaseToBeta main;

    public ProfileCache(ReleaseToBeta main) {
        this.main = main;
        profileCache = Caffeine.newBuilder().maximumSize(100).expireAfterWrite(1, TimeUnit.HOURS).buildAsync(this::fetchProfile);
    }

    public CompletableFuture<GameProfile> getSkin(String username) {
        return profileCache.get(username);
    }

    private GameProfile fetchProfile(String username) {
        final GameProfile[] gameProfile = {new GameProfile(Utils.getOfflineUUID(username), username)};

        PROFILE_SERVICE.findProfilesByName(new String[]{username}, new ProfileService.ProfileLookupCallback() {

            @Override
            public void onProfileLookupSucceeded(GameProfile profile) {
                try {
                    SESSION_SERVICE.fillProfileProperties(profile);
                    gameProfile[0] = profile;
                } catch (ProfileException e) {
                    main.getLogger().error("[" + profile.getName() + "] Error: " + e.getMessage());
                }
            }

            @Override
            public void onProfileLookupFailed(GameProfile profile, Exception e) {
                main.getLogger().error("[" + profile.getName() + "] Error: " + e.getMessage());
            }
        });

        return gameProfile[0];
    }
}
