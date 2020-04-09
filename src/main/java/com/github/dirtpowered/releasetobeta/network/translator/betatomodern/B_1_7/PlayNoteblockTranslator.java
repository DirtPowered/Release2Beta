package com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_7;

import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.PlayNoteblockPacketData;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.steveice10.mc.protocol.data.MagicValues;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.Position;
import com.github.steveice10.mc.protocol.data.game.world.block.value.NoteBlockValue;
import com.github.steveice10.mc.protocol.data.game.world.block.value.NoteBlockValueType;
import com.github.steveice10.mc.protocol.data.game.world.sound.BuiltinSound;
import com.github.steveice10.mc.protocol.data.game.world.sound.SoundCategory;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerBlockValuePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerPlayBuiltinSoundPacket;
import com.github.steveice10.packetlib.Session;

public class PlayNoteblockTranslator implements BetaToModern<PlayNoteblockPacketData> {

    @Override
    public void translate(PlayNoteblockPacketData packet, BetaClientSession session, Session modernSession) {
        int x = packet.getX();
        int y = packet.getY();
        int z = packet.getZ();
        Position position = new Position(x, y, z);

        NoteBlockValueType type = MagicValues.key(NoteBlockValueType.class, packet.getInstrumentType());
        int pitch = packet.getPitch();
        if (pitch > 24) //that should fix rare case when opening small chest disconnects all players
            return;

        BuiltinSound builtinSound;

        switch (type) {
            case HARP:
                builtinSound = BuiltinSound.BLOCK_NOTE_HARP;
                break;
            case DOUBLE_BASS:
                builtinSound = BuiltinSound.BLOCK_NOTE_BASS;
                break;
            case SNARE_DRUM:
                builtinSound = BuiltinSound.BLOCK_NOTE_SNARE;
                break;
            case HI_HAT:
                builtinSound = BuiltinSound.BLOCK_NOTE_HAT;
                break;
            case BASS_DRUM:
                builtinSound = BuiltinSound.BLOCK_NOTE_BASEDRUM;
                break;
            case FLUTE:
                builtinSound = BuiltinSound.BLOCK_NOTE_FLUTE;
                break;
            case BELL:
                builtinSound = BuiltinSound.BLOCK_NOTE_BELL;
                break;
            case GUITAR:
                builtinSound = BuiltinSound.BLOCK_NOTE_GUITAR;
                break;
            case CHIME:
                builtinSound = BuiltinSound.BLOCK_NOTE_CHIME;
                break;
            case XYLOPHONE:
                builtinSound = BuiltinSound.BLOCK_NOTE_XYLOPHONE;
                break;
            default:
                builtinSound = BuiltinSound.BLOCK_NOTE_BASEDRUM;
                break;
        }

        modernSession.send(new ServerPlayBuiltinSoundPacket(builtinSound, SoundCategory.RECORD, x, y, z, 3.0f, pitch));
        modernSession.send(new ServerBlockValuePacket(position, type, new NoteBlockValue(pitch), 25));
    }
}
