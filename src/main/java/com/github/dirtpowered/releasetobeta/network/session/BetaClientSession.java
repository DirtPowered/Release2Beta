package com.github.dirtpowered.releasetobeta.network.session;

import com.github.dirtpowered.betaprotocollib.model.Packet;
import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.KeepAlivePacketData;
import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.StatisticsPacketData;
import com.github.dirtpowered.releasetobeta.ReleaseToBeta;
import com.github.dirtpowered.releasetobeta.configuration.R2BConfiguration;
import com.github.dirtpowered.releasetobeta.data.ProtocolState;
import com.github.dirtpowered.releasetobeta.data.entity.EntityCache;
import com.github.dirtpowered.releasetobeta.data.entity.TileEntity;
import com.github.dirtpowered.releasetobeta.data.mapping.BlockMap;
import com.github.dirtpowered.releasetobeta.data.mapping.MetadataMap;
import com.github.dirtpowered.releasetobeta.data.player.BetaPlayer;
import com.github.dirtpowered.releasetobeta.data.player.ModernPlayer;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.dirtpowered.releasetobeta.utils.TextColor;
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

import java.util.ArrayList;
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
    private List<BetaPlayer> playersInRange = new ArrayList<>();
    private boolean resourcepack;

    private String clientId;
    private int tickLimiter = 0;
    private int i;

    public BetaClientSession(ReleaseToBeta server, Channel channel, Session session, String clientId) {
        this.releaseToBeta = server;
        this.channel = channel;
        this.protocolState = ProtocolState.LOGIN;
        this.player = new ModernPlayer(this);
        this.session = session;
        this.entityCache = new EntityCache();
        this.clientId = clientId;

        packetsToSkip = Arrays.asList(
                StatisticsPacketData.class,
                KeepAlivePacketData.class
        );
    }

    public void createSession() {
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
        playersInRange.remove(player);
        ServerPlayerListEntryPacket entryPacket =
                new ServerPlayerListEntryPacket(PlayerListEntryAction.REMOVE_PLAYER, new PlayerListEntry[]{
                        new PlayerListEntry(player.getGameProfile())
                });

        getPlayer().sendPacket(entryPacket);
    }

    public void addBetaTabEntry(BetaPlayer player) {
        playersInRange.add(player);
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
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause) {
        Logger.warn("[client] closed connection: {}", cause.toString());

        cause.printStackTrace();
        context.close();
    }

    public String getClientId() {
        return clientId;
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
            if (channel.isActive() && player.getGameProfile() != null && !initialPacketsQueue.isEmpty()) {
                Packet p = initialPacketsQueue.poll();
                Logger.info("sending queued packet: {}", p.getPacketClass().getSimpleName());

                channel.writeAndFlush(p);
                return;
            }

            //wait 3 seconds to make sure client is ready to receive resourcepack packet
            if (i > 20 * 3 && !resourcepack && !R2BConfiguration.resourcePack.isEmpty()) {
                player.sendResourcePack();
                resourcepack = true;
            }
            //sending block change packets immediately may cause problems, so delay it
            poolTileEntityQueue();
        }

        i++;
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
        releaseToBeta.getServer().getServerConnection().getPlayerList().removeTabEntry(player);
        releaseToBeta.getSessionRegistry().removeSession(player.getClientId());
        session.disconnect(TextColor.translate("&cunexpectedly disconnected by server"));

        initialPacketsQueue.clear();
        blockChangeQueue.clear();

        entityCache.getEntities().clear();
        playersInRange.clear();
    }

    public void joinPlayer() {
        if (!isLoggedIn()) {
            releaseToBeta.getServer().getServerConnection().getPlayerList().addTabEntry(player);
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

    public void queueBlockChange(int x, int y, int z, int blockId, int data) {
        Position position = new Position(x, y, z);

        if (TileEntity.getFromId(blockId) == TileEntity.MOB_SPAWNER) {
            blockChangeQueue.add(new BlockChangeRecord(position, new BlockState(0, 0)));
            blockChangeQueue.add(new BlockChangeRecord(position, new BlockState(blockId, data)));
        } else if (TileEntity.getFromId(blockId) == TileEntity.CHEST) {
            blockChangeQueue.add(new BlockChangeRecord(position, new BlockState(blockId, 2)));
        }
    }

    public int remapBlock(int blockId) {
        BlockMap b = releaseToBeta.getBlockMap();
        if (b.exist(blockId)) {
            return b.getFromId(blockId);
        }

        return blockId;
    }

    public int remapMetadata(int blockId, int rawData) {
        MetadataMap m = releaseToBeta.getMetadataMap();
        if (m.exist(blockId)) {
            if (m.getFromId(blockId).getFrom() == rawData) {
                return m.getFromId(blockId).getTo();
            }
        }

        return rawData;
    }

    public String[] combinedPlayerList() {
        List<String> combinedPlayers = new ArrayList<>();
        //Players joined from R2B
        for (ModernPlayer modernPlayer : getMain().getServer().getServerConnection().getPlayerList().getPlayers()) {
            combinedPlayers.add(modernPlayer.getUsername());
        }
        //Players using beta client
        for (BetaPlayer betaPlayer : playersInRange) {
            combinedPlayers.add(betaPlayer.getUsername());
        }

        return combinedPlayers.toArray(new String[0]);
    }
}
