package com.github.dirtpowered.releasetobeta;

import com.github.dirtpowered.betaprotocollib.BetaLib;
import com.github.dirtpowered.betaprotocollib.packet.data.*;
import com.github.dirtpowered.releasetobeta.network.InternalServer;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.session.SessionRegistry;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.*;
import com.github.dirtpowered.releasetobeta.network.translator.moderntobeta.*;
import com.github.dirtpowered.releasetobeta.network.translator.registry.BetaToModernTranslatorRegistry;
import com.github.dirtpowered.releasetobeta.network.translator.registry.ModernToBetaTranslatorRegistry;
import com.github.steveice10.mc.protocol.packet.handshake.client.HandshakePacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientKeepAlivePacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientRequestPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.*;
import com.github.steveice10.mc.protocol.packet.ingame.client.window.ClientCloseWindowPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.window.ClientConfirmTransactionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.window.ClientWindowActionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.world.ClientTeleportConfirmPacket;
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
