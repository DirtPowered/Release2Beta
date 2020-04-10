package com.github.dirtpowered.releasetobeta.data.command;

import com.github.dirtpowered.releasetobeta.data.command.model.Command;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;

@Getter
@NoArgsConstructor
public class CommandRegistry {
    private HashMap<String, Command> commands = new HashMap<>();

    public void register(String name, Command command) {
        commands.put(name, command);
    }
}
