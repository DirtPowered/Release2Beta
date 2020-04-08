package com.github.dirtpowered.releasetobeta.network.translator.moderntobeta.B_1_7;

import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.WindowClickPacketData;
import com.github.dirtpowered.releasetobeta.data.player.ModernPlayer;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.ModernToBeta;
import com.github.dirtpowered.releasetobeta.utils.TextColor;
import com.github.dirtpowered.releasetobeta.utils.Utils;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.ItemStack;
import com.github.steveice10.mc.protocol.data.game.window.ClickItemParam;
import com.github.steveice10.mc.protocol.data.game.window.DropItemParam;
import com.github.steveice10.mc.protocol.data.game.window.SpreadItemParam;
import com.github.steveice10.mc.protocol.data.game.window.WindowAction;
import com.github.steveice10.mc.protocol.data.game.window.WindowActionParam;
import com.github.steveice10.mc.protocol.packet.ingame.client.window.ClientWindowActionPacket;
import com.github.steveice10.packetlib.Session;

public class ClientWindowActionTranslator implements ModernToBeta<ClientWindowActionPacket> {

    @Override
    public void translate(ClientWindowActionPacket packet, Session modernSession, BetaClientSession betaSession) {
        betaSession.getMain().getScheduledExecutorService().execute(() -> {
            ModernPlayer player = betaSession.getPlayer();

            int windowId = packet.getWindowId();
            int slot = packet.getSlot();

            WindowActionParam actionParam = packet.getParam();
            WindowAction inventoryAction = packet.getAction();
            boolean leftClick = actionParam == ClickItemParam.LEFT_CLICK ||
                    actionParam == SpreadItemParam.LEFT_MOUSE_ADD_SLOT ||
                    actionParam == SpreadItemParam.LEFT_MOUSE_BEGIN_DRAG ||
                    actionParam == SpreadItemParam.LEFT_MOUSE_END_DRAG;

            boolean usingShift = inventoryAction == WindowAction.SHIFT_CLICK_ITEM;
            boolean droppingUsingQ = actionParam == DropItemParam.DROP_FROM_SELECTED && inventoryAction == WindowAction.DROP_ITEM;

            int mouseClick = leftClick ? 0 : 1;

            boolean clickingOutside = slot == -999 && inventoryAction != WindowAction.SPREAD_ITEM;

            if (clickingOutside) {
                betaSession.sendPacket(new WindowClickPacketData(windowId, slot, mouseClick, (short) 0, null, false));
                return;
            }

            ItemStack itemStack = packet.getClickedItem() == null ? player.getInventory().getItem(slot) : packet.getClickedItem();

            if (slot == 45 || droppingUsingQ) {
                player.closeInventory();
                player.updateInventory();

                player.sendMessage(TextColor.translate("&cunsupported operation"));
                return;
            }

            betaSession.sendPacket(new WindowClickPacketData(windowId, slot, mouseClick, (short) 0, Utils.itemStackToBetaItemStack(itemStack), usingShift));
            player.getInventory().setLastSlot(slot);
        });
    }
}
