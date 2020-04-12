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

package com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_7.UpdateProgress;

import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.UpdateProgressPacketData;
import com.github.steveice10.mc.protocol.packet.ingame.server.window.ServerWindowPropertyPacket;
import com.github.steveice10.packetlib.Session;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UpdateProgressHandler {
    private int maxFuel;

    public void translateUpdateProgress(UpdateProgressPacketData data, Session session) {
        int windowId = data.getWindowId();
        int property = data.getProgressBar();
        int value = property == 0 ? data.getProgressBarValue() : 0;

        if (property == 2) {
            //TODO: send max-progress once
            maxFuel = data.getProgressBarValue();
            session.send(new ServerWindowPropertyPacket(windowId, 3, 200)); //max progress
        } else if (property == 1) {
            if (maxFuel != 0)
                session.send(new ServerWindowPropertyPacket(windowId, 0, data.getProgressBarValue() * 200 / maxFuel)); //fuel left
            return;
        }

        int newProperty = property == 0 ? 2 : 0;
        session.send(new ServerWindowPropertyPacket(windowId, property == 2 ? 1 : newProperty, value));

    }
}
