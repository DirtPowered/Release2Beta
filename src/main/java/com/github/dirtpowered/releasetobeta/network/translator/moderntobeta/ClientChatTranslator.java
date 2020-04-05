package com.github.dirtpowered.releasetobeta.network.translator.moderntobeta;

import com.github.dirtpowered.betaprotocollib.packet.data.ChatPacketData;
import com.github.dirtpowered.releasetobeta.data.player.ModernPlayer;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.ModernToBeta;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientChatPacket;
import com.github.steveice10.packetlib.Session;
import org.pmw.tinylog.Logger;

public class ClientChatTranslator implements ModernToBeta<ClientChatPacket> {

    @Override
    public void translate(ClientChatPacket packet, Session modernSession, BetaClientSession betaSession) {
        ModernPlayer player = betaSession.getPlayer();
        String message = packet.getMessage();
        if (!message.startsWith("/")) Logger.info("[CHAT] {}: {}", player.getUsername(), message);
        else {
            if (betaSession.getMain().getServer().executeCommand(player, message)) {
                Logger.info("[COMMAND] {}: {}", player.getUsername(), message);
                return;
            }
        }

        betaSession.sendPacket(new ChatPacketData(message.length() > 100 ? message.substring(0, 100) : message));
    }
}
