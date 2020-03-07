package com.github.dirtpowered.releasetobeta.network.session;

import com.github.dirtpowered.betaprotocollib.model.Packet;
import com.github.dirtpowered.releasetobeta.ReleaseToBeta;
import com.github.dirtpowered.releasetobeta.data.ProtocolState;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.dirtpowered.releasetobeta.utils.Tickable;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.pmw.tinylog.Logger;

public class BetaClientSession extends SimpleChannelInboundHandler<Packet> implements Tickable {

    private final Channel channel;
    private ReleaseToBeta releaseToBeta;
    private ProtocolState protocolState;
    private ModernPlayer player;

    public BetaClientSession(ReleaseToBeta server, Channel channel) {
        this.releaseToBeta = server;
        this.channel = channel;
        this.protocolState = ProtocolState.LOGIN;
        this.player = new ModernPlayer(this);
    }

    @SuppressWarnings("unchecked")
    private void processPacket(Packet packet) {
        BetaToModern handler = releaseToBeta.getBetaToModernTranslatorRegistry().getByPacket(packet);

        if (handler != null) {
            handler.translate(packet, this, releaseToBeta.getSessionRegistry().getSessions().inverse().get(this));
            //Logger.info("[client] translating {}", packet.getClass().getSimpleName());
        } else {
            if (!packet.getClass().getSimpleName().contains("ntity"))
                Logger.warn("[client] missing 'BetaToModern' translator for {}", packet.getClass().getSimpleName());
        }
    }

    public ProtocolState getProtocolState() {
        return protocolState;
    }

    public void setProtocolState(ProtocolState protocolState) {
        this.protocolState = protocolState;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet packet) {
        processPacket(packet);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        Logger.info("[client] connected");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        Logger.info("[client] disconnected");
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause) {
        Logger.warn("[client] closed connection: {}", cause.getLocalizedMessage());
        cause.printStackTrace();
        context.close();
    }

    public void sendPacket(Packet packet) {
        channel.writeAndFlush(packet);
    }

    public void joinPlayer() {
        player.sendMessage("test");
    }

    public ModernPlayer getPlayer() {
        return player;
    }

    @Override
    public void tick() {

    }

    ReleaseToBeta getMain() {
        return releaseToBeta;
    }

    public void disconnect() {
        channel.close();
    }
}
