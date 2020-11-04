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

import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.HandshakePacketData;
import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.LoginPacketData;
import com.github.dirtpowered.releasetobeta.ReleaseToBeta;
import com.github.dirtpowered.releasetobeta.configuration.R2BConfiguration;
import com.github.dirtpowered.releasetobeta.data.ProtocolState;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.ModernToBeta;
import com.github.steveice10.mc.protocol.packet.login.client.LoginStartPacket;
import com.github.steveice10.packetlib.Session;

import java.net.InetSocketAddress;

public class LoginStartTranslator implements ModernToBeta<LoginStartPacket> {

    @Override
    public void translate(ReleaseToBeta main, LoginStartPacket packet, Session modernSession, BetaClientSession betaSession) {
        String username = packet.getUsername();
        if (betaSession.getProtocolState() != ProtocolState.LOGIN)
            return;

        boolean flag = R2BConfiguration.ipForwarding;

        InetSocketAddress socketAddress = (InetSocketAddress) modernSession.getRemoteAddress();

        long address = flag ? serializeAddress(socketAddress.getAddress().getHostAddress()) : 0;
        byte header = (byte) (flag ? -999 : 1);

        betaSession.getPlayer().fillProfile(username, result -> {
            betaSession.sendPacket(new HandshakePacketData(username));
            betaSession.sendPacket(new LoginPacketData(R2BConfiguration.version.getProtocolVersion(), username, address, header));
        });
    }

    private long serializeAddress(String address) {
        String[] ipAddressInArray = address.split("\\.");

        long result = 0;

        // https://mkyong.com/java/java-convert-ip-address-to-decimal-number/
        for (int i = 0; i < ipAddressInArray.length; i++) {
            int power = 3 - i;
            int ip = Integer.parseInt(ipAddressInArray[i]);

            result += ip * Math.pow(256, power);
        }

        return result;
    }
}
