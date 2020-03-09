package com.github.dirtpowered.releasetobeta;

import com.github.dirtpowered.betaprotocollib.BetaLib;
import com.github.dirtpowered.betaprotocollib.packet.data.AnimationPacketData;
import com.github.dirtpowered.betaprotocollib.packet.data.BedAndWeatherPacketData;
import com.github.dirtpowered.betaprotocollib.packet.data.BlockChangePacketData;
import com.github.dirtpowered.betaprotocollib.packet.data.BlockItemSwitchPacketData;
import com.github.dirtpowered.betaprotocollib.packet.data.ChatPacketData;
import com.github.dirtpowered.betaprotocollib.packet.data.EntityDestroyPacketData;
import com.github.dirtpowered.betaprotocollib.packet.data.EntityMoveLookPacketData;
import com.github.dirtpowered.betaprotocollib.packet.data.EntityPositionPacketData;
import com.github.dirtpowered.betaprotocollib.packet.data.EntityStatusPacketData;
import com.github.dirtpowered.betaprotocollib.packet.data.EntityTeleportPacketData;
import com.github.dirtpowered.betaprotocollib.packet.data.EntityVelocityPacketData;
import com.github.dirtpowered.betaprotocollib.packet.data.ExplosionPacketData;
import com.github.dirtpowered.betaprotocollib.packet.data.HandshakePacketData;
import com.github.dirtpowered.betaprotocollib.packet.data.KickDisconnectPacketData;
import com.github.dirtpowered.betaprotocollib.packet.data.LoginPacketData;
import com.github.dirtpowered.betaprotocollib.packet.data.MapChunkPacketData;
import com.github.dirtpowered.betaprotocollib.packet.data.MobSpawnPacketData;
import com.github.dirtpowered.betaprotocollib.packet.data.MultiBlockChangePacketData;
import com.github.dirtpowered.betaprotocollib.packet.data.PickupSpawnPacketData;
import com.github.dirtpowered.betaprotocollib.packet.data.PlayerLookMovePacketData;
import com.github.dirtpowered.betaprotocollib.packet.data.PreChunkPacketData;
import com.github.dirtpowered.betaprotocollib.packet.data.SetSlotPacketData;
import com.github.dirtpowered.betaprotocollib.packet.data.SpawnPositionPacketData;
import com.github.dirtpowered.betaprotocollib.packet.data.UpdateHealthPacketData;
import com.github.dirtpowered.betaprotocollib.packet.data.UpdateTimePacketData;
import com.github.dirtpowered.betaprotocollib.packet.data.VehicleSpawnPacketData;
import com.github.dirtpowered.betaprotocollib.packet.data.WindowItemsPacketData;
import com.github.dirtpowered.releasetobeta.network.InternalServer;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.session.SessionRegistry;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.AnimationTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.BedAndWeatherTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.BlockChangeTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.BlockItemSwitchTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.ChatTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.EntityDestroyTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.EntityMoveLookTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.EntityPositionTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.EntityStatusTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.EntityTeleportTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.EntityVelocityTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.ExplosionTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.HandshakeTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.KickDisconnectTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.LoginTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.MapChunkTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.MobSpawnTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.MultiBlockChangeTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.PickupSpawnTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.PlayerLookMoveTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.PreChunkTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.SetSlotTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.SpawnPositionTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.UpdateHealthTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.UpdateTimeTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.VehicleSpawnTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.WindowItemsTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.moderntobeta.ClientChatTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.moderntobeta.ClientCloseWindowTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.moderntobeta.ClientHandshakeTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.moderntobeta.ClientKeepAliveTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.moderntobeta.ClientPlayerActionTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.moderntobeta.ClientPlayerChangeHeldItemTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.moderntobeta.ClientPlayerInteractEntityTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.moderntobeta.ClientPlayerPlaceBlockTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.moderntobeta.ClientPlayerPositionRotationTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.moderntobeta.ClientPlayerPositionTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.moderntobeta.ClientPlayerRotationTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.moderntobeta.ClientPlayerStateTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.moderntobeta.ClientPlayerSwingArmTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.moderntobeta.ClientPlayerUseItemTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.moderntobeta.ClientRequestTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.moderntobeta.LoginStartTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.registry.BetaToModernTranslatorRegistry;
import com.github.dirtpowered.releasetobeta.network.translator.registry.ModernToBetaTranslatorRegistry;
import com.github.steveice10.mc.protocol.packet.handshake.client.HandshakePacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientKeepAlivePacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientRequestPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerActionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerChangeHeldItemPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerInteractEntityPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPlaceBlockPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPositionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPositionRotationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerRotationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerStatePacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerSwingArmPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerUseItemPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.window.ClientCloseWindowPacket;
import com.github.steveice10.mc.protocol.packet.login.client.LoginStartPacket;
import com.github.steveice10.packetlib.Session;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ReleaseToBeta implements Runnable {

    private final ScheduledExecutorService scheduledExecutorService;
    private SessionRegistry sessionRegistry;
    private BetaToModernTranslatorRegistry betaToModernTranslatorRegistry;
    private ModernToBetaTranslatorRegistry modernToBetaTranslatorRegistry;
    private InternalServer server;

    ReleaseToBeta() {
        this.scheduledExecutorService = Executors.newScheduledThreadPool(32);
        this.sessionRegistry = new SessionRegistry();
        this.betaToModernTranslatorRegistry = new BetaToModernTranslatorRegistry();
        this.modernToBetaTranslatorRegistry = new ModernToBetaTranslatorRegistry();
        this.server = new InternalServer(this);

        //register packets
        BetaLib.inject();

        betaToModernTranslatorRegistry.registerTranslator(KickDisconnectPacketData.class, new KickDisconnectTranslator());
        betaToModernTranslatorRegistry.registerTranslator(HandshakePacketData.class, new HandshakeTranslator());
        betaToModernTranslatorRegistry.registerTranslator(LoginPacketData.class, new LoginTranslator());
        betaToModernTranslatorRegistry.registerTranslator(ChatPacketData.class, new ChatTranslator());
        betaToModernTranslatorRegistry.registerTranslator(SpawnPositionPacketData.class, new SpawnPositionTranslator());
        betaToModernTranslatorRegistry.registerTranslator(UpdateTimePacketData.class, new UpdateTimeTranslator());
        betaToModernTranslatorRegistry.registerTranslator(PlayerLookMovePacketData.class, new PlayerLookMoveTranslator());
        betaToModernTranslatorRegistry.registerTranslator(BlockChangePacketData.class, new BlockChangeTranslator());
        betaToModernTranslatorRegistry.registerTranslator(PreChunkPacketData.class, new PreChunkTranslator());
        betaToModernTranslatorRegistry.registerTranslator(MapChunkPacketData.class, new MapChunkTranslator());
        betaToModernTranslatorRegistry.registerTranslator(UpdateHealthPacketData.class, new UpdateHealthTranslator());
        betaToModernTranslatorRegistry.registerTranslator(BedAndWeatherPacketData.class, new BedAndWeatherTranslator());
        betaToModernTranslatorRegistry.registerTranslator(SetSlotPacketData.class, new SetSlotTranslator());
        betaToModernTranslatorRegistry.registerTranslator(MultiBlockChangePacketData.class, new MultiBlockChangeTranslator());
        betaToModernTranslatorRegistry.registerTranslator(BlockItemSwitchPacketData.class, new BlockItemSwitchTranslator());
        betaToModernTranslatorRegistry.registerTranslator(WindowItemsPacketData.class, new WindowItemsTranslator());
        betaToModernTranslatorRegistry.registerTranslator(AnimationPacketData.class, new AnimationTranslator());
        betaToModernTranslatorRegistry.registerTranslator(MobSpawnPacketData.class, new MobSpawnTranslator());
        betaToModernTranslatorRegistry.registerTranslator(EntityPositionPacketData.class, new EntityPositionTranslator());
        betaToModernTranslatorRegistry.registerTranslator(EntityTeleportPacketData.class, new EntityTeleportTranslator());
        betaToModernTranslatorRegistry.registerTranslator(EntityMoveLookPacketData.class, new EntityMoveLookTranslator());
        betaToModernTranslatorRegistry.registerTranslator(EntityDestroyPacketData.class, new EntityDestroyTranslator());
        betaToModernTranslatorRegistry.registerTranslator(EntityStatusPacketData.class, new EntityStatusTranslator());
        betaToModernTranslatorRegistry.registerTranslator(ExplosionPacketData.class, new ExplosionTranslator());
        betaToModernTranslatorRegistry.registerTranslator(EntityVelocityPacketData.class, new EntityVelocityTranslator());
        betaToModernTranslatorRegistry.registerTranslator(VehicleSpawnPacketData.class, new VehicleSpawnTranslator());
        betaToModernTranslatorRegistry.registerTranslator(PickupSpawnPacketData.class, new PickupSpawnTranslator());

        modernToBetaTranslatorRegistry.registerTranslator(LoginStartPacket.class, new LoginStartTranslator());
        modernToBetaTranslatorRegistry.registerTranslator(ClientKeepAlivePacket.class, new ClientKeepAliveTranslator());
        modernToBetaTranslatorRegistry.registerTranslator(ClientChatPacket.class, new ClientChatTranslator());
        modernToBetaTranslatorRegistry.registerTranslator(ClientPlayerChangeHeldItemPacket.class, new ClientPlayerChangeHeldItemTranslator());
        modernToBetaTranslatorRegistry.registerTranslator(ClientPlayerPositionPacket.class, new ClientPlayerPositionTranslator());
        modernToBetaTranslatorRegistry.registerTranslator(ClientPlayerPositionRotationPacket.class, new ClientPlayerPositionRotationTranslator());
        modernToBetaTranslatorRegistry.registerTranslator(ClientRequestPacket.class, new ClientRequestTranslator());
        modernToBetaTranslatorRegistry.registerTranslator(ClientPlayerRotationPacket.class, new ClientPlayerRotationTranslator());
        modernToBetaTranslatorRegistry.registerTranslator(ClientPlayerSwingArmPacket.class, new ClientPlayerSwingArmTranslator());
        modernToBetaTranslatorRegistry.registerTranslator(ClientPlayerStatePacket.class, new ClientPlayerStateTranslator());
        modernToBetaTranslatorRegistry.registerTranslator(ClientPlayerActionPacket.class, new ClientPlayerActionTranslator());
        modernToBetaTranslatorRegistry.registerTranslator(ClientPlayerPlaceBlockPacket.class, new ClientPlayerPlaceBlockTranslator());
        modernToBetaTranslatorRegistry.registerTranslator(ClientCloseWindowPacket.class, new ClientCloseWindowTranslator());
        modernToBetaTranslatorRegistry.registerTranslator(ClientPlayerInteractEntityPacket.class, new ClientPlayerInteractEntityTranslator());
        modernToBetaTranslatorRegistry.registerTranslator(ClientPlayerUseItemPacket.class, new ClientPlayerUseItemTranslator());
        modernToBetaTranslatorRegistry.registerTranslator(HandshakePacket.class, new ClientHandshakeTranslator());

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(r -> new Thread(r, "Main Thread"));
        executor.scheduleAtFixedRate(this, 0L, 50L, TimeUnit.MILLISECONDS);
    }

    void stop() {
        getSessionRegistry().getSessions().clear();
    }

    @Override
    public void run() {
        for (Map.Entry<Session, BetaClientSession> entry : sessionRegistry.getSessions().entrySet()) {
            BetaClientSession client = entry.getValue();
            client.tick();
        }

        this.server.tick();
    }

    public ScheduledExecutorService getScheduledExecutorService() {
        return scheduledExecutorService;
    }

    public SessionRegistry getSessionRegistry() {
        return sessionRegistry;
    }

    public BetaToModernTranslatorRegistry getBetaToModernTranslatorRegistry() {
        return betaToModernTranslatorRegistry;
    }

    public ModernToBetaTranslatorRegistry getModernToBetaTranslatorRegistry() {
        return modernToBetaTranslatorRegistry;
    }

    public InternalServer getServer() {
        return server;
    }
}
