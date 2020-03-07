package com.github.dirtpowered.releasetobeta.network.codec;

import com.github.dirtpowered.betaprotocollib.BetaLib;
import com.github.dirtpowered.betaprotocollib.model.AbstractPacket;
import com.github.dirtpowered.betaprotocollib.model.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import org.pmw.tinylog.Logger;

import java.io.IOException;
import java.util.List;

public class PacketDecoder extends ReplayingDecoder<Packet> {

    @Override
    protected void decode(ChannelHandlerContext context, ByteBuf buffer, List<Object> list) throws IOException, IllegalAccessException, InstantiationException {
        final int packetId = buffer.readUnsignedByte();
        if (packetId == 0x7F)
            return;

        if (!BetaLib.getRegistry().hasId(packetId)) {
            Logger.warn("Packet {} is not registered", packetId);
            list.add(Unpooled.EMPTY_BUFFER);
            return;
        }

        if (BetaLib.getRegistry().getFromId(packetId) == null)
            return;

        AbstractPacket abstractPacket = BetaLib.getRegistry().getFromId(packetId).newInstance();

        Packet o = abstractPacket.readPacketData(buffer);
        /*if (!o.getClass().getSimpleName().contains("hunk"))
            Logger.info("received packet: {}", ReflectionToStringBuilder.toString(o, ToStringStyle.SHORT_PREFIX_STYLE));*/
        list.add(o);
    }
}
