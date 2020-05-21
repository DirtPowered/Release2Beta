package com.github.dirtpowered.releasetobeta.api.plugin;

public abstract class Plugin {

    public abstract void onEnable();

    public abstract void onDisable();

    public String getName() {
        return getClass().getSimpleName();
    }
}
