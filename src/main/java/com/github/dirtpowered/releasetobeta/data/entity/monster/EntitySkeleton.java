package com.github.dirtpowered.releasetobeta.data.entity.monster;

import com.github.dirtpowered.releasetobeta.data.entity.model.Entity;
import com.github.dirtpowered.releasetobeta.data.entity.model.Mob;
import com.github.steveice10.mc.protocol.data.game.entity.EquipmentSlot;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.ItemStack;
import com.github.steveice10.mc.protocol.data.game.entity.type.MobType;
import com.github.steveice10.mc.protocol.data.game.world.sound.BuiltinSound;
import com.github.steveice10.mc.protocol.data.game.world.sound.SoundCategory;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityEquipmentPacket;
import com.github.steveice10.packetlib.Session;

public class EntitySkeleton extends Entity implements Mob {

    public EntitySkeleton(int entityId) {
        super(entityId, MobType.SKELETON);
    }

    @Override
    public void onSpawn(Session session) {
        ItemStack itemStack = new ItemStack(261, 1, 0);
        session.send(new ServerEntityEquipmentPacket(getEntityId(), EquipmentSlot.MAIN_HAND, itemStack));
    }

    @Override
    public void onDeath(Session session) {
        playSound(session, BuiltinSound.ENTITY_SKELETON_DEATH, SoundCategory.HOSTILE);
    }

    @Override
    public void onDamage(Session session) {
        playSound(session, BuiltinSound.ENTITY_SKELETON_HURT, SoundCategory.HOSTILE);
    }
}
