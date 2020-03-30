package com.github.dirtpowered.releasetobeta.network.translator.moderntobeta;

import com.github.dirtpowered.betaprotocollib.data.BetaItemStack;
import com.github.dirtpowered.betaprotocollib.packet.data.WindowClickPacketData;
import com.github.dirtpowered.releasetobeta.data.player.ModernPlayer;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.ModernToBeta;
import com.github.dirtpowered.releasetobeta.utils.Utils;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.ItemStack;
import com.github.steveice10.mc.protocol.data.game.window.ClickItemParam;
import com.github.steveice10.mc.protocol.data.game.window.SpreadItemParam;
import com.github.steveice10.mc.protocol.data.game.window.WindowAction;
import com.github.steveice10.mc.protocol.data.game.window.WindowActionParam;
import com.github.steveice10.mc.protocol.packet.ingame.client.window.ClientWindowActionPacket;
import com.github.steveice10.packetlib.Session;

public class ClientWindowActionTranslator implements ModernToBeta<ClientWindowActionPacket> {

    @Override
    public void translate(ClientWindowActionPacket packet, Session modernSession, BetaClientSession betaSession) {
        betaSession.getMain().getScheduledExecutorService().execute(() -> {
            Utils.debug(packet);

            ModernPlayer player = betaSession.getPlayer();

            int windowId = packet.getWindowId();
            int slot = packet.getSlot();
            int mouseClick = packet.getParam() == ClickItemParam.LEFT_CLICK ? 0 : 1;
            int actionId = packet.getActionId();
            ItemStack item = packet.getClickedItem();
            WindowAction windowAction = packet.getAction();
            WindowActionParam param = packet.getParam();
            boolean shiftPressed = windowAction == WindowAction.SHIFT_CLICK_ITEM;

            //block non-existent inventory actions
            if (windowAction == WindowAction.FILL_STACK || windowAction == WindowAction.SPREAD_ITEM
                    && param == SpreadItemParam.RIGHT_MOUSE_END_DRAG || slot == 45) {

                player.updateInventory();
                int lastSlot = player.getInventory().getLastSlot();
                betaSession.sendPacket(new WindowClickPacketData(windowId, lastSlot, mouseClick, (short) actionId, null, false));
                return;
            }

            player.getInventory().setLastSlot(slot);

            if (slot == -999 && param
                    != SpreadItemParam.LEFT_MOUSE_ADD_SLOT && param
                    != SpreadItemParam.LEFT_MOUSE_BEGIN_DRAG && param
                    != SpreadItemParam.LEFT_MOUSE_END_DRAG && param
                    != SpreadItemParam.RIGHT_MOUSE_ADD_SLOT && param
                    != SpreadItemParam.RIGHT_MOUSE_BEGIN_DRAG && param
                    != SpreadItemParam.RIGHT_MOUSE_END_DRAG) {

                betaSession.sendPacket(new WindowClickPacketData(windowId, slot, mouseClick, (short) actionId, null, false));
                return;
            }

            if (item == null) {
                BetaItemStack lastItem = Utils.itemStackToBetaItemStack(player.getInventory().getItem(slot));
                betaSession.sendPacket(new WindowClickPacketData(windowId, slot, mouseClick, (short) actionId, lastItem, shiftPressed));
                return;
            }

            BetaItemStack itemStack = Utils.itemStackToBetaItemStack(item);
            betaSession.sendPacket(new WindowClickPacketData(windowId, slot, mouseClick, (short) actionId, itemStack, shiftPressed));
        });
    }
}
