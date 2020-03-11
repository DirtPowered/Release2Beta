package com.github.dirtpowered.releasetobeta.network.translator.betatomodern;

import com.github.dirtpowered.betaprotocollib.packet.data.PlayNoteblockPacketData;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.dirtpowered.releasetobeta.utils.Utils;
import com.github.steveice10.mc.protocol.data.MagicValues;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.Position;
import com.github.steveice10.mc.protocol.data.game.world.block.value.NoteBlockValue;
import com.github.steveice10.mc.protocol.data.game.world.block.value.NoteBlockValueType;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerBlockValuePacket;
import com.github.steveice10.packetlib.Session;

public class PlayNoteblockTranslator implements BetaToModern<PlayNoteblockPacketData> {

    @Override
    public void translate(PlayNoteblockPacketData packet, BetaClientSession session, Session modernSession) {
        Utils.debug(packet);

        int x = packet.getX();
        int y = packet.getY();
        int z = packet.getZ();
        Position position = new Position(x, y, z);

        NoteBlockValueType type = MagicValues.key(NoteBlockValueType.class, packet.getInstrumentType());
        int pitch = packet.getPitch();

        modernSession.send(new ServerBlockValuePacket(position, type, new NoteBlockValue(pitch), 25));
    }
}
