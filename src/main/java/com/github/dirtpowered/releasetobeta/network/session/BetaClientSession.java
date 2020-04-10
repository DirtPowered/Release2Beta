package com.github.dirtpowered.releasetobeta.network.session;

import com.github.dirtpowered.betaprotocollib.data.version.MinecraftVersion;
import com.github.dirtpowered.betaprotocollib.model.Packet;
import com.github.dirtpowered.releasetobeta.ReleaseToBeta;
import com.github.dirtpowered.releasetobeta.configuration.R2BConfiguration;
import com.github.dirtpowered.releasetobeta.data.ProtocolState;
import com.github.dirtpowered.releasetobeta.data.entity.EntityCache;
import com.github.dirtpowered.releasetobeta.data.entity.TileEntity;
import com.github.dirtpowered.releasetobeta.data.mapping.BlockMap;
import com.github.dirtpowered.releasetobeta.data.mapping.DataObject;
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
import lombok.Getter;
import lombok.Setter;
import org.pmw.tinylog.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;

public class BetaClientSession extends SimpleChannelInboundHandler<Packet> implements Tickable {
    private final Channel channel;
    private Session session;
    private Deque<BlockChangeRecord> blockChangeQueue = new LinkedList<>();
    private Deque<Packet> initialPacketsQueue = new LinkedBlockingDeque<>();
    private List<BetaPlayer> playersInRange = new ArrayList<>();

    @Getter
    private ReleaseToBeta main;

    @Getter
    @Setter
    private ProtocolState protocolState;

    @Getter
    private ModernPlayer player;

    @Getter
    @Setter
    private boolean loggedIn;

    @Getter
    private EntityCache entityCache;

    @Getter
    private String clientId;

    private boolean resourcepack;

    private int tickLimiter = 0;
    private int i;

    public BetaClientSession(ReleaseToBeta server, Channel channel, Session session, String clientId) {
        this.main = server;
        this.channel = channel;
        this.protocolState = ProtocolState.LOGIN;
        this.player = new ModernPlayer(this);
        this.session = session;
        this.entityCache = new EntityCache();
        this.clientId = clientId;
    }

    @Override
    public void tick() {
        tickLimiter = (tickLimiter + 1) % 2;
        if (tickLimiter == 0) {
            if (channel.isActive() && player.getGameProfile() != null && !initialPacketsQueue.isEmpty()) {
                Packet p = initialPacketsQueue.poll();

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

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet packet) {
        processPacket(packet);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Logger.info("[{}] connected", clientId);

        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Logger.info("[{}] disconnected", clientId);
        quitPlayer();

        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause) {
        Logger.warn("[{}/{}] closed connection: {}", clientId, player.getUsername(), cause.toString());

        cause.printStackTrace();
        context.close();
    }

    public void createSession() {
        main.getSessionRegistry().addSession(clientId, new MultiSession(this, session));
        player.setClientId(clientId);
    }

    @SuppressWarnings("unchecked")
    private void processPacket(Packet packet) {
        BetaToModern handler = main.getBetaToModernTranslatorRegistry().getByPacket(packet);
        if (handler != null && channel.isActive()) {
            handler.translate(packet, this, main.getSessionRegistry().getSession(player.getClientId()).getModernSession());
        } else {
            Logger.warn("[client={}] missing 'BetaToModern' translator for {}", clientId, packet.getClass().getSimpleName());
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

    public void sendPacket(Packet packet) {
        if (channel.isActive())
            channel.writeAndFlush(packet);
        else {
            initialPacketsQueue.add(packet);
        }
    }

    public void disconnect() {
        if (channel.isActive())
            channel.close();
    }

    private void quitPlayer() {
        session.disconnect(TextColor.translate("&cunexpectedly disconnected by server"));
        main.getServer().getServerConnection().getPlayerList().removeTabEntry(player);

        initialPacketsQueue.clear();
        blockChangeQueue.clear();

        entityCache.getEntities().clear();
        playersInRange.clear();

        main.getSessionRegistry().removeSession(player.getClientId());
    }

    public void joinPlayer() {
        if (!isLoggedIn()) {
            main.getServer().getServerConnection().getPlayerList().addTabEntry(player);
            setLoggedIn(true);
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
        } else if (TileEntity.getFromId(blockId) == TileEntity.CHEST && ReleaseToBeta.MINECRAFT_VERSION == MinecraftVersion.B_1_7_3) {
            blockChangeQueue.add(new BlockChangeRecord(position, new BlockState(blockId, 2)));
        }
    }

    public int remapBlock(int blockId) {
        BlockMap b = main.getBlockMap();
        if (b.exist(blockId)) {
            return b.getFromId(blockId);
        }

        return blockId;
    }

    public int remapMetadata(int blockId, int rawData) {
        MetadataMap m = main.getMetadataMap();
        if (m.exist(blockId)) {
            DataObject dataObject = m.getFromId(blockId);
            if (dataObject.getFrom() == rawData || dataObject.getFrom() == -1) {
                if (Arrays.asList(dataObject.getMinecraftVersion()).contains(ReleaseToBeta.MINECRAFT_VERSION)) {
                    return m.getFromId(blockId).getTo();
                }
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
