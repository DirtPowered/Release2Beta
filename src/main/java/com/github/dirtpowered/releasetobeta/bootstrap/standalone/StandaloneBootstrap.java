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

package com.github.dirtpowered.releasetobeta.bootstrap.standalone;

import com.github.dirtpowered.releasetobeta.ReleaseToBeta;
import com.github.dirtpowered.releasetobeta.bootstrap.AbstractBootstrap;
import com.github.dirtpowered.releasetobeta.logger.AbstractLogger;
import com.github.dirtpowered.releasetobeta.logger.DefaultLogger;
import org.pmw.tinylog.Configurator;

public class StandaloneBootstrap implements AbstractBootstrap {
    private ReleaseToBeta server;
    private DefaultLogger logger;

    public static void main(String[] args) {
        new StandaloneBootstrap().onEnable();
    }

    @Override
    public void onEnable() {
        initDefaultLogger();

        logger = new DefaultLogger();
        server = new ReleaseToBeta(this);

        addShutdownHook();
    }

    @Override
    public void onDisable() {
        server.stop();
    }

    @Override
    public AbstractLogger getLogger() {
        return logger;
    }

    private void addShutdownHook() {
        Thread stopThread = new Thread(() -> {
            logger.info("Stopping ...");
            onDisable();
        });

        Runtime.getRuntime().addShutdownHook(stopThread);
    }

    private void initDefaultLogger() {
        Configurator.currentConfig().formatPattern("[{level} {date:HH:mm:ss}] {message}").activate();
    }
}
