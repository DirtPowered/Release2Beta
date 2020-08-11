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

package com.github.dirtpowered.releasetobeta.network.translator.moderntobeta.B_1_7;

import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.WindowClickPacketData;
import com.github.dirtpowered.releasetobeta.ReleaseToBeta;
import com.github.dirtpowered.releasetobeta.data.inventory.PlayerInventory;
import com.github.dirtpowered.releasetobeta.data.player.ModernPlayer;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.ModernToBeta;
import com.github.dirtpowered.releasetobeta.utils.item.ItemConverter;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.ItemStack;
import com.github.steveice10.mc.protocol.data.game.window.ClickItemParam;
import com.github.steveice10.mc.protocol.data.game.window.DropItemParam;
import com.github.steveice10.mc.protocol.data.game.window.SpreadItemParam;
import com.github.steveice10.mc.protocol.data.game.window.WindowAction;
import com.github.steveice10.mc.protocol.data.game.window.WindowActionParam;
import com.github.steveice10.mc.protocol.data.game.window.WindowType;
import com.github.steveice10.mc.protocol.packet.ingame.client.window.ClientWindowActionPacket;
import com.github.steveice10.packetlib.Session;

public class ClientWindowActionTranslator implements ModernToBeta<ClientWindowActionPacket> {

    @Override
    public void translate(ReleaseToBeta main, ClientWindowActionPacket packet, Session modernSession, BetaClientSession betaSession) {
        ModernPlayer player = betaSession.getPlayer();
        PlayerInventory inventory = player.getInventory();

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

        if (player.getOpenedInventoryType() == WindowType.GENERIC_INVENTORY && slot == 45 || droppingUsingQ) {
            player.updateInventory();
            return;
        }

        if (clickingOutside) {
            betaSession.sendPacket(new WindowClickPacketData(windowId, slot, mouseClick, (short) 0, null, false));
            inventory.setItem(inventory.getLastSlot(), new ItemStack(0));
            return;
        }

        ItemStack itemStack = packet.getClickedItem() == null ? (slot < 0 ? null : player.getInventory().getItem(slot)) : packet.getClickedItem();

        if (itemStack == null)
            return;

        betaSession.sendPacket(new WindowClickPacketData(windowId, slot, mouseClick, (short) 0, ItemConverter.itemStackToBetaItemStack(itemStack), usingShift));
        inventory.setLastSlot(slot);
    }
}
