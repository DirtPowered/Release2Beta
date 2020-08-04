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

package com.github.dirtpowered.releasetobeta.network.translator.internal;

import com.github.dirtpowered.releasetobeta.ReleaseToBeta;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.ModernToBeta;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientTabCompletePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerTabCompletePacket;
import com.github.steveice10.packetlib.Session;

import java.util.ArrayList;
import java.util.List;

public class ClientTabCompleteTranslator implements ModernToBeta<ClientTabCompletePacket> {

    @Override
    public void translate(ReleaseToBeta main, ClientTabCompletePacket packet, Session modernSession, BetaClientSession betaSession) {
        String[] clientCommands = packet.getText().split("\\s+");
        String[] commands = main.getServer().getCommands().clone();

        //ugly hack
        for (int i = 0; i < commands.length; i++) {
            String command = commands[i];
            commands[i] = "/" + command;
        }

        String[] matches;

        String lastArg = clientCommands[clientCommands.length - 1];
        String[] combined = lastArg.startsWith("/") ? commands : betaSession.combinedPlayerList();

        matches = findWord(combined, lastArg);

        modernSession.send(new ServerTabCompletePacket(matches));
    }

    private String[] findWord(String[] possibleCompletions, String current) {
        List<String> strList = new ArrayList<>();

        for (String word : possibleCompletions) {
            if (word.regionMatches(true, 0, current, 0, current.length())) {
                strList.add(current + word.substring(current.length()));
            }
        }

        strList.sort(String.CASE_INSENSITIVE_ORDER);

        return strList.size() == 0 ? possibleCompletions : strList.toArray(new String[0]);

    }
}
