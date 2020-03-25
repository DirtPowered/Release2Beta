package com.github.dirtpowered.releasetobeta.data.skin;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.dirtpowered.releasetobeta.utils.Utils;
import com.github.steveice10.mc.auth.data.GameProfile;
import com.github.steveice10.mc.auth.exception.profile.ProfileException;
import com.github.steveice10.mc.auth.service.ProfileService;
import com.github.steveice10.mc.auth.service.SessionService;
import org.pmw.tinylog.Logger;

import java.net.Proxy;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class ProfileCache {
    private static final ProfileService PROFILE_SERVICE = new ProfileService(Proxy.NO_PROXY);
    private static final SessionService SESSION_SERVICE = new SessionService(Proxy.NO_PROXY);
    private AsyncLoadingCache<String, GameProfile> profileCache;

    public ProfileCache() {
        profileCache = Caffeine.newBuilder().maximumSize(100).expireAfterWrite(1, TimeUnit.HOURS).buildAsync(k -> fetchProfile(k));
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
                    Logger.error("Error: {}", e.getMessage());
                }
            }

            @Override
            public void onProfileLookupFailed(GameProfile profile, Exception e) {
                Logger.error("Error: {}", e.getMessage());
            }
        });

        return gameProfile[0];
    }
}
