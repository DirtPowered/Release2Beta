package com.github.dirtpowered.releasetobeta.network.session;

import com.github.dirtpowered.betaprotocollib.model.Packet;
import com.github.dirtpowered.betaprotocollib.packet.data.KeepAlivePacketData;
import com.github.dirtpowered.betaprotocollib.packet.data.StatisticsPacketData;
import com.github.dirtpowered.releasetobeta.ReleaseToBeta;
import com.github.dirtpowered.releasetobeta.data.ProtocolState;
import com.github.dirtpowered.releasetobeta.data.entity.EntityCache;
import com.github.dirtpowered.releasetobeta.data.entity.TileEntity;
import com.github.dirtpowered.releasetobeta.data.mapping.BlockMap;
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
    private String clientId;
    private int tickLimiter = 0;

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

    public void queueBlockChange(int x, int y, int z, int blockId, int data) {
        Position position = new Position(x, y, z);

        switch (TileEntity.getFromId(blockId)) {
            case CHEST:
                blockChangeQueue.add(new BlockChangeRecord(position, new BlockState(blockId, 2)));
                break;
            case FURNACE:
                blockChangeQueue.add(new BlockChangeRecord(position, new BlockState(blockId, 0)));
                break;
            case MOB_SPAWNER:
                /*CompoundTag spawner = new CompoundTag(StringUtils.EMPTY);
                spawner.put(new ShortTag("SpawnRange", (short) 0));

                spawner.put(new ShortTag("MaxNearbyEntities", (short) 0));
                spawner.put(new ShortTag("RequiredPlayerRange", (short) 0));
                spawner.put(new ShortTag("SpawnCount", (short) 4));
                spawner.put(new ShortTag("MaxSpawnDelay", (short) 800));
                spawner.put(new ShortTag("Delay", (short) 0));

                spawner.put(new IntTag("x", x));
                spawner.put(new IntTag("y", y));
                spawner.put(new IntTag("z", z));

                spawner.put(new StringTag("id", "minecraft:mob_spawner"));

                spawner.put(new ShortTag("SpawnRange", (short) 0));
                spawner.put(new ShortTag("MinSpawnDelay", (short) 200));
                CompoundTag spawnData = new CompoundTag("SpawnData");

                spawner.put(new StringTag("id", "minecraft:creeper"));
                spawner.put(spawnData);

                ServerUpdateTileEntityPacket packet = new ServerUpdateTileEntityPacket(position, UpdatedTileType.MOB_SPAWNER, spawner);
                session.send(packet);*/

                /* I did something wrong above, cuz it's not working. */
                blockChangeQueue.add(new BlockChangeRecord(position, new BlockState(0, 0)));
                blockChangeQueue.add(new BlockChangeRecord(position, new BlockState(blockId, 0)));
                break;
        }
    }

    public int remapBlock(int blockId) {
        BlockMap b = releaseToBeta.getBlockMap();
        if (b.exist(blockId)) {
            return b.getFromId(blockId);
        }

        return blockId;
    }
}
