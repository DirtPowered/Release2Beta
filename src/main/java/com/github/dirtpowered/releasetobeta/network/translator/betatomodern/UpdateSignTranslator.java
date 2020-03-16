package com.github.dirtpowered.releasetobeta.network.translator.betatomodern;

import com.github.dirtpowered.betaprotocollib.packet.data.UpdateSignPacketData;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
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
                    TextMessage.fromString(lines[line]).setStyle(new MessageStyle().setColor(ChatColor.RESET)).toJsonString()));
        }

        CompoundTag tag = new CompoundTag("", nbt);
        modernSession.send(new ServerUpdateTileEntityPacket(pos, UpdatedTileType.SIGN, tag));
    }
}
