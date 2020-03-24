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
import com.github.steveice10.mc.protocol.data.game.entity.metadata.Position;
import com.github.steveice10.mc.protocol.data.game.world.block.BlockChangeRecord;
import com.github.steveice10.mc.protocol.data.game.world.block.BlockState;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerPlayerListEntryPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerBlockChangePacket;
import com.github.steveice10.packetlib.Session;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.pmw.tinylog.Logger;

import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;

public class BetaClientSession extends SimpleChannelInboundHandler<Packet> implements Tickable {

    private final Channel channel;
    private ReleaseToBeta releaseToBeta;
    private ProtocolState protocolState;
    private ModernPlayer player;
    private Session session;
    private boolean loggedIn;
    private List<Class<? extends Packet>> packetsToSkip;
    private EntityCache entityCache;
    private Deque<BlockChangeRecord> blockChangeQueue = new LinkedList<>();
    private Deque<Packet> initialPacketsQueue = new LinkedBlockingDeque<>();

    private int tickLimiter = 0;

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
        ServerPlayerListEntryPacket entryPacket =
                new ServerPlayerListEntryPacket(PlayerListEntryAction.REMOVE_PLAYER, new PlayerListEntry[]{
                        new PlayerListEntry(player.getGameProfile())
                });

        getPlayer().sendPacket(entryPacket);
    }

    public void addBetaTabEntry(BetaPlayer player) {
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
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Logger.info("[client] connected");

        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Logger.info("[client] disconnected");
        quitPlayer();

        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause) throws Exception {
        Logger.warn("[client] closed connection: {}", cause.getMessage());

        super.exceptionCaught(context, cause);
    }

    public String getClientId() {
        return player.getClientId();
    }

    public void sendPacket(Packet packet) {
        if (channel.isActive())
            channel.writeAndFlush(packet);
        else {
            Logger.warn("channel is not ready, queueing packet '{}'", packet.getPacketClass().getSimpleName());
            initialPacketsQueue.add(packet);
        }
    }

    public ModernPlayer getPlayer() {
        return player;
    }

    @Override
    public void tick() {
        tickLimiter = (tickLimiter + 1) % 2;
        if (tickLimiter == 0) {
            if (channel.isActive() && !initialPacketsQueue.isEmpty()) {
                Packet p = initialPacketsQueue.poll();
                Logger.info("sending queued packet: {}", p.getPacketClass().getSimpleName());

                channel.writeAndFlush(p);
                return;
            }

            //sending block change packets immediately may cause problems, so delay it
            poolTileEntityQueue();
        }
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
        releaseToBeta.getSessionRegistry().removeSession(player.getClientId());

        initialPacketsQueue.clear();
        blockChangeQueue.clear();

        entityCache.getEntities().clear();
    }

    public void joinPlayer() {
        if (!isLoggedIn()) {
            releaseToBeta.getServer().addTabEntry(player);
            setLoggedIn();
        }
    }

    private void poolTileEntityQueue() {
        if (blockChangeQueue.isEmpty())
            return;

        int allowance = Math.min(1, blockChangeQueue.size());

        for (int i = 0; i < allowance; i++) {
            BlockChangeRecord block = blockChangeQueue.remove();
            if (block == null)
                return;

            player.sendPacket(new ServerBlockChangePacket(block));
        }
    }

    public void queueBlockChange(BlockChangeRecord blockChangeRecord) {
        blockChangeQueue.add(blockChangeRecord);
    }

    public void sendBlockUpdate(Position pos, int id, int data) {
        queueBlockChange(new BlockChangeRecord(pos, new BlockState(id, data)));
    }
}
