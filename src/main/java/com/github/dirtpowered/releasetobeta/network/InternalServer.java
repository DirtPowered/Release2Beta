package com.github.dirtpowered.releasetobeta.network;

import com.github.dirtpowered.releasetobeta.ReleaseToBeta;
import com.github.dirtpowered.releasetobeta.configuration.R2BConfiguration;
import com.github.dirtpowered.releasetobeta.data.entity.EntityRegistry;
import com.github.dirtpowered.releasetobeta.data.player.ModernPlayer;
import com.github.dirtpowered.releasetobeta.data.skin.ProfileCache;
import com.github.dirtpowered.releasetobeta.network.codec.PipelineFactory;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.session.MultiSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.ModernToBeta;
import com.github.dirtpowered.releasetobeta.utils.Tickable;
import com.github.steveice10.mc.auth.data.GameProfile;
import com.github.steveice10.mc.protocol.MinecraftConstants;
import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.mc.protocol.ServerLoginHandler;
import com.github.steveice10.mc.protocol.data.game.PlayerListEntry;
import com.github.steveice10.mc.protocol.data.game.PlayerListEntryAction;
import com.github.steveice10.mc.protocol.data.message.TextMessage;
import com.github.steveice10.mc.protocol.data.status.PlayerInfo;
import com.github.steveice10.mc.protocol.data.status.ServerStatusInfo;
import com.github.steveice10.mc.protocol.data.status.VersionInfo;
import com.github.steveice10.mc.protocol.data.status.handler.ServerInfoBuilder;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerPlayerListEntryPacket;
import com.github.steveice10.packetlib.Server;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.event.server.ServerAdapter;
import com.github.steveice10.packetlib.event.server.SessionAddedEvent;
import com.github.steveice10.packetlib.event.server.SessionRemovedEvent;
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent;
import com.github.steveice10.packetlib.event.session.SessionAdapter;
import com.github.steveice10.packetlib.packet.Packet;
import com.github.steveice10.packetlib.tcp.TcpSessionFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.pmw.tinylog.Logger;

import java.net.InetSocketAddress;
import java.util.AbstractMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

public class InternalServer implements Tickable {

    private final Queue<AbstractMap.SimpleEntry<Session, Packet>> packetQueue = new LinkedBlockingQueue<>();
    private final VersionInfo versionInfo = new VersionInfo(MinecraftConstants.GAME_VERSION, MinecraftConstants.PROTOCOL_VERSION);
    private Server server;
    private ReleaseToBeta releaseToBeta;
    private NioEventLoopGroup loopGroup;
    private EntityRegistry entityRegistry;
    private ProfileCache profileCache;

    public InternalServer(ReleaseToBeta releaseToBeta) {
        this.releaseToBeta = releaseToBeta;
        this.entityRegistry = new EntityRegistry();
        this.profileCache = new ProfileCache();

        createServer();
    }

    private int getSessionCount() {
        return releaseToBeta.getSessionRegistry().getSessions().size();
    }

    @SuppressWarnings("unchecked")
    private void handleIncomingPackets() {
        AbstractMap.SimpleEntry<Session, Packet> entries;

        while ((entries = packetQueue.poll()) != null) {
            Packet packet = entries.getValue();
            Session modernSession = entries.getKey();

            ModernToBeta handler = releaseToBeta.getModernToBetaTranslatorRegistry().getByPacket(packet);
            if (handler != null) {
                BetaClientSession clientSession = getSessionFromServerSession(modernSession);
                if (clientSession == null) {
                    Logger.warn("[server] not all packets were processed ({}:{})", packet.getClass().getSimpleName(),
                            ReflectionToStringBuilder.toString(packet, ToStringStyle.JSON_STYLE));
                    return;
                }
                handler.translate(packet, modernSession, clientSession);
            } else {
                Logger.warn("[server] missing 'ModernToBeta' translator for {}", packet.getClass().getSimpleName());
            }
        }
    }

    /*
     * TODO: Fix that huge mess
     */
    private BetaClientSession getSessionFromServerSession(Session modernSession) {
        return releaseToBeta.getSessionRegistry().getSessions().values().stream()
                .filter(multiSession -> multiSession.getModernSession() == modernSession).findFirst()
                .map(MultiSession::getBetaClientSession).orElse(null);
    }

    private ModernPlayer[] getPlayers() {
        List<ModernPlayer> players = new LinkedList<>();
        releaseToBeta.getSessionRegistry().getSessions().forEach((s, multiSession) -> {
            players.add(multiSession.getBetaClientSession().getPlayer());
        });

        return players.toArray(new ModernPlayer[0]);
    }

    private GameProfile[] getProfiles() {
        List<GameProfile> profiles = new LinkedList<>();
        releaseToBeta.getSessionRegistry().getSessions().forEach((s, multiSession) -> {
            GameProfile gameProfile = multiSession.getBetaClientSession().getPlayer().getGameProfile();
            if (gameProfile != null) {
                profiles.add(gameProfile);
            }
        });

        return profiles.toArray(new GameProfile[0]);
    }

    private PlayerListEntry[] getTabEntries() {
        List<PlayerListEntry> tabEntries = new LinkedList<>();
        releaseToBeta.getSessionRegistry().getSessions().forEach((s, multiSession) -> {
            PlayerListEntry listEntry = multiSession.getBetaClientSession().getPlayer().getTabEntry();
            if (listEntry != null) {
                tabEntries.add(listEntry);
            }
        });

        return tabEntries.toArray(new PlayerListEntry[0]);
    }

    public UUID getUUIDFromUsername(String username) {
        for (ModernPlayer player : getPlayers()) {
            if (player.getUsername() != null) {
                if (player.getUsername().equals(username)) {
                    return player.getGameProfile().getId();
                }
            } else {
                player.getSession().disconnect();
            }
        }

        return null;
    }

    private void createServer() {
        server = new Server(R2BConfiguration.bindAddress, R2BConfiguration.bindPort, MinecraftProtocol.class, new TcpSessionFactory());
        server.setGlobalFlag(MinecraftConstants.VERIFY_USERS_KEY, false);
        server.setGlobalFlag(MinecraftConstants.SERVER_COMPRESSION_THRESHOLD, 256);
        server.setGlobalFlag(MinecraftConstants.SERVER_INFO_BUILDER_KEY, (ServerInfoBuilder) session -> {
            return new ServerStatusInfo(versionInfo,
                    new PlayerInfo(R2BConfiguration.maxPlayers, getSessionCount(), getProfiles()), new TextMessage(R2BConfiguration.motd), null);
        });

        server.setGlobalFlag(MinecraftConstants.SERVER_LOGIN_HANDLER_KEY, (ServerLoginHandler) session -> {
            try {
                if (session.isConnected()) {
                    createClientSession(RandomStringUtils.randomAlphabetic(8), session);
                }

            } catch (InterruptedException e) {
                Logger.error("Error: {}", e.getMessage());
            }
        });

        server.addListener(new ServerAdapter() {
            @Override
            public void sessionAdded(SessionAddedEvent event) {
                event.getSession().addListener(new SessionAdapter() {
                    @Override
                    public void packetReceived(PacketReceivedEvent event) {
                        packetQueue.add(new AbstractMap.SimpleEntry<>(event.getSession(), event.getPacket()));
                    }
                });
            }

            @Override
            public void sessionRemoved(SessionRemovedEvent event) {
                releaseToBeta.getSessionRegistry().getSessions().forEach((clientId, multiSession) -> {
                    if (multiSession.getModernSession().equals(event.getSession())) {
                        multiSession.getBetaClientSession().disconnect();
                    }
                });
            }
        });

        server.bind();
    }

    private void broadcastPacket(Packet packet) {
        releaseToBeta.getSessionRegistry().getSessions().forEach((s, multiSession) -> {
            if (multiSession.getModernSession().isConnected())
                multiSession.getModernSession().send(packet);
        });
    }

    public void removeTabEntry(ModernPlayer player) {
        if (player.getGameProfile() == null)
            return;

        Logger.info("removing '{}' from tablist", player.getUsername());

        ServerPlayerListEntryPacket entryPacket =
                new ServerPlayerListEntryPacket(PlayerListEntryAction.REMOVE_PLAYER, new PlayerListEntry[]{
                        new PlayerListEntry(player.getGameProfile())
                });

        broadcastPacket(entryPacket);
    }

    public void addTabEntry(ModernPlayer player) {
        player.sendPacket(new ServerPlayerListEntryPacket(PlayerListEntryAction.ADD_PLAYER, getTabEntries()));

        ServerPlayerListEntryPacket entryPacket =
                new ServerPlayerListEntryPacket(PlayerListEntryAction.ADD_PLAYER, new PlayerListEntry[]{
                        player.getTabEntry()
                });

        broadcastPacket(entryPacket);
    }

    @Override
    public void tick() {
        handleIncomingPackets();
    }

    public Server getServer() {
        return server;
    }

    public EntityRegistry getEntityRegistry() {
        return entityRegistry;
    }

    private void createClientSession(String clientId, Session session) throws InterruptedException {
        try {
            Bootstrap clientBootstrap = new Bootstrap();

            loopGroup = new NioEventLoopGroup();
            clientBootstrap.group(loopGroup);
            clientBootstrap.channel(NioSocketChannel.class);
            clientBootstrap
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000);

            clientBootstrap.remoteAddress(new InetSocketAddress(R2BConfiguration.remoteAddress, R2BConfiguration.remotePort));
            clientBootstrap.handler(new ChannelInitializer<SocketChannel>() {

                @Override
                protected void initChannel(SocketChannel ch) {
                    ch.pipeline().addLast("mc_pipeline", new PipelineFactory());
                    BetaClientSession clientSession = new BetaClientSession(releaseToBeta, ch, session, clientId);

                    ch.pipeline().addLast("client_connection_handler", clientSession);
                }
            });

            ChannelFuture channelFuture = clientBootstrap.connect().sync();
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            session.disconnect(e.getMessage());
        } finally {
            loopGroup.shutdownGracefully().sync();
        }
    }

    public ProfileCache getProfileCache() {
        return profileCache;
    }
}
