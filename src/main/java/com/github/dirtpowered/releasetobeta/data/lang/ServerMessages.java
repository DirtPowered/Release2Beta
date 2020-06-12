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

package com.github.dirtpowered.releasetobeta.data.lang;

import com.github.steveice10.mc.protocol.data.message.Message;
import com.github.steveice10.mc.protocol.data.message.TextMessage;
import com.github.steveice10.mc.protocol.data.message.TranslationMessage;
import lombok.AllArgsConstructor;
import lombok.Data;

public class ServerMessages {

    public static RichMessage translate(String rawMessage) {
        RichMessage translatedMessage;

        switch (rawMessage) {
            case "Took too long to log in":
                translatedMessage = new RichMessage(new TranslationMessage("multiplayer.disconnect.slow_login"), false);
                break;
            case "Failed to verify username!":
                translatedMessage = new RichMessage(new TranslationMessage("multiplayer.disconnect.unverified_username"), false);
                break;
            case "You can only sleep at night":
                translatedMessage = new RichMessage(new TranslationMessage("tile.bed.noSleep"), true);
                break;
            case "This bed is occupied":
                translatedMessage = new RichMessage(new TranslationMessage("tile.bed.occupied"), true);
                break;
            case "Your home bed was missing or obstructed":
                translatedMessage = new RichMessage(new TranslationMessage("tile.bed.notValid"), true);
                break;
            case "Kicked by admin":
                translatedMessage = new RichMessage(new TranslationMessage("multiplayer.disconnect.kicked"), false);
                break;
            case "Banned by admin":
                translatedMessage = new RichMessage(new TranslationMessage("multiplayer.disconnect.banned"), false);
                break;
            case "You logged in from another location":
                translatedMessage = new RichMessage(new TranslationMessage("multiplayer.disconnect.duplicate_login"), false);
                break;
            case "Flying is not enabled on this server":
                translatedMessage = new RichMessage(new TranslationMessage("multiplayer.disconnect.flying"), false);
                break;
            default:
                translatedMessage = new RichMessage(new TextMessage(rawMessage), false);
                break;
        }

        return translatedMessage;
    }

    @Data
    @AllArgsConstructor
    public final static class RichMessage {
        private Message message;
        private boolean hotbar;
    }
}
