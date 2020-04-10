package com.github.dirtpowered.releasetobeta.configuration;

import com.github.dirtpowered.betaprotocollib.data.version.MinecraftVersion;
import lombok.NoArgsConstructor;
import org.pmw.tinylog.Logger;
import org.simpleyaml.configuration.file.YamlFile;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@NoArgsConstructor
public class R2BConfiguration {
    public static MinecraftVersion version;
    public static String motd;
    public static int maxPlayers;
    public static boolean skinFix;
    public static boolean arrowFix;
    public static String remoteAddress;
    public static int remotePort;
    public static String bindAddress;
    public static int bindPort;
    public static boolean disableSprinting;
    public static String resourcePack;
    public static String resourcePackHash;
    public static boolean tabComplete;
    public static boolean testMode;
    public static boolean ver1_8PingPassthrough;

    public void loadConfiguration() {
        YamlFile config = new YamlFile("config.yml");

        try {
            if (config.exists()) {
                config.load();
            } else {
                //not compiled
                Path p = Paths.get("src/main/resources/config.yml");
                if (Files.exists(p)) {
                    Files.copy(p, Paths.get("config.yml"));
                } else {
                    //compiled
                    InputStream inputStream = getClass().getResourceAsStream("/config.yml");
                    Files.copy(inputStream, Paths.get("config.yml"));
                }

                config.load();
            }

            try {
                version = MinecraftVersion.valueOf(config.getString("general.beta_version"));
            } catch (Exception e) {
                Logger.warn("'beta_version' is wrong, defaulting to B_1_7_3");
                version = MinecraftVersion.B_1_7_3;
            }

            motd = config.getString("general.motd");
            maxPlayers = config.getInt("general.max_players");

            remoteAddress = config.getString("connection.remote_address");
            remotePort = config.getInt("connection.remote_port");
            bindAddress = config.getString("connection.bind_address");
            bindPort = config.getInt("connection.bind_port");

            resourcePack = config.getString("misc.resource_pack_url");
            resourcePackHash = config.getString("misc.resource_pack_hash");
            tabComplete = config.getBoolean("misc.tab_complete");
            skinFix = config.getBoolean("misc.skin_fix");
            arrowFix = config.getBoolean("misc.arrows_fix");
            disableSprinting = config.getBoolean("misc.disable_sprinting");
            ver1_8PingPassthrough = config.getBoolean("misc.beta_1_8_ping_passthrough");

            testMode = config.getBoolean("experimental.enable_chunk_updates");
        } catch (Exception e) {
            Logger.error("error: {}", e.getMessage());
        }
    }
}
