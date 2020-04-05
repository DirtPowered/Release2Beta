package com.github.dirtpowered.releasetobeta;

import com.github.dirtpowered.betaprotocollib.BetaLib;
import com.github.dirtpowered.betaprotocollib.data.version.MinecraftVersion;
import com.github.dirtpowered.releasetobeta.configuration.R2BConfiguration;
import com.github.dirtpowered.releasetobeta.data.mapping.BlockMap;
import com.github.dirtpowered.releasetobeta.data.mapping.MetadataMap;
import com.github.dirtpowered.releasetobeta.data.mapping.SoundEffectMap;
import com.github.dirtpowered.releasetobeta.network.protocol.B_1_7;
import com.github.dirtpowered.releasetobeta.network.protocol.B_1_8;
import com.github.dirtpowered.releasetobeta.network.server.ModernServer;
import com.github.dirtpowered.releasetobeta.network.session.SessionRegistry;
import com.github.dirtpowered.releasetobeta.network.translator.internal.ClientResourcePackStatusTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.internal.ClientTabCompleteTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.registry.BetaToModernTranslatorRegistry;
import com.github.dirtpowered.releasetobeta.network.translator.registry.ModernToBetaTranslatorRegistry;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientResourcePackStatusPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientTabCompletePacket;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ReleaseToBeta implements Runnable {
    public final static MinecraftVersion MINECRAFT_VERSION = MinecraftVersion.B_1_8_1;
    private final ScheduledExecutorService scheduledExecutorService;
    private SessionRegistry sessionRegistry;
    private BetaToModernTranslatorRegistry betaToModernTranslatorRegistry;
    private ModernToBetaTranslatorRegistry modernToBetaTranslatorRegistry;
    private SoundEffectMap soundEffectMap;
    private BlockMap blockMap;
    private MetadataMap metadataMap;
    private ModernServer server;

    ReleaseToBeta() {
        this.scheduledExecutorService = Executors.newScheduledThreadPool(32);
        this.sessionRegistry = new SessionRegistry();
        this.betaToModernTranslatorRegistry = new BetaToModernTranslatorRegistry();
        this.modernToBetaTranslatorRegistry = new ModernToBetaTranslatorRegistry();
        this.soundEffectMap = new SoundEffectMap();
        this.blockMap = new BlockMap();
        this.metadataMap = new MetadataMap();
        this.server = new ModernServer(this);
        new R2BConfiguration(); //load config

        BetaLib.inject(MINECRAFT_VERSION);

        switch (MINECRAFT_VERSION) {
            case B1_7_3:
                new B_1_7(betaToModernTranslatorRegistry, modernToBetaTranslatorRegistry);
                break;
            case B_1_8_1:
                new B_1_8(betaToModernTranslatorRegistry, modernToBetaTranslatorRegistry);
                break;
        }

        //internal translators
        if (R2BConfiguration.tabComplete) {
            modernToBetaTranslatorRegistry.registerTranslator(ClientTabCompletePacket.class, new ClientTabCompleteTranslator());
        }

        modernToBetaTranslatorRegistry.registerTranslator(ClientResourcePackStatusPacket.class, new ClientResourcePackStatusTranslator());

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

        this.server.getServerConnection().tick();
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

    public SoundEffectMap getSoundEffectMap() {
        return soundEffectMap;
    }

    public BlockMap getBlockMap() {
        return blockMap;
    }

    public MetadataMap getMetadataMap() {
        return metadataMap;
    }

    public ModernServer getServer() {
        return server;
    }
}
