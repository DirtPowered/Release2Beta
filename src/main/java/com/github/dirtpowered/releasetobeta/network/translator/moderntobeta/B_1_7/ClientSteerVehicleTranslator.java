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

import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.PlayerPositionPacketData;
import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.UseEntityPacketData;
import com.github.dirtpowered.releasetobeta.data.player.ModernPlayer;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.ModernToBeta;
import com.github.steveice10.mc.protocol.packet.ingame.client.world.ClientSteerVehiclePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntitySetPassengersPacket;
import com.github.steveice10.packetlib.Session;

public class ClientSteerVehicleTranslator implements ModernToBeta<ClientSteerVehiclePacket> {

    @Override
    public void translate(ClientSteerVehiclePacket packet, Session modernSession, BetaClientSession betaSession) {
        ModernPlayer player = betaSession.getPlayer();
        boolean dismount = packet.getDismounting();

        // Modern client sends this packet twice when leaving vehicle - we don't need that
        if (dismount && System.currentTimeMillis() - player.getLastInteractAtEntity() >= 100L) {
            betaSession.sendPacket(new UseEntityPacketData(player.getEntityId(), player.getVehicleEntityId(), false));
            modernSession.send(new ServerEntitySetPassengersPacket(player.getVehicleEntityId()));

            player.setLastInteractAtEntity(System.currentTimeMillis());
        } else if (!dismount) {
            /*
             * Controlling boats is far from perfect - it's almost unusable, but hey! It's working!
             * TODO: Fix that
             */
            float sideways = packet.getSideways() / 62.93781f;
            float forward = packet.getForward() / 62.93781f;

            betaSession.sendPacket(new PlayerPositionPacketData(sideways, -999.0D, forward, -999.0D, player.isOnGround()));
        }
    }
}
