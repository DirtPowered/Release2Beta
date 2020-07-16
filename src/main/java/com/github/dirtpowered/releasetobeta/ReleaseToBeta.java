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

package com.github.dirtpowered.releasetobeta;

import com.github.dirtpowered.betaprotocollib.BetaLib;
import com.github.dirtpowered.betaprotocollib.data.version.MinecraftVersion;
import com.github.dirtpowered.releasetobeta.bootstrap.AbstractBootstrap;
import com.github.dirtpowered.releasetobeta.configuration.R2BConfiguration;
import com.github.dirtpowered.releasetobeta.data.Constants;
import com.github.dirtpowered.releasetobeta.data.mapping.DifficultyMap;
import com.github.dirtpowered.releasetobeta.data.mapping.EntityEffectMap;
import com.github.dirtpowered.releasetobeta.data.mapping.MobTypeMap;
import com.github.dirtpowered.releasetobeta.data.mapping.SoundEffectMap;
import com.github.dirtpowered.releasetobeta.data.mapping.flattening.DataConverter;
import com.github.dirtpowered.releasetobeta.logger.AbstractLogger;
import com.github.dirtpowered.releasetobeta.network.protocol.B_1_7;
import com.github.dirtpowered.releasetobeta.network.protocol.B_1_8;
import com.github.dirtpowered.releasetobeta.network.protocol.B_1_9;
import com.github.dirtpowered.releasetobeta.network.server.ModernServer;
import com.github.dirtpowered.releasetobeta.network.server.ping.LegacyPing.PingPassthroughThread;
import com.github.dirtpowered.releasetobeta.network.session.MultiSession;
import com.github.dirtpowered.releasetobeta.network.session.SessionRegistry;
import com.github.dirtpowered.releasetobeta.network.translator.internal.ClientResourcePackStatusTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.internal.ClientTabCompleteTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.registry.BetaToModernTranslatorRegistry;
import com.github.dirtpowered.releasetobeta.network.translator.registry.ModernToBetaTranslatorRegistry;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientResourcePackStatusPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientTabCompletePacket;
import lombok.Getter;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Getter
public class ReleaseToBeta implements Runnable {
    private final ScheduledExecutorService scheduledExecutorService;
    private SessionRegistry sessionRegistry;
    private BetaToModernTranslatorRegistry betaToModernTranslatorRegistry;
    private ModernToBetaTranslatorRegistry modernToBetaTranslatorRegistry;
    private SoundEffectMap soundEffectMap;
    private EntityEffectMap entityEffectMap;
    private MobTypeMap mobTypeMap;
    private DifficultyMap difficultyMap;
    private ModernServer server;
    private PingPassthroughThread pingPassthroughThread;
    private AbstractBootstrap bootstrap;
    private DataConverter dataConverter;

    public ReleaseToBeta(AbstractBootstrap bootstrap) {
        long startTime = System.nanoTime();
        this.bootstrap = bootstrap;
        getLogger().info("Starting ReleaseToBeta on " + bootstrap.getPlatform() + " platform");

        new R2BConfiguration(this).loadConfiguration(bootstrap.getConfigPath()); //load config

        this.scheduledExecutorService = Executors.newScheduledThreadPool(32);
        this.sessionRegistry = new SessionRegistry();
        this.betaToModernTranslatorRegistry = new BetaToModernTranslatorRegistry();
        this.modernToBetaTranslatorRegistry = new ModernToBetaTranslatorRegistry();
        this.soundEffectMap = new SoundEffectMap();
        this.difficultyMap = new DifficultyMap();
        this.entityEffectMap = new EntityEffectMap();
        this.mobTypeMap = new MobTypeMap();
        this.server = new ModernServer(this);
        this.dataConverter = new DataConverter(this);

        BetaLib.inject(R2BConfiguration.version);

        switch (R2BConfiguration.version) {
            case B_1_6_6:
                //no protocol changes compared to b1.7
                new B_1_7(betaToModernTranslatorRegistry, modernToBetaTranslatorRegistry);
                break;
            case B_1_7_3:
                new B_1_7(betaToModernTranslatorRegistry, modernToBetaTranslatorRegistry);
                break;
            case B_1_8_1:
                new B_1_8(betaToModernTranslatorRegistry, modernToBetaTranslatorRegistry);
                break;
            case B_1_9:
                new B_1_9(betaToModernTranslatorRegistry, modernToBetaTranslatorRegistry);
                break;
        }

        //internal translators
        if (R2BConfiguration.tabComplete) {
            modernToBetaTranslatorRegistry.registerTranslator(ClientTabCompletePacket.class, new ClientTabCompleteTranslator());
        }

        modernToBetaTranslatorRegistry.registerTranslator(ClientResourcePackStatusPacket.class, new ClientResourcePackStatusTranslator());

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(r -> new Thread(r, "Main Thread"));
        executor.scheduleAtFixedRate(this, 0L, 50L, TimeUnit.MILLISECONDS);

        if (R2BConfiguration.ver1_8PingPassthrough && MinecraftVersion.B_1_8_1.isNewerOrEqual(R2BConfiguration.version)) {
            this.pingPassthroughThread = new PingPassthroughThread(this);
            scheduledExecutorService.scheduleAtFixedRate(pingPassthroughThread, 0L, Constants.PING_INTERVAL, TimeUnit.MILLISECONDS);
        }

        if (R2BConfiguration.metricsEnabled) {
            getLogger().info("***********************************");
            getLogger().info("ReleaseToBeta collects data and sends them to bstats");
            getLogger().info("You can disable that by changing 'enable_metrics' from 'true' to 'false'");
            getLogger().info("***********************************");
            new Metrics.R2BMetrics(this);
        }

        long endTime = System.nanoTime();
        getLogger().info("Ready for connections (" + ((endTime - startTime) / 1000000L) + "ms)");
    }

    public void stop() {
        server.getServerConnection().shutdown();
        dataConverter.cleanup();
    }

    @Override
    public void run() {
        try {
            this.server.getServerConnection().tick();

            for (MultiSession session : sessionRegistry.getSessions().values()) {
                session.getBetaClientSession().tick();
            }
        } catch (Throwable throwable) {
            getLogger().error("Exception in tick loop (" + throwable.getMessage() + ")");
            getLogger().error("Stopping server");

            stop();
        }
    }

    //helper method
    public AbstractLogger getLogger() {
        return bootstrap.getAppLogger();
    }
}
