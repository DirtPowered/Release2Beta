package com.github.dirtpowered.releasetobeta;

import org.pmw.tinylog.Configurator;
import org.pmw.tinylog.Logger;

public class Main {

    private static ReleaseToBeta server;

    public static void main(String... arguments) {
        Configurator.currentConfig().formatPattern("[{level} {date:HH:mm:ss}] {message}").activate();
        server = new ReleaseToBeta();
        addShutdownHook();
    }

    private static void addShutdownHook() {
        Thread stopThread = new Thread(() -> {
            Logger.info("Stopping ...");
            server.stop();
        });

        Runtime.getRuntime().addShutdownHook(stopThread);
    }
}
