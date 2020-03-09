package com.github.dirtpowered.releasetobeta.network.translator.moderntobeta;

import com.github.dirtpowered.betaprotocollib.data.BetaItemStack;
import com.github.dirtpowered.betaprotocollib.packet.data.WindowClickPacketData;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.ModernToBeta;
import com.github.dirtpowered.releasetobeta.utils.Utils;
import com.github.steveice10.mc.protocol.data.game.window.ClickItemParam;
import com.github.steveice10.mc.protocol.data.game.window.WindowAction;
import com.github.steveice10.mc.protocol.packet.ingame.client.window.ClientWindowActionPacket;
import com.github.steveice10.packetlib.Session;

public class ClientWindowActionTranslator implements ModernToBeta<ClientWindowActionPacket> {

    @Override
    public void translate(ClientWindowActionPacket packet, Session modernSession, BetaClientSession betaSession) {
        betaSession.getMain().getScheduledExecutorService().execute(() -> {
            Utils.debug(packet);

            //TODO: woah. Sooo much to do here
            // - Nothing work correctly atm.

            int windowId = packet.getWindowId();
            int slot = packet.getSlot();
            int mouseClick = packet.getParam() == ClickItemParam.LEFT_CLICK ? 0 : 1;

            int actionId = packet.getActionId();

            BetaItemStack itemStack = Utils.itemStackToBetaItemStack(packet.getClickedItem());
            WindowAction windowAction = packet.getAction();

            boolean shiftPressed = windowAction == WindowAction.SHIFT_CLICK_ITEM;

            betaSession.sendPacket(new WindowClickPacketData(windowId, slot, mouseClick, (short) actionId, itemStack, shiftPressed));
            /*
            [INFO 21:26:01] [DEBUG] ClientWindowActionPacket[windowId=0,slot=36,param=LEFT_CLICK,actionId=6,action=CLICK_ITEM,clicked=ItemStack(id=12, amount=1, data=0, nbt=null)]
            [INFO 21:26:02] [DEBUG] ClientWindowActionPacket[windowId=0,slot=38,param=LEFT_CLICK,actionId=7,action=CLICK_ITEM,clicked=<null>]
            [INFO 21:26:09] [DEBUG] ClientWindowActionPacket[windowId=0,slot=38,param=LEFT_CLICK,actionId=8,action=CLICK_ITEM,clicked=ItemStack(id=12, amount=1, data=0, nbt=null)]
             */
        });
    }
}
