package com.github.dirtpowered.releasetobeta.network.translator.betatomodern;

import com.github.dirtpowered.betaprotocollib.packet.data.OpenWindowPacketData;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.dirtpowered.releasetobeta.utils.Utils;
import com.github.steveice10.mc.protocol.data.game.window.WindowType;
import com.github.steveice10.mc.protocol.data.message.TextMessage;
import com.github.steveice10.mc.protocol.packet.ingame.server.window.ServerOpenWindowPacket;
import com.github.steveice10.packetlib.Session;

public class OpenWindowTranslator implements BetaToModern<OpenWindowPacketData> {

    @Override
    public void translate(OpenWindowPacketData packet, BetaClientSession session, Session modernSession) {
        Utils.debug(packet);
        int windowId = packet.getWindowId();
        int inventoryType = packet.getInventoryType();
        String inventoryTitle = TextMessage.fromString(packet.getWindowTitle()).toJsonString();
        int slots = packet.getSlotsCount();

        WindowType windowType;

        switch (inventoryType) {
            case 0:
                windowType = WindowType.CHEST;
                break;
            /*case 1:
                windowType = WindowType.CRAFTING_TABLE; //Woah! There's a bug in mcprotocollib?
                break;*/
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
    }
}
