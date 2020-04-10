package com.github.dirtpowered.releasetobeta.network.server.login;

import com.github.dirtpowered.releasetobeta.ReleaseToBeta;
import com.github.dirtpowered.releasetobeta.configuration.R2BConfiguration;
import com.github.dirtpowered.releasetobeta.network.codec.PipelineFactory;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.steveice10.mc.protocol.ServerLoginHandler;
import com.github.steveice10.packetlib.Session;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.commons.lang3.RandomStringUtils;
import org.pmw.tinylog.Logger;

import java.net.InetSocketAddress;

public class LoginHandler implements ServerLoginHandler {
    private ReleaseToBeta main;

    public LoginHandler(ReleaseToBeta releaseToBeta) {
        this.main = releaseToBeta;
    }

    @Override
    public void loggedIn(Session session) {
        try {
            if (session.isConnected()) {
                createClientSession(RandomStringUtils.randomAlphabetic(8), session);
            }
        } catch (InterruptedException e) {
            Logger.error(e.getMessage());
        }
    }

    private void createClientSession(String clientId, Session session) throws InterruptedException {
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
                    ch.pipeline().addLast("mc_pipeline", new PipelineFactory());
                    BetaClientSession clientSession = new BetaClientSession(main, ch, session, clientId);

                    clientSession.createSession();
                    ch.pipeline().addLast("client_connection_handler", clientSession);
                }
            });

            ChannelFuture channelFuture = clientBootstrap.connect().sync();
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            session.disconnect(e.getMessage());
            main.getSessionRegistry().removeSession(main.getSessionRegistry().getClientSessionFromServerSession(session).getClientId());
        } finally {
            loopGroup.shutdownGracefully().sync();
        }
    }
}
