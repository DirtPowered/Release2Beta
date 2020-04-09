package com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_7;

import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.OpenWindowPacketData;
import com.github.dirtpowered.releasetobeta.data.player.ModernPlayer;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.steveice10.mc.protocol.data.game.window.WindowType;
import com.github.steveice10.mc.protocol.data.message.TextMessage;
import com.github.steveice10.mc.protocol.packet.ingame.server.window.ServerOpenWindowPacket;
import com.github.steveice10.packetlib.Session;

public class OpenWindowTranslator implements BetaToModern<OpenWindowPacketData> {

    @Override
    public void translate(OpenWindowPacketData packet, BetaClientSession session, Session modernSession) {
        ModernPlayer player = session.getPlayer();
        int windowId = packet.getWindowId();
        int inventoryType = packet.getInventoryType();
        String inventoryTitle = TextMessage.fromString(packet.getWindowTitle()).toJsonString();
        int slots = packet.getSlotsCount();

        WindowType windowType;

        switch (inventoryType) {
            case 0:
                windowType = WindowType.CHEST;
                break;
            case 1:
                windowType = WindowType.CRAFTING_TABLE;
                slots = 0; //Always 0 for non-storage windows
                break;
            case 2:
                windowType = WindowType.FURNACE;
                break;
            case 3:
                windowType = WindowType.DISPENSER;
                break;
            default:
                windowType = WindowType.GENERIC_INVENTORY;
                break;
        }

        modernSession.send(new ServerOpenWindowPacket(windowId, windowType, inventoryTitle, slots));
        player.onInventoryOpen(windowType);
    }
}
