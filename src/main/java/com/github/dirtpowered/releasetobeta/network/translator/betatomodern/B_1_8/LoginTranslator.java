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

package com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_8;

import com.github.dirtpowered.betaprotocollib.packet.Version_B1_8.data.LoginPacketData;
import com.github.dirtpowered.releasetobeta.ReleaseToBeta;
import com.github.dirtpowered.releasetobeta.data.ProtocolState;
import com.github.dirtpowered.releasetobeta.data.mapping.StaticValues;
import com.github.dirtpowered.releasetobeta.data.player.ModernPlayer;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.dirtpowered.releasetobeta.utils.Utils;
import com.github.steveice10.mc.protocol.data.game.entity.player.GameMode;
import com.github.steveice10.mc.protocol.data.game.setting.Difficulty;
import com.github.steveice10.mc.protocol.data.game.world.WorldType;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerDifficultyPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerJoinGamePacket;
import com.github.steveice10.packetlib.Session;

public class LoginTranslator implements BetaToModern<LoginPacketData> {

    @Override
    public void translate(ReleaseToBeta main, LoginPacketData packet, BetaClientSession session, Session modernSession) {
        ModernPlayer player = session.getPlayer();
        int entityId = packet.getEntityId();
        int dimension = Utils.fixDimension(packet.getDimension());

        int maxPlayers = packet.getMaxPlayers();
        GameMode gameMode = packet.getGamemode() == 0 ? GameMode.SURVIVAL : GameMode.CREATIVE;
        Difficulty difficulty = StaticValues.getDifficulty(packet.getDifficulty());

        session.setProtocolState(ProtocolState.PLAY);
        modernSession.send(new ServerJoinGamePacket(
                entityId,
                false,
                gameMode,
                dimension,
                0,
                maxPlayers,
                WorldType.DEFAULT,
                32,
                false,
                true
        ));

        modernSession.send(new ServerDifficultyPacket(difficulty, false));

        player.setEntityId(entityId);
        player.setDimension(dimension);
        player.setDifficulty(packet.getDifficulty());
        player.setGamemode(packet.getGamemode());
        player.setWorldHeight(packet.getWorldHeight());
        player.setSeed(packet.getSeed());
        session.joinPlayer();
    }
}
