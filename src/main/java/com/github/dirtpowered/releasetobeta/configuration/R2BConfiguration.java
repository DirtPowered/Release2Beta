package com.github.dirtpowered.releasetobeta.configuration;

public class R2BConfiguration {

    public static String motd = "Release2Beta server";
    public static int maxPlayers = 20;
    public static boolean skinFix = true;
    public static boolean arrowFix = true;

    public R2BConfiguration() {
        /*Toml toml = new Toml().read(new File("Release2Beta.toml"));

        motd = toml.getString("motd");
        maxPlayers = toml.getLong("max-players").intValue();
        skinFix = toml.getBoolean("skin-fix");*/
    }
}
