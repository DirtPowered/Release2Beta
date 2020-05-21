package com.github.dirtpowered.releasetobeta.api.plugin.event.player;

import com.github.dirtpowered.releasetobeta.api.plugin.event.Event;
import com.github.dirtpowered.releasetobeta.data.player.ModernPlayer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class PlayerJoinEvent extends Event {
    private ModernPlayer player;
}
