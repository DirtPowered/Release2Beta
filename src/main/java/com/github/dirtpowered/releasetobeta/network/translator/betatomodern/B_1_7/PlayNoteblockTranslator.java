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
import com.github.steveice10.mc.protocol.data.game.entity.metadata.Position;
import com.github.steveice10.mc.protocol.data.game.world.block.value.BlockValueType;
import com.github.steveice10.mc.protocol.data.game.world.block.value.GenericBlockValue;
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

        int pitch = packet.getPitch();

        BuiltinSound builtinSound;
        BlockValueType type;

        switch (packet.getInstrumentType()) {
            case 0:
                builtinSound = BuiltinSound.BLOCK_NOTE_HARP;
                type = NoteBlockValueType.HARP;
                break;
            case 1:
                /*builtinSound = BuiltinSound.BLOCK_NOTE_BASEDRUM;
                type = NoteBlockValueType.BASS_DRUM;*/

                //Opening chest fix
                builtinSound = BuiltinSound.BLOCK_NOTE_BASS;
                type = NoteBlockValueType.DOUBLE_BASS;
                break;
            case 2:
                builtinSound = BuiltinSound.BLOCK_NOTE_SNARE;
                type = NoteBlockValueType.SNARE_DRUM;
                break;
            case 3:
                builtinSound = BuiltinSound.BLOCK_NOTE_HAT;
                type = NoteBlockValueType.HI_HAT;
                break;
            case 4:
                builtinSound = BuiltinSound.BLOCK_NOTE_BASS;
                type = NoteBlockValueType.DOUBLE_BASS;
                break;
            default:
                builtinSound = BuiltinSound.BLOCK_NOTE_HARP;
                type = NoteBlockValueType.HARP;
                break;
        }

        modernSession.send(new ServerPlayBuiltinSoundPacket(builtinSound, SoundCategory.RECORD, x, y, z, 3.0f, pitch));
        modernSession.send(new ServerBlockValuePacket(position, type, new GenericBlockValue(pitch), 25));
    }
}
