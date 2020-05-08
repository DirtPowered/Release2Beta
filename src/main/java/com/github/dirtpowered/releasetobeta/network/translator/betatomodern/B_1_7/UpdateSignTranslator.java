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

package com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_7;

import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.UpdateSignPacketData;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.dirtpowered.releasetobeta.utils.ChatUtils;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.Position;
import com.github.steveice10.mc.protocol.data.game.world.block.UpdatedTileType;
import com.github.steveice10.mc.protocol.data.message.ChatColor;
import com.github.steveice10.mc.protocol.data.message.MessageStyle;
import com.github.steveice10.mc.protocol.data.message.TextMessage;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerUpdateTileEntityPacket;
import com.github.steveice10.opennbt.tag.builtin.CompoundTag;
import com.github.steveice10.opennbt.tag.builtin.IntTag;
import com.github.steveice10.opennbt.tag.builtin.StringTag;
import com.github.steveice10.opennbt.tag.builtin.Tag;
import com.github.steveice10.packetlib.Session;

import java.util.HashMap;
import java.util.Map;

public class UpdateSignTranslator implements BetaToModern<UpdateSignPacketData> {

    @Override
    public void translate(UpdateSignPacketData packet, BetaClientSession session, Session modernSession) {
        int x = packet.getX();
        int y = packet.getY();
        int z = packet.getZ();

        Position pos = new Position(x, y, z);
        String[] lines = packet.getSignLines();

        Map<String, Tag> nbt = new HashMap<>();
        nbt.put("id", new StringTag("id", "minecraft:sign"));

        nbt.put("x", new IntTag("x", x));
        nbt.put("y", new IntTag("y", y));
        nbt.put("z", new IntTag("z", z));

        for (int line = 0; line < 4; ++line) {
            nbt.put("Text" + (line + 1), new StringTag("Text" + (line + 1),
                    TextMessage.fromString(ChatUtils.colorize(lines[line])).setStyle(new MessageStyle().setColor(ChatColor.RESET)).toJsonString()));
        }

        CompoundTag tag = new CompoundTag("", nbt);
        modernSession.send(new ServerUpdateTileEntityPacket(pos, UpdatedTileType.SIGN, tag));
    }
}
