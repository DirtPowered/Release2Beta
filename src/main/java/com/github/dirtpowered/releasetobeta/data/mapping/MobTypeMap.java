package com.github.dirtpowered.releasetobeta.data.mapping;

import com.github.dirtpowered.releasetobeta.data.mapping.model.DataHolder;
import com.github.steveice10.mc.protocol.data.game.entity.type.MobType;

public class MobTypeMap extends DataHolder<MobType> {

    public MobTypeMap() {
        add(50, MobType.CREEPER);
        add(51, MobType.SKELETON);
        add(52, MobType.SPIDER);
        add(53, MobType.GIANT_ZOMBIE);
        add(54, MobType.ZOMBIE);
        add(55, MobType.SLIME);
        add(56, MobType.GHAST);
        add(57, MobType.ZOMBIE_PIGMAN);
        add(58, MobType.ENDERMAN);
        add(59, MobType.CAVE_SPIDER);
        add(60, MobType.SILVERFISH);
        add(61, MobType.BLAZE);
        add(62, MobType.MAGMA_CUBE);
        add(63, MobType.ENDER_DRAGON);
        add(90, MobType.PIG);
        add(91, MobType.SHEEP);
        add(92, MobType.COW);
        add(93, MobType.CHICKEN);
        add(94, MobType.SQUID);
        add(95, MobType.WOLF);
        add(96, MobType.MOOSHROOM);
        add(97, MobType.SNOWMAN);
        add(120, MobType.VILLAGER);
    }
}
