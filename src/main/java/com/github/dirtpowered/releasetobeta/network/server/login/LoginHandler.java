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

package com.github.dirtpowered.releasetobeta.network.server.login;

import com.github.dirtpowered.releasetobeta.ReleaseToBeta;
import com.github.dirtpowered.releasetobeta.bootstrap.Platform;
import com.github.dirtpowered.releasetobeta.configuration.R2BConfiguration;
import com.github.dirtpowered.releasetobeta.network.codec.PipelineFactory;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.utils.chat.ChatUtils;
import com.github.steveice10.mc.protocol.ServerLoginHandler;
import com.github.steveice10.packetlib.Session;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.util.UUID;

public class LoginHandler implements ServerLoginHandler {
    private ReleaseToBeta main;
    private long lastLogin;

    public LoginHandler(ReleaseToBeta main) {
        this.main = main;
    }

    @Override
    public void loggedIn(Session session) {
        try {
            if (session.isConnected()) {
                if ((System.currentTimeMillis() - lastLogin) < R2BConfiguration.globalConnectionThrottle) {
                    session.disconnect(ChatUtils.colorize(R2BConfiguration.connectionThrottleKickMessage));
                    return;
                }

                this.lastLogin = System.currentTimeMillis();

                if (main.getBootstrap().getOnline() + 1 > R2BConfiguration.maxPlayers && main.getBootstrap().getPlatform() != Platform.BUKKIT) {
                    session.disconnect(ChatUtils.colorize(R2BConfiguration.serverFullMessage));
                    return;
                }

                UUID uniqueId = UUID.randomUUID();
                session.setFlag("uniqueId", uniqueId);

                createClientSession(uniqueId, session);
            }
        } catch (InterruptedException e) {
            main.getLogger().error(e.getMessage());
        }
    }

    private void createClientSession(UUID clientId, Session session) throws InterruptedException {
        NioEventLoopGroup loopGroup = new NioEventLoopGroup();

        try {
            Bootstrap clientBootstrap = new Bootstrap();

            clientBootstrap.group(loopGroup);
            clientBootstrap.channel(NioSocketChannel.class);
            clientBootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            clientBootstrap.option(ChannelOption.TCP_NODELAY, true);

            clientBootstrap.remoteAddress(new InetSocketAddress(R2BConfiguration.remoteAddress, R2BConfiguration.remotePort));
            clientBootstrap.handler(new ChannelInitializer<SocketChannel>() {

                @Override
                protected void initChannel(SocketChannel ch) {
                    ch.pipeline().addLast("mc_pipeline", new PipelineFactory(main));
                    ch.pipeline().addLast("client_connection_handler", new BetaClientSession(main, ch, session, clientId));
                }
            });

            ChannelFuture channelFuture = clientBootstrap.connect().sync();
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            session.disconnect(e.getMessage());
            main.getSessionRegistry().removeSession(clientId);
        } finally {
            loopGroup.shutdownGracefully().sync();
        }
    }
}
