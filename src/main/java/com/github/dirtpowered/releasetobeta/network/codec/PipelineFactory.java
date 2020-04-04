package com.github.dirtpowered.releasetobeta.network.codec;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.timeout.ReadTimeoutHandler;

public class PipelineFactory extends ChannelInitializer {

    @Override
    protected void initChannel(Channel channel) {
        channel.pipeline().addLast("decoder", new PacketDecoder());
        channel.pipeline().addLast("encoder", new PacketEncoder());
        channel.pipeline().addLast("timeout", new ReadTimeoutHandler(30));
    }
}