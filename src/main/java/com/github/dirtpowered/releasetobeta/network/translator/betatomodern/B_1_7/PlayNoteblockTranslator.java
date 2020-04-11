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
