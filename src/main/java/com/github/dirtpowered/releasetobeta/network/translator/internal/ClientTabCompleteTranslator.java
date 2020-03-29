package com.github.dirtpowered.releasetobeta.network.translator.internal;

import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.ModernToBeta;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientTabCompletePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerTabCompletePacket;
import com.github.steveice10.packetlib.Session;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClientTabCompleteTranslator implements ModernToBeta<ClientTabCompletePacket> {

    @Override
    public void translate(ClientTabCompletePacket packet, Session modernSession, BetaClientSession betaSession) {
        String[] clientCommands = packet.getText().split("\\s+");
        String[] commands = betaSession.getMain().getServer().getCommands();

        //ugly hack
        for (int i = 0; i < commands.length; i++) {
            String command = commands[i];
            commands[i] = "/" + command;
        }

        String[] matches;
        String[] combined = ArrayUtils.addAll(commands, betaSession.combinedPlayerList());

        matches = findWord(combined, clientCommands[clientCommands.length - 1]);

        modernSession.send(new ServerTabCompletePacket(matches));
    }

    private String[] findWord(String[] possibleCompletions, String current) {
        List<String> strList = new ArrayList<>();

        for (String word : possibleCompletions) {
            if (word.startsWith(current)) {
                strList.add(current + word.substring(current.length()));
            }
        }

        Collections.sort(strList);
        return strList.toArray(new String[0]);
    }
}
