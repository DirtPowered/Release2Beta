package com.github.dirtpowered.releasetobeta.utils;

public class ChatColor {
    private static final char COLOR_CHAR = '§';

    public static String translate(String rawMessage) {
        return rawMessage.replaceAll("&", String.valueOf(COLOR_CHAR));
    }
}