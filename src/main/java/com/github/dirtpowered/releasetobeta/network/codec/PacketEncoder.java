package com.github.dirtpowered.releasetobeta.network.codec;

import com.github.dirtpowered.betaprotocollib.model.AbstractPacket;
import com.github.dirtpowered.betaprotocollib.model.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

public class PacketEncoder extends MessageToMessageEncoder {

    @SuppressWarnings("unchecked")
    @Override
    protected void encode(ChannelHandlerContext ctx, Object message, List out) throws Exception {
        if (message instanceof Packet) {
            Packet packet = (Packet) message;
            //Class<? extends Packet> clazz = packet.getClass();

            AbstractPacket abstractPacket = (AbstractPacket) packet.getPacketClass().newInstance();
            //Logger.info("sending {} packet", clazz.getSimpleName());


            ByteBuf packetId = Unpooled.buffer(1);
            packetId.writeByte(abstractPacket.getPacketId());

            out.add(Unpooled.wrappedBuffer(packetId, abstractPacket.writePacketData(packet)));
        }

        out.add(message);
    }
}
