package com.github.dirtpowered.releasetobeta.network.server.ping.LegacyPing;

import com.github.dirtpowered.releasetobeta.network.server.ping.LegacyPing.model.PingMessage;
import org.pmw.tinylog.Logger;

public class PingPassthroughThread implements Runnable {
    private PingMessage pingMessage;

    @Override
    public void run() {
        try {
            new LegacyPingConnector().connect(result -> {
                pingMessage = result;
            });
        } catch (InterruptedException e) {
            Logger.error(e.getMessage());
        }
    }

    public PingMessage getPingMessage() {
        return pingMessage;
    }
}
