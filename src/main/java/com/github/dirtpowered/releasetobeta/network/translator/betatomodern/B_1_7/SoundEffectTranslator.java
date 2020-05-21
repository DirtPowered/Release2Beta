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

import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.SoundEffectPacketData;
import com.github.dirtpowered.releasetobeta.data.mapping.SoundEffectMap;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.dirtpowered.releasetobeta.utils.Utils;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.Position;
import com.github.steveice10.mc.protocol.data.game.world.block.BlockState;
import com.github.steveice10.mc.protocol.data.game.world.effect.BreakBlockEffectData;
import com.github.steveice10.mc.protocol.data.game.world.effect.ParticleEffect;
import com.github.steveice10.mc.protocol.data.game.world.effect.RecordEffectData;
import com.github.steveice10.mc.protocol.data.game.world.effect.SoundEffect;
import com.github.steveice10.mc.protocol.data.game.world.effect.WorldEffect;
import com.github.steveice10.mc.protocol.data.game.world.effect.WorldEffectData;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerPlayEffectPacket;
import com.github.steveice10.packetlib.Session;

public class SoundEffectTranslator implements BetaToModern<SoundEffectPacketData> {

    @Override
    public void translate(SoundEffectPacketData packet, BetaClientSession session, Session modernSession) {
        Utils.debug(session.getMain().getLogger(), packet);
        SoundEffectMap soundEffectMap = session.getMain().getSoundEffectMap();

        int id = packet.getSoundType();
        int data = packet.getData();

        Position pos = new Position(packet.getX(), packet.getY(), packet.getZ());
        WorldEffect worldEffect = soundEffectMap.getFromId(id);

        if (worldEffect instanceof SoundEffect) {
            if (worldEffect == SoundEffect.RECORD) {
                modernSession.send(new ServerPlayEffectPacket(worldEffect, pos, new RecordEffectData(data) {

                }));
                return;
            }

            modernSession.send(new ServerPlayEffectPacket(worldEffect, pos, new WorldEffectData() {

            }));
        } else if (worldEffect instanceof ParticleEffect) {
            modernSession.send(new ServerPlayEffectPacket(worldEffect, pos, new BreakBlockEffectData(new BlockState(session.remapBlock(data, 0, false), 0))));
        }
    }
}
