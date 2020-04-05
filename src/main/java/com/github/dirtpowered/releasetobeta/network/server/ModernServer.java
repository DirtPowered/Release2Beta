package com.github.dirtpowered.releasetobeta.network.server;

import com.github.dirtpowered.releasetobeta.ReleaseToBeta;
import com.github.dirtpowered.releasetobeta.data.command.CommandRegistry;
import com.github.dirtpowered.releasetobeta.data.command.R2BCommand;
import com.github.dirtpowered.releasetobeta.data.command.model.Command;
import com.github.dirtpowered.releasetobeta.data.entity.EntityRegistry;
import com.github.dirtpowered.releasetobeta.data.player.ModernPlayer;
import com.github.dirtpowered.releasetobeta.data.skin.ProfileCache;

public class ModernServer {
    private ReleaseToBeta main;
    private ServerConnection serverConnection;
    private EntityRegistry entityRegistry;
    private ProfileCache profileCache;
    private CommandRegistry commandRegistry;

    public ModernServer(ReleaseToBeta releaseToBeta) {
        this.main = releaseToBeta;

        this.serverConnection = new ServerConnection(this);
        this.entityRegistry = new EntityRegistry();
        this.profileCache = new ProfileCache();
        this.commandRegistry = new CommandRegistry();

        registerInternalCommands();
    }

    private void registerInternalCommands() {
        commandRegistry.register("releasetobeta", new R2BCommand());
    }

    public boolean executeCommand(ModernPlayer player, String message) {
        message = message.substring(1);
        String[] args = message.trim().split("\\s+");

        Command command;
        if (commandRegistry.getCommands().containsKey(args[0])) {
            command = commandRegistry.getCommands().get(args[0]);
            command.execute(player, args);
            return true;
        }

        return false;
    }

    public String[] getCommands() {
        return commandRegistry.getCommands().keySet().toArray(new String[0]);
    }

    public ReleaseToBeta getMain() {
        return main;
    }

    public ServerConnection getServerConnection() {
        return serverConnection;
    }

    public CommandRegistry getCommandRegistry() {
        return commandRegistry;
    }

    public EntityRegistry getEntityRegistry() {
        return entityRegistry;
    }

    public ProfileCache getProfileCache() {
        return profileCache;
    }
}
