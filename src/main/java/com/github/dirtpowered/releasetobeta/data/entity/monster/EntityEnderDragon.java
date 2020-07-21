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

package com.github.dirtpowered.releasetobeta.data.entity.monster;

import com.github.dirtpowered.releasetobeta.data.entity.model.Entity;
import com.github.dirtpowered.releasetobeta.data.entity.model.Mob;
import com.github.dirtpowered.releasetobeta.utils.chat.ChatUtils;
import com.github.steveice10.mc.protocol.data.game.BossBarColor;
import com.github.steveice10.mc.protocol.data.game.BossBarDivision;
import com.github.steveice10.mc.protocol.data.game.entity.type.EntityType;
import com.github.steveice10.mc.protocol.data.game.world.sound.BuiltinSound;
import com.github.steveice10.mc.protocol.data.game.world.sound.SoundCategory;
import com.github.steveice10.mc.protocol.data.message.Message;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerBossBarPacket;
import com.github.steveice10.packetlib.Session;

import java.util.UUID;

public class EntityEnderDragon extends Entity implements Mob {
    private static UUID uuid;

    static {
        uuid = UUID.randomUUID();
    }

    public EntityEnderDragon(int entityId) {
        super(entityId, EntityType.ENDER_DRAGON);
    }

    @Override
    public void onSpawn(Session session) {
        Message message = ChatUtils.toModernMessage("&dBoss health", true);
        float health = 0.005f;
        BossBarColor color = BossBarColor.PINK;
        BossBarDivision division = BossBarDivision.NONE;

        session.send(new ServerBossBarPacket(uuid, message, health, color, division, false, true, false));
    }

    public void updateHealth(Session session, int health) {
        float a = health * 1.0f / 200;
        session.send(new ServerBossBarPacket(uuid, a));
    }

    private void destroyBossBar(Session session) {
        session.send(new ServerBossBarPacket(uuid));
    }

    @Override
    public void onDeath(Session session) {
        playSound(session, BuiltinSound.ENTITY_ENDER_DRAGON_DEATH, SoundCategory.HOSTILE);
    }

    @Override
    public void onDamage(Session session) {
        playSound(session, BuiltinSound.ENTITY_ENDER_DRAGON_HURT, SoundCategory.HOSTILE);
    }

    @Override
    public void onUpdate(Session session) {
        playSound(session, BuiltinSound.ENTITY_ENDER_DRAGON_AMBIENT, SoundCategory.HOSTILE);
    }

    @Override
    public void onDestroy(Session session) {
        destroyBossBar(session);
    }
}
