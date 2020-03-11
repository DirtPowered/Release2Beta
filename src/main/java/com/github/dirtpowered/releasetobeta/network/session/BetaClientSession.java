package com.github.dirtpowered.releasetobeta.network.session;

import com.github.dirtpowered.betaprotocollib.model.Packet;
import com.github.dirtpowered.releasetobeta.ReleaseToBeta;
import com.github.dirtpowered.releasetobeta.data.ProtocolState;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.dirtpowered.releasetobeta.utils.Tickable;
import com.github.steveice10.packetlib.Session;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.pmw.tinylog.Logger;

import java.util.UUID;

public class BetaClientSession extends SimpleChannelInboundHandler<Packet> implements Tickable {

    private final Channel channel;
    private ReleaseToBeta releaseToBeta;
    private ProtocolState protocolState;
    private ModernPlayer player;
    private UUID clientId;

    public BetaClientSession(ReleaseToBeta server, Channel channel, Session session) {
        this.releaseToBeta = server;
        this.channel = channel;
        this.protocolState = ProtocolState.LOGIN;
        this.player = new ModernPlayer(this);
        this.clientId = UUID.randomUUID();

        server.getSessionRegistry().addSession(this, session);
    }

    @SuppressWarnings("unchecked")
    private void processPacket(Packet packet) {
        BetaToModern handler = releaseToBeta.getBetaToModernTranslatorRegistry().getByPacket(packet);
        if (handler != null && channel.isActive()) {
            handler.translate(packet, this, releaseToBeta.getSessionRegistry().getSessions().inverse().get(this));
        } else {
            Logger.warn("[client={}] missing 'BetaToModern' translator for {}", getClientId().toString().substring(0, 8),
                    packet.getClass().getSimpleName());
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
        //Logger.info("Processing packet {} from clientID: {}", packet.getPacketClass().getSimpleName(), clientId);
        processPacket(packet);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Logger.info("[client] connected");

        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Logger.info("[client] disconnected");
        ctx.close();

        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause) throws Exception {
        Logger.warn("[client] closed connection: {}", cause.getMessage());
        context.close();

        super.exceptionCaught(context, cause);
    }

    public UUID getClientId() {
        return clientId;
    }

    public void sendPacket(Packet packet) {
        if (channel.isActive())
            channel.writeAndFlush(packet);
    }

    public ModernPlayer getPlayer() {
        return player;
    }

    @Override
    public void tick() {
    }

    public ReleaseToBeta getMain() {
        return releaseToBeta;
    }

    public void disconnect() {
        channel.close();
    }
}
