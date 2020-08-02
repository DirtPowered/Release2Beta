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

package com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_7;

import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.OpenWindowPacketData;
import com.github.dirtpowered.releasetobeta.ReleaseToBeta;
import com.github.dirtpowered.releasetobeta.data.player.ModernPlayer;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.dirtpowered.releasetobeta.utils.chat.ChatUtils;
import com.github.steveice10.mc.protocol.data.game.window.WindowType;
import com.github.steveice10.mc.protocol.packet.ingame.server.window.ServerOpenWindowPacket;
import com.github.steveice10.packetlib.Session;

public class OpenWindowTranslator implements BetaToModern<OpenWindowPacketData> {

    @Override
    public void translate(ReleaseToBeta main, OpenWindowPacketData packet, BetaClientSession session, Session modernSession) {
        ModernPlayer player = session.getPlayer();
        int windowId = packet.getWindowId();
        int inventoryType = packet.getInventoryType();
        String inventoryTitle = ChatUtils.toModernMessage(packet.getWindowTitle(), false).toJsonString();

        int slots = packet.getSlotsCount();

        WindowType windowType;

        switch (inventoryType) {
            case 0:
                switch (slots) {
                    case 9:
                        windowType = WindowType.GENERIC_9X1;
                        break;
                    case 18:
                        windowType = WindowType.GENERIC_9X2;
                        break;
                    case 27:
                        windowType = WindowType.GENERIC_9X3;
                        break;
                    case 36:
                        windowType = WindowType.GENERIC_9X4;
                        break;
                    case 45:
                        windowType = WindowType.GENERIC_9X5;
                        break;
                    case 54:
                        windowType = WindowType.GENERIC_9X6;
                        break;
                    default:
                        windowType = WindowType.GENERIC_9X1;
                        break;
                }
                break;
            case 1:
                windowType = WindowType.CRAFTING;
                break;
            case 2:
                windowType = WindowType.FURNACE;
                break;
            case 3:
                windowType = WindowType.GENERIC_3X3;
                break;
            default:
                windowType = WindowType.GENERIC_3X3;
                break;
        }

        modernSession.send(new ServerOpenWindowPacket(windowId, windowType, inventoryTitle));
        player.getPlayerEvent().onInventoryOpen(windowType);
    }
}
