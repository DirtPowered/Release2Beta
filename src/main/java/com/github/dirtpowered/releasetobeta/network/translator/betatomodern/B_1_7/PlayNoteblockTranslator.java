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
import com.github.dirtpowered.betaprotocollib.utils.Location;
import com.github.dirtpowered.releasetobeta.data.Constants;
import com.github.dirtpowered.releasetobeta.data.blockstorage.DataBlock;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.Position;
import com.github.steveice10.mc.protocol.data.game.world.block.value.BlockValueType;
import com.github.steveice10.mc.protocol.data.game.world.block.value.ChestValueType;
import com.github.steveice10.mc.protocol.data.game.world.block.value.GenericBlockValue;
import com.github.steveice10.mc.protocol.data.game.world.block.value.NoteBlockValueType;
import com.github.steveice10.mc.protocol.data.game.world.block.value.PistonValueType;
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

        int pitch = packet.getPitch();

        BuiltinSound builtinSound;
        BlockValueType type;

        Location l = new Location(x, y, z);

        double dist = l.distanceTo(session.getPlayer().getLocation());
        if (dist < Constants.SOUND_RANGE) {
            DataBlock b = session.getBlockStorage().getCachedBlockAt(l);

            int blockId = b.getBlockState().getId();
            switch (packet.getInstrumentType()) {
                case 0:
                    if (blockId == 33 || blockId == 29) {
                        builtinSound = BuiltinSound.BLOCK_PISTON_EXTEND;
                        type = PistonValueType.PUSHING;
                        pitch = 0;
                    } else {
                        builtinSound = BuiltinSound.BLOCK_NOTE_HARP;
                        type = NoteBlockValueType.HARP;
                    }
                    break;
                case 1:
                    if (blockId == 54) {
                        builtinSound = BuiltinSound.BLOCK_CHEST_OPEN;
                        type = ChestValueType.VIEWING_PLAYER_COUNT;
                    } else if (blockId == 33 || blockId == 29) {
                        builtinSound = BuiltinSound.BLOCK_PISTON_CONTRACT;
                        type = PistonValueType.PULLING;
                        pitch = 0;
                    } else {
                        builtinSound = BuiltinSound.BLOCK_NOTE_BASEDRUM;
                        type = NoteBlockValueType.BASS_DRUM;
                    }
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

            modernSession.send(new ServerPlayBuiltinSoundPacket(builtinSound, SoundCategory.BLOCK, x, y, z, 1.33f, pitch));
            modernSession.send(new ServerBlockValuePacket(new Position(x, y, z), type, new GenericBlockValue(pitch), blockId));
        }
    }
}
