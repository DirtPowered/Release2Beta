package com.github.dirtpowered.releasetobeta;

import com.github.dirtpowered.betaprotocollib.BetaLib;
import com.github.dirtpowered.betaprotocollib.packet.data.AnimationPacketData;
import com.github.dirtpowered.betaprotocollib.packet.data.AttachEntityPacketData;
import com.github.dirtpowered.betaprotocollib.packet.data.BedAndWeatherPacketData;
import com.github.dirtpowered.betaprotocollib.packet.data.BlockChangePacketData;
import com.github.dirtpowered.betaprotocollib.packet.data.BlockItemSwitchPacketData;
import com.github.dirtpowered.betaprotocollib.packet.data.ChatPacketData;
import com.github.dirtpowered.betaprotocollib.packet.data.CloseWindowPacketData;
import com.github.dirtpowered.betaprotocollib.packet.data.CollectPacketData;
import com.github.dirtpowered.betaprotocollib.packet.data.EntityDestroyPacketData;
import com.github.dirtpowered.betaprotocollib.packet.data.EntityEquipmentPacketData;
import com.github.dirtpowered.betaprotocollib.packet.data.EntityMetadataPacketData;
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
import com.github.dirtpowered.betaprotocollib.packet.data.MapDataPacketData;
import com.github.dirtpowered.betaprotocollib.packet.data.MobSpawnPacketData;
import com.github.dirtpowered.betaprotocollib.packet.data.MultiBlockChangePacketData;
import com.github.dirtpowered.betaprotocollib.packet.data.NamedEntitySpawnPacketData;
import com.github.dirtpowered.betaprotocollib.packet.data.OpenWindowPacketData;
import com.github.dirtpowered.betaprotocollib.packet.data.PaintingPacketData;
import com.github.dirtpowered.betaprotocollib.packet.data.PickupSpawnPacketData;
import com.github.dirtpowered.betaprotocollib.packet.data.PlayNoteblockPacketData;
import com.github.dirtpowered.betaprotocollib.packet.data.PlayerLookMovePacketData;
import com.github.dirtpowered.betaprotocollib.packet.data.PreChunkPacketData;
import com.github.dirtpowered.betaprotocollib.packet.data.RespawnPacketData;
import com.github.dirtpowered.betaprotocollib.packet.data.SetSlotPacketData;
import com.github.dirtpowered.betaprotocollib.packet.data.SleepPacketData;
import com.github.dirtpowered.betaprotocollib.packet.data.SpawnPositionPacketData;
import com.github.dirtpowered.betaprotocollib.packet.data.ThunderboltPacketData;
import com.github.dirtpowered.betaprotocollib.packet.data.TransactionPacketData;
import com.github.dirtpowered.betaprotocollib.packet.data.UpdateHealthPacketData;
import com.github.dirtpowered.betaprotocollib.packet.data.UpdateProgressPacketData;
import com.github.dirtpowered.betaprotocollib.packet.data.UpdateSignPacketData;
import com.github.dirtpowered.betaprotocollib.packet.data.UpdateTimePacketData;
import com.github.dirtpowered.betaprotocollib.packet.data.VehicleSpawnPacketData;
import com.github.dirtpowered.betaprotocollib.packet.data.WindowItemsPacketData;
import com.github.dirtpowered.releasetobeta.configuration.R2BConfiguration;
import com.github.dirtpowered.releasetobeta.network.InternalServer;
import com.github.dirtpowered.releasetobeta.network.session.SessionRegistry;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.AnimationTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.AttachEntityTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.BedAndWeatherTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.BlockChangeTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.BlockItemSwitchTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.ChatTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.CloseWindowTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.CollectTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.EntityDestroyTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.EntityEquipmentTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.EntityMetadataTranslator;
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
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.MapDataTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.MobSpawnTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.MultiBlockChangeTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.NamedEntitySpawnTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.OpenWindowTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.PaintingTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.PickupSpawnTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.PlayNoteblockTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.PlayerLookMoveTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.PreChunkTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.RespawnTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.SetSlotTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.SleepPacketTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.SpawnPositionTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.ThunderboltTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.TransactionTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.UpdateHealthTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.UpdateProgressTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.UpdateSignTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.UpdateTimeTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.VehicleSpawnTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.WindowItemsTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.moderntobeta.ClientChatTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.moderntobeta.ClientCloseWindowTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.moderntobeta.ClientConfirmTransactionTranslator;
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
import com.github.dirtpowered.releasetobeta.network.translator.moderntobeta.ClientTeleportConfirmTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.moderntobeta.ClientUpdateSignTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.moderntobeta.ClientWindowActionTranslator;
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
import com.github.steveice10.mc.protocol.packet.ingame.client.window.ClientConfirmTransactionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.window.ClientWindowActionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.world.ClientTeleportConfirmPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.world.ClientUpdateSignPacket;
import com.github.steveice10.mc.protocol.packet.login.client.LoginStartPacket;

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
        new R2BConfiguration(); //load config

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
        betaToModernTranslatorRegistry.registerTranslator(EntityMetadataPacketData.class, new EntityMetadataTranslator());
        betaToModernTranslatorRegistry.registerTranslator(CollectPacketData.class, new CollectTranslator());
        betaToModernTranslatorRegistry.registerTranslator(OpenWindowPacketData.class, new OpenWindowTranslator());
        betaToModernTranslatorRegistry.registerTranslator(TransactionPacketData.class, new TransactionTranslator());
        betaToModernTranslatorRegistry.registerTranslator(PlayNoteblockPacketData.class, new PlayNoteblockTranslator());
        betaToModernTranslatorRegistry.registerTranslator(UpdateProgressPacketData.class, new UpdateProgressTranslator());
        betaToModernTranslatorRegistry.registerTranslator(UpdateSignPacketData.class, new UpdateSignTranslator());
        betaToModernTranslatorRegistry.registerTranslator(SleepPacketData.class, new SleepPacketTranslator());
        betaToModernTranslatorRegistry.registerTranslator(AttachEntityPacketData.class, new AttachEntityTranslator());
        betaToModernTranslatorRegistry.registerTranslator(NamedEntitySpawnPacketData.class, new NamedEntitySpawnTranslator());
        betaToModernTranslatorRegistry.registerTranslator(EntityEquipmentPacketData.class, new EntityEquipmentTranslator());
        betaToModernTranslatorRegistry.registerTranslator(ThunderboltPacketData.class, new ThunderboltTranslator());
        betaToModernTranslatorRegistry.registerTranslator(RespawnPacketData.class, new RespawnTranslator());
        betaToModernTranslatorRegistry.registerTranslator(CloseWindowPacketData.class, new CloseWindowTranslator());
        betaToModernTranslatorRegistry.registerTranslator(MapDataPacketData.class, new MapDataTranslator());
        betaToModernTranslatorRegistry.registerTranslator(PaintingPacketData.class, new PaintingTranslator());

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
        modernToBetaTranslatorRegistry.registerTranslator(ClientTeleportConfirmPacket.class, new ClientTeleportConfirmTranslator());
        modernToBetaTranslatorRegistry.registerTranslator(ClientWindowActionPacket.class, new ClientWindowActionTranslator());
        modernToBetaTranslatorRegistry.registerTranslator(ClientConfirmTransactionPacket.class, new ClientConfirmTransactionTranslator());
        modernToBetaTranslatorRegistry.registerTranslator(ClientUpdateSignPacket.class, new ClientUpdateSignTranslator());

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(r -> new Thread(r, "Main Thread"));
        executor.scheduleAtFixedRate(this, 0L, 50L, TimeUnit.MILLISECONDS);
    }

    void stop() {
        getSessionRegistry().getSessions().clear();
    }

    @Override
    public void run() {
        sessionRegistry.getSessions().forEach((username, internalSession) -> {
            internalSession.getBetaClientSession().tick();
        });

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
