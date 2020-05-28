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

package com.github.dirtpowered.releasetobeta.utils.chat;

import com.github.steveice10.mc.protocol.data.message.ChatColor;
import com.github.steveice10.mc.protocol.data.message.Message;
import com.github.steveice10.mc.protocol.data.message.MessageStyle;
import com.github.steveice10.mc.protocol.data.message.TextMessage;

public class ChatUtils {
    private final static char COLOR_CHAR = '§';
    private final static char[] ALLOWED_CHARACTERS = new char[]{
            ' ', '!', '"', '#', '$', '%', '&', '\'', '(', ')',
            '*', '+', ',', '-', '.', '/', '0', '1', '2', '3',
            '4', '5', '6', '7', '8', '9', ':', ';', '<', '=',
            '>', '?', '@', 'A', 'B', 'C', 'D', 'E', 'F', 'G',
            'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q',
            'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '[',
            '\\', ']', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
            'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
            's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '{', '|',
            '}', '~', '⌂', 'Ç', 'ü', 'é', 'â', 'ä', 'à', 'å',
            'ç', 'ê', 'ë', 'è', 'ï', 'î', 'ì', 'Ä', 'Å', 'É',
            'æ', 'Æ', 'ô', 'ö', 'ò', 'û', 'ù', 'ÿ', 'Ö', 'Ü',
            'ø', '£', 'Ø', '×', 'ƒ', 'á', 'í', 'ó', 'ú', 'ñ',
            'Ñ', 'ª', 'º', '¿', '®', '¬', '½', '¼', '¡', '«',
            '»', '_', '^', '\''
    };

    public static String toBetaChatColors(String message) {
        String replacement = "§f";
        return message
                .replaceAll("§l", replacement)
                .replaceAll("§m", replacement)
                .replaceAll("§n", replacement)
                .replaceAll("§o", replacement);
    }

    public static String replaceIllegal(String message) {
        message = message.trim();
        String allowed = new String(ALLOWED_CHARACTERS);

        for (int i = 0; i < message.length(); ++i) {
            String toReplace = Character.toString(message.charAt(i));

            if (!allowed.contains(toReplace)) {
                message = message.replaceAll(toReplace, "*");
            }
        }

        return message;
    }

    public static String colorize(String rawMessage) {
        return rawMessage.replaceAll("&", String.valueOf(COLOR_CHAR));
    }

    public static Message toModernMessage(String oldMessage, boolean colors) {
        return TextMessage.fromString(colors ? ChatUtils.colorize(oldMessage) : oldMessage).setStyle(new MessageStyle().setColor(ChatColor.RESET));
    }
}
