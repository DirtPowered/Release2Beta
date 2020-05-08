/*
 * Copyright (c) 2020 Dirt Powered
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.dirtpowered.releasetobeta.network.session;

import com.github.dirtpowered.betaprotocollib.data.version.MinecraftVersion;
import com.github.dirtpowered.betaprotocollib.model.Packet;
import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.MapDataPacketData;
import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.PlayerLookPacketData;
import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.UpdateProgressPacketData;
import com.github.dirtpowered.betaprotocollib.utils.Location;
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
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_7.MapData.MapDataHandler;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_7.UpdateProgress.UpdateProgressHandler;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.dirtpowered.releasetobeta.utils.ChatUtils;
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

    @Getter
    private List<BetaPlayer> betaPlayers = new ArrayList<>();

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

    private int i;
    private MapDataHandler mapDataHandler;
    private UpdateProgressHandler updateProgressHandler;

    public BetaClientSession(ReleaseToBeta server, Channel channel, Session session, String clientId) {
        this.main = server;
        this.channel = channel;
        this.protocolState = ProtocolState.LOGIN;
        this.player = new ModernPlayer(this);
        this.session = session;
        this.entityCache = new EntityCache();
        this.clientId = clientId;
        this.mapDataHandler = new MapDataHandler();
        this.updateProgressHandler = new UpdateProgressHandler();
    }

    @Override
    public void tick() {
        if (channel.isActive())
            if (player.getGameProfile() != null) {
                Location l = player.getLocation();
                if (l != null)
                    /*
                     * Beta client sending position every tick, without that 'hack' nether portals,
                     * food eating, mob effects(potions?) will not work correctly.
                     */
                    sendPacket(new PlayerLookPacketData(l.getYaw(), l.getPitch(), player.isOnGround()));

                if (!initialPacketsQueue.isEmpty()) {
                    Packet p = initialPacketsQueue.poll();

                    channel.writeAndFlush(p);
                    return;
                }
            }

        //wait 3 seconds to make sure client is ready to receive resourcepack packet
        if (i > 20 * 3 && !resourcepack && !R2BConfiguration.resourcePack.isEmpty()) {
            player.sendResourcePack();
            resourcepack = true;
        }

        //sending block change packets immediately may cause problems, so delay it
        poolTileEntityQueue();
        i++;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet packet) {
        processPacket(packet);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        createSession();
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Logger.info("[{}] disconnected", player.getUsername());
        quitPlayer();

        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause) {
        Logger.warn("[{}/{}] closed connection: {}", clientId, player.getUsername(), cause.toString());

        cause.printStackTrace();
        context.close();
    }

    private void createSession() {
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
        betaPlayers.remove(player);
        ServerPlayerListEntryPacket entryPacket =
                new ServerPlayerListEntryPacket(PlayerListEntryAction.REMOVE_PLAYER, new PlayerListEntry[]{
                        new PlayerListEntry(player.getGameProfile())
                });

        getPlayer().sendPacket(entryPacket);
    }

    public void addBetaTabEntry(BetaPlayer player) {
        betaPlayers.add(player);
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
        channel.close();
        channel.flush();
    }

    private void quitPlayer() {
        session.disconnect(ChatUtils.colorize("&cunexpectedly disconnected by server"));
        main.getServer().getServerConnection().getPlayerList().removeTabEntry(player);
        initialPacketsQueue.clear();
        blockChangeQueue.clear();

        entityCache.getEntities().clear();
        betaPlayers.clear();

        main.getSessionRegistry().removeSession(player.getClientId());
    }

    public void joinPlayer() {
        if (!isLoggedIn()) {
            Logger.info("[{}] connected", player.getUsername());
            main.getServer().getServerConnection().getPlayerList().addTabEntry(player);
            main.getServer().updatePlayerProperties(session, player);
            setLoggedIn(true);
        }
    }

    private void poolTileEntityQueue() {
        if (blockChangeQueue.isEmpty())
            return;

        int allowance = Math.min(2, blockChangeQueue.size());

        for (int i = 0; i < allowance; i++) {
            BlockChangeRecord block = blockChangeQueue.remove();
            if (block == null)
                return;

            player.sendPacket(new ServerBlockChangePacket(block));
        }
    }

    public void queueBlockChange(int x, int y, int z, int blockId, int data) {
        TileEntity tileEntity = TileEntity.getFromId(blockId);
        Position position = new Position(x, y, z);

        switch (tileEntity) {
            case CHEST:
                if (R2BConfiguration.version == MinecraftVersion.B_1_7_3 || R2BConfiguration.version == MinecraftVersion.B_1_6_6) {
                    blockChangeQueue.add(new BlockChangeRecord(position, new BlockState(blockId, 2)));
                }
                break;
            case MOB_SPAWNER:
                blockChangeQueue.add(new BlockChangeRecord(position, new BlockState(0, 0)));
                blockChangeQueue.add(new BlockChangeRecord(position, new BlockState(blockId, data)));
                break;
            case BED:
                blockChangeQueue.add(new BlockChangeRecord(position, new BlockState(0, 0)));
                blockChangeQueue.add(new BlockChangeRecord(position, new BlockState(blockId, remapMetadata(blockId, data))));
                break;
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
        if (blockId == 0) //skip air
            return 0;

        MetadataMap m = main.getMetadataMap();
        if (m.exist(blockId)) {
            DataObject[] dataObjects = m.getFromId(blockId);
            for (DataObject dataObject : dataObjects) {
                if (dataObject.getFrom() == rawData || dataObject.getFrom() == -1) {
                    if (Arrays.asList(dataObject.getMinecraftVersion()).contains(R2BConfiguration.version)) {
                        return dataObject.getTo();
                    }
                }
            }
        }

        return rawData;
    }

    public String[] combinedPlayerList() {
        List<String> combinedPlayers = new ArrayList<>();
        //Players joined from R2B
        for (ModernPlayer modernPlayer : main.getServer().getServerConnection().getPlayerList().getPlayers()) {
            combinedPlayers.add(modernPlayer.getUsername());
        }
        //Players using beta client
        for (BetaPlayer betaPlayer : betaPlayers) {
            combinedPlayers.add(betaPlayer.getUsername());
        }

        return combinedPlayers.toArray(new String[0]);
    }

    public void handleMapPacket(MapDataPacketData mapData) {
        mapDataHandler.translateMapData(mapData, session);
    }

    public void handleUpdateProgress(UpdateProgressPacketData progressData) {
        updateProgressHandler.translateUpdateProgress(progressData, session);
    }
}
