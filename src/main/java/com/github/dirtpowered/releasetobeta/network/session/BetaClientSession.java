package com.github.dirtpowered.releasetobeta.network.session;

import com.github.dirtpowered.betaprotocollib.model.Packet;
import com.github.dirtpowered.betaprotocollib.packet.data.KeepAlivePacketData;
import com.github.dirtpowered.betaprotocollib.packet.data.StatisticsPacketData;
import com.github.dirtpowered.releasetobeta.ReleaseToBeta;
import com.github.dirtpowered.releasetobeta.data.ProtocolState;
import com.github.dirtpowered.releasetobeta.data.entity.EntityCache;
import com.github.dirtpowered.releasetobeta.data.player.BetaPlayer;
import com.github.dirtpowered.releasetobeta.data.player.ModernPlayer;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.dirtpowered.releasetobeta.utils.Tickable;
import com.github.steveice10.mc.protocol.data.game.PlayerListEntry;
import com.github.steveice10.mc.protocol.data.game.PlayerListEntryAction;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerPlayerListEntryPacket;
import com.github.steveice10.packetlib.Session;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.pmw.tinylog.Logger;

import java.util.Arrays;
import java.util.List;

public class BetaClientSession extends SimpleChannelInboundHandler<Packet> implements Tickable {

    private final Channel channel;
    private ReleaseToBeta releaseToBeta;
    private ProtocolState protocolState;
    private ModernPlayer player;
    private Session session;
    private boolean loggedIn;
    private List<Class<? extends Packet>> packetsToSkip;
    //private Map<UUID, PlayerListEntry> betaPlayers = new HashMap<>();
    private EntityCache entityCache;

    public BetaClientSession(ReleaseToBeta server, Channel channel, Session session) {
        this.releaseToBeta = server;
        this.channel = channel;
        this.protocolState = ProtocolState.LOGIN;
        this.player = new ModernPlayer(this);
        this.session = session;
        this.entityCache = new EntityCache();

        packetsToSkip = Arrays.asList(
                StatisticsPacketData.class,
                KeepAlivePacketData.class
        );
    }

    public void createSession(String clientId) {
        releaseToBeta.getSessionRegistry().addSession(clientId, new MultiSession(this, session));
        player.setClientId(clientId);

    }

    @SuppressWarnings("unchecked")
    private void processPacket(Packet packet) {
        BetaToModern handler = releaseToBeta.getBetaToModernTranslatorRegistry().getByPacket(packet);
        if (handler != null && channel.isActive()) {
            handler.translate(packet, this, releaseToBeta.getSessionRegistry().getSession(player.getClientId()).getModernSession());
        } else if (!packetsToSkip.contains(packet.getClass())) {
            Logger.warn("[client={}] missing 'BetaToModern' translator for {}", getClientId().substring(0, 8),
                    packet.getClass().getSimpleName());
        }
    }

    public void removeBetaTabEntry(BetaPlayer player) {
        //betaPlayers.remove(player.getUUID());

        ServerPlayerListEntryPacket entryPacket =
                new ServerPlayerListEntryPacket(PlayerListEntryAction.REMOVE_PLAYER, new PlayerListEntry[]{
                        new PlayerListEntry(player.getGameProfile())
                });

        getPlayer().sendPacket(entryPacket);
    }

    public void addBetaTabEntry(BetaPlayer player) {
        //betaPlayers.put(player.getUUID(), player.getTabEntry());

        ServerPlayerListEntryPacket entryPacket =
                new ServerPlayerListEntryPacket(PlayerListEntryAction.ADD_PLAYER, new PlayerListEntry[]{
                        player.getTabEntry()
                });

        getPlayer().sendPacket(entryPacket);
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
        quitPlayer();
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause) {
        Logger.warn("[client] closed connection: {}", cause.getMessage());
        player.kick("connection closed");
        context.close();
    }

    public String getClientId() {
        return player.getClientId();
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
        if (channel.isActive())
            channel.close();
    }

    public EntityCache getEntityCache() {
        return entityCache;
    }

    private boolean isLoggedIn() {
        return loggedIn;
    }

    private void setLoggedIn() {
        this.loggedIn = true;
    }

    private void quitPlayer() {
        releaseToBeta.getServer().removeTabEntry(player);
        //betaPlayers.clear();
        getEntityCache().getEntities().clear();
    }

    public void joinPlayer() {
        if (!isLoggedIn()) {
            releaseToBeta.getServer().addTabEntry(player);
            setLoggedIn();
        }
    }
}
