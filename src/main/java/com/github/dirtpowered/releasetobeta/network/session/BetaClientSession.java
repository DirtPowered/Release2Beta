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

import com.github.dirtpowered.betaprotocollib.model.Packet;
import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.MapDataPacketData;
import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.PlayerLookMovePacketData;
import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.PlayerPositionPacketData;
import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.UpdateProgressPacketData;
import com.github.dirtpowered.betaprotocollib.utils.Location;
import com.github.dirtpowered.releasetobeta.ReleaseToBeta;
import com.github.dirtpowered.releasetobeta.api.plugin.event.player.PlayerJoinEvent;
import com.github.dirtpowered.releasetobeta.configuration.R2BConfiguration;
import com.github.dirtpowered.releasetobeta.data.ProtocolState;
import com.github.dirtpowered.releasetobeta.data.blockstorage.TempBlockStorage;
import com.github.dirtpowered.releasetobeta.data.entity.EntityCache;
import com.github.dirtpowered.releasetobeta.data.entity.model.Entity;
import com.github.dirtpowered.releasetobeta.data.mapping.BlockMap;
import com.github.dirtpowered.releasetobeta.data.mapping.MetadataMap;
import com.github.dirtpowered.releasetobeta.data.mapping.model.BlockObject;
import com.github.dirtpowered.releasetobeta.data.mapping.model.DataObject;
import com.github.dirtpowered.releasetobeta.data.player.BetaPlayer;
import com.github.dirtpowered.releasetobeta.data.player.ModernPlayer;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_7.MapData.MapDataHandler;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_7.UpdateProgress.UpdateProgressHandler;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.dirtpowered.releasetobeta.utils.chat.ChatUtils;
import com.github.dirtpowered.releasetobeta.utils.interfaces.Callback;
import com.github.dirtpowered.releasetobeta.utils.interfaces.Tickable;
import com.github.steveice10.mc.protocol.data.game.PlayerListEntry;
import com.github.steveice10.mc.protocol.data.game.PlayerListEntryAction;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerPlayerListEntryPacket;
import com.github.steveice10.packetlib.Session;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class BetaClientSession extends SimpleChannelInboundHandler<Packet> implements Tickable {

    @Getter
    TempBlockStorage blockStorage;

    private final Channel channel;

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
    private List<BetaPlayer> betaPlayers;

    private boolean resourcepack;
    private int i;
    private MapDataHandler mapDataHandler;
    private UpdateProgressHandler updateProgressHandler;
    private Session session;
    private List<Packet> initialPackets = new LinkedList<>();
    private Callback<Boolean> connectionCallback;

    public BetaClientSession(ReleaseToBeta server, Channel channel, Session session, UUID clientId, Callback<Boolean> onConnect) {
        this.main = server;
        this.channel = channel;
        this.protocolState = ProtocolState.LOGIN;
        this.player = new ModernPlayer(this, clientId);
        this.session = session;
        this.entityCache = new EntityCache();
        this.mapDataHandler = new MapDataHandler();
        this.updateProgressHandler = new UpdateProgressHandler();
        this.betaPlayers = new ArrayList<>();

        // blockstorage
        this.blockStorage = new TempBlockStorage();

        // connection callback
        this.connectionCallback = onConnect;
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
        main.getLogger().info("[" + player.getUsername() + "] disconnected");
        quitPlayer();

        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause) {
        main.getLogger().warning("[" + player.getClientId() + "/" + player.getUsername() + "]" + " closed connection: " + cause.toString());

        cause.printStackTrace();
        context.close();
    }

    @Override
    public void tick() {
        if (channel.isActive()) {
            if (player.getGameProfile() != null) {
                Location l = player.getLocation();
                if (l != null)
                    /*
                     * Beta client sending position every tick, without that 'hack' nether portals,
                     * food eating, mob effects(potions?) will not work correctly.
                     */

                    /* If location is send more often than 1 tick - player starts to starve, drown faster */
                    if ((System.currentTimeMillis() - player.getLastLocationUpdate()) >= 51 && protocolState != ProtocolState.LOGIN && !player.isInVehicle()) {
                        sendPacket(new PlayerPositionPacketData(0, -999.0D, 0, -999.0D, player.isOnGround()));
                    } else if (player.isInVehicle()) {
                        sendPacket(new PlayerLookMovePacketData(l.getX(), -999.0D, l.getZ(), -999.0D, l.getYaw(), l.getPitch(), player.isOnGround()));
                    }

                if (!initialPackets.isEmpty()) {
                    initialPackets.forEach(channel::writeAndFlush);
                    return;
                }
            }

            //wait 3 seconds to make sure client is ready to receive resourcepack packet
            if (i > 20 * 3 && !resourcepack && !R2BConfiguration.resourcePack.isEmpty()) {
                player.sendResourcePack();
                resourcepack = true;
            }

            for (Entity entity : entityCache.getEntities().values())
               entity.updateEntity(player, session);

            i++;
        }
    }

    private void createSession() {
        main.getSessionRegistry().addSession(player.getClientId(), new MultiSession(this, session));
        session.setFlag("ready", true);

        connectionCallback.onComplete(true);
    }

    @SuppressWarnings("unchecked")
    private void processPacket(Packet packet) {
        BetaToModern handler = main.getBetaToModernTranslatorRegistry().getByPacket(packet);
        if (handler != null && channel.isActive()) {
            Session session = main.getSessionRegistry().getSession(player.getClientId()).getModernSession();
            if (session != null) {
                handler.translate(packet, this, session);
            }
        } else {
            main.getLogger().warning("[" + player.getClientId() + "/" + player.getUsername() + "]" + " missing 'BetaToModern' translator for: " + packet.getClass().getSimpleName());
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
            initialPackets.add(packet);
        }
    }

    public void disconnect() {
        channel.close();
        channel.flush();
    }

    private void quitPlayer() {
        session.disconnect(ChatUtils.colorize("&cunexpectedly disconnected from server"));
        main.getServer().getServerConnection().getPlayerList().removeTabEntry(player);
        initialPackets.clear();

        entityCache.getEntities().clear();
        betaPlayers.clear();

        blockStorage.purgeAll();
        main.getSessionRegistry().removeSession(player.getClientId());
    }

    public void joinPlayer() {
        if (!isLoggedIn()) {
            main.getLogger().info("[" + player.getUsername() + "] connected");
            main.getServer().getServerConnection().getPlayerList().addTabEntry(player);
            main.getServer().updatePlayerProperties(session, player);

            main.getServer().sendInitialPlayerAbilities(player);
            main.getServer().sendWorldBorder(session);
            setLoggedIn(true);

            main.getEventManager().fireEvent(new PlayerJoinEvent(player));
        }
    }

    public int remapBlock(int blockId, int blockData, boolean inInventory) {
        BlockMap b = main.getBlockMap();
        if (b.exist(blockId)) {
            BlockObject blockObject = b.getFromId(blockId);

            if (!blockObject.isInInventory() || inInventory && blockData == blockObject.getItemData()) {
                return blockObject.getTo();
            }
        }

        return blockId;
    }

    public int remapMetadata(int blockId, int rawData) {
        return remapMetadata(blockId, rawData, false);
    }

    public int remapMetadata(int blockId, int rawData, boolean custom) {
        if (blockId == 0 || custom) //skip air
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
