package com.github.dirtpowered.releasetobeta.network.server.ping.LegacyPing;

import com.github.dirtpowered.releasetobeta.configuration.R2BConfiguration;
import com.github.dirtpowered.releasetobeta.network.codec.PipelineFactory;
import com.github.dirtpowered.releasetobeta.network.server.ping.LegacyPing.model.PingMessage;
import com.github.dirtpowered.releasetobeta.utils.Callback;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.NoArgsConstructor;
import org.pmw.tinylog.Logger;

import java.net.InetSocketAddress;

@NoArgsConstructor
class LegacyPingConnector {

    void connect(Callback<PingMessage> pingMessageCallback) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap clientBootstrap = new Bootstrap();

            clientBootstrap.group(group);
            clientBootstrap.channel(NioSocketChannel.class);
            clientBootstrap.remoteAddress(new InetSocketAddress(R2BConfiguration.remoteAddress, R2BConfiguration.remotePort));
            clientBootstrap.handler(new ChannelInitializer<SocketChannel>() {
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast("mc_pipeline", new PipelineFactory());
                    socketChannel.pipeline().addLast("legacy_ping_handler", new LegacyPingHandler(pingMessageCallback));
                }
            });
            ChannelFuture channelFuture = clientBootstrap.connect().sync();
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            Logger.warn("unable to ping remote server!");
        } finally {
            group.shutdownGracefully().sync();
        }
    }
}
