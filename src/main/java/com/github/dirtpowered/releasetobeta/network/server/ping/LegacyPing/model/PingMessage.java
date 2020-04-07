package com.github.dirtpowered.releasetobeta.network.server.ping.LegacyPing.model;

public class PingMessage {
    private String motd;
    private int onlinePlayers;
    private int maxPlayers;

    public PingMessage(String kickMessage) {
        String[] motdMessage = kickMessage.split("\u00a7");
        this.motd = motdMessage[0];
        this.onlinePlayers = Integer.parseInt(motdMessage[1]);
        this.maxPlayers = Integer.parseInt(motdMessage[2]);
    }

    public String getMotd() {
        return motd;
    }

    public int getOnlinePlayers() {
        return onlinePlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }
}
