package com.github.dirtpowered.releasetobeta.network.server.ping.LegacyPing;

import com.github.dirtpowered.betaprotocollib.model.Packet;
import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.KickDisconnectPacketData;
import com.github.dirtpowered.betaprotocollib.packet.Version_B1_8.data.ServerListPingPacketData;
import com.github.dirtpowered.releasetobeta.network.server.ping.LegacyPing.model.PingMessage;
import com.github.dirtpowered.releasetobeta.utils.Callback;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class LegacyPingHandler extends SimpleChannelInboundHandler<Packet> {
    private Callback<PingMessage> pingMessageCallback;

    LegacyPingHandler(Callback<PingMessage> pingMessageCallback) {
        this.pingMessageCallback = pingMessageCallback;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet packet) {
        if (packet instanceof KickDisconnectPacketData) {
            pingMessageCallback.onComplete(new PingMessage(((KickDisconnectPacketData) packet).getDisconnectReason()));
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(new ServerListPingPacketData());

        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause) {
        context.close();
    }
}
