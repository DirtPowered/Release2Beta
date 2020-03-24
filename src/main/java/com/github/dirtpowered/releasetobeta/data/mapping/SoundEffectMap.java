package com.github.dirtpowered.releasetobeta.data.mapping;

import com.github.steveice10.mc.protocol.data.game.world.effect.ParticleEffect;
import com.github.steveice10.mc.protocol.data.game.world.effect.SoundEffect;
import com.github.steveice10.mc.protocol.data.game.world.effect.WorldEffect;

public class SoundEffectMap extends Remapper<WorldEffect> {

    public SoundEffectMap() {
        remap(1003, SoundEffect.BLOCK_WOODEN_DOOR_OPEN);
        remap(1004, SoundEffect.BLOCK_FIRE_EXTINGUISH);
        remap(1007, SoundEffect.ENTITY_GHAST_WARN);
        remap(2001, ParticleEffect.BREAK_BLOCK);
    }
}
