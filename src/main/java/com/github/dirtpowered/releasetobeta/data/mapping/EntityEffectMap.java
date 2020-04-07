package com.github.dirtpowered.releasetobeta.data.mapping;

import com.github.steveice10.mc.protocol.data.game.entity.Effect;

public class EntityEffectMap extends Remapper<Effect> {

    public EntityEffectMap() {
        remap(1, Effect.SPEED);
        remap(2, Effect.SLOWNESS);
        remap(3, Effect.DIG_SPEED);
        remap(4, Effect.DIG_SLOWNESS);
        remap(5, Effect.DAMAGE_BOOST);
        remap(6, Effect.HEAL);
        remap(7, Effect.DAMAGE);
        remap(8, Effect.JUMP_BOOST);
        remap(9, Effect.CONFUSION);
        remap(10, Effect.REGENERATION);
        remap(11, Effect.RESISTANCE);
        remap(12, Effect.FIRE_RESISTANCE);
        remap(13, Effect.WATER_BREATHING);
        remap(14, Effect.INVISIBILITY);
        remap(15, Effect.BLINDNESS);
        remap(16, Effect.NIGHT_VISION);
        remap(17, Effect.HUNGER);
        remap(18, Effect.WEAKNESS);
        remap(19, Effect.POISON);
    }
}
