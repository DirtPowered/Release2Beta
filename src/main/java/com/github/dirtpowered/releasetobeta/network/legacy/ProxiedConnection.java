package com.github.dirtpowered.releasetobeta.network.legacy;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.pmw.tinylog.Logger;

public class ProxiedConnection extends ChannelInboundHandlerAdapter {

    ProxiedConnection() {

    }

    @Override
    public void channelRead(ChannelHandlerContext channelhandlercontext, Object object) throws Exception {
        ByteBuf buffer = (ByteBuf) object;
        boolean oldPacket = true;

        try {
            if (buffer.readUnsignedByte() == 0x02) { //handshake
                if (buffer.readableBytes() == 24) {
                    Logger.info("detected legacy client");
                }
                buffer.release();
                oldPacket = false;
            }
        } finally {
            if (oldPacket) {
                buffer.resetReaderIndex();
                channelhandlercontext.channel().pipeline().remove("proxied_connection");
                channelhandlercontext.fireChannelRead(object);
            }
        }
    }
}
