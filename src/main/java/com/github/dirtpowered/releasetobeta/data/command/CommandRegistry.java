package com.github.dirtpowered.releasetobeta.data.command;

import com.github.dirtpowered.releasetobeta.data.command.model.Command;

import java.util.HashMap;

public class CommandRegistry {
    private HashMap<String, Command> commands = new HashMap<>();

    public CommandRegistry() {

    }

    public void register(String name, Command command) {
        commands.put(name, command);
    }

    public HashMap<String, Command> getCommands() {
        return commands;
    }
}
