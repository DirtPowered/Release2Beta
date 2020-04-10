package com.github.dirtpowered.releasetobeta.network.server.ping.LegacyPing;

import com.github.dirtpowered.releasetobeta.network.server.ping.LegacyPing.model.PingMessage;
import lombok.Getter;
import org.pmw.tinylog.Logger;

@Getter
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
}
