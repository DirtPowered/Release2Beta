package com.github.dirtpowered.releasetobeta.configuration;

public class R2BConfiguration {

    public static String motd = "Release2Beta server";
    public static int maxPlayers = 20;
    public static boolean skinFix = true;
    public static boolean arrowFix = true;
    public static String remoteAddress = "dev.mineblock.pl";
    public static int remotePort = 25567;
    public static String bindAddress = "localhost";
    public static int bindPort = 25565;
    public static boolean disableSprinting = false;
    public static String resourcePack = "https://www.dropbox.com/s/hqiluec4aoho3po/beta.zip?dl=1";
    public static String resourcePackHash = "D4EE78CF52D243371AD1AFF87EA69428BFCA096C";

    public R2BConfiguration() {
        /*Toml toml = new Toml().read(new File("Release2Beta.toml"));

        motd = toml.getString("motd");
        maxPlayers = toml.getLong("max-players").intValue();
        skinFix = toml.getBoolean("skin-fix");*/
    }
}
