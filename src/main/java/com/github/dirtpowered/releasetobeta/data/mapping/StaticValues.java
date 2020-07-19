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

package com.github.dirtpowered.releasetobeta.data.mapping;

import com.github.dirtpowered.releasetobeta.data.mapping.model.DataHolder;
import com.github.steveice10.mc.protocol.data.game.entity.Effect;
import com.github.steveice10.mc.protocol.data.game.entity.type.MobType;
import com.github.steveice10.mc.protocol.data.game.entity.type.PaintingType;
import com.github.steveice10.mc.protocol.data.game.setting.Difficulty;
import com.github.steveice10.mc.protocol.data.game.world.effect.ParticleEffect;
import com.github.steveice10.mc.protocol.data.game.world.effect.SoundEffect;
import com.github.steveice10.mc.protocol.data.game.world.effect.WorldEffect;

public class StaticValues {
    private static DifficultyMap difficultyMap;
    private static EntityEffectMap entityEffectMap;
    private static MobTypeMap mobTypeMap;
    private static PaintingTypeMap paintingTypeMap;
    private static SoundEffectMap soundEffectMap;

    static {
        difficultyMap = new DifficultyMap();
        entityEffectMap = new EntityEffectMap();
        mobTypeMap = new MobTypeMap();
        paintingTypeMap = new PaintingTypeMap();
        soundEffectMap = new SoundEffectMap();
    }

    public static Difficulty getDifficulty(int difficulty) {
        return difficultyMap.getFromId(difficulty);
    }

    public static Effect getEntityEffect(int effect) {
        return entityEffectMap.getFromId(effect);
    }

    public static MobType getMobType(int mobId) {
        return mobTypeMap.getFromId(mobId);
    }

    public static PaintingType getPaintingType(String paintingStr) {
        return paintingTypeMap.getFromId(paintingStr);
    }

    public static WorldEffect getWorldEffect(int effectId) {
        return soundEffectMap.getFromId(effectId);
    }

    static class DifficultyMap extends DataHolder<Integer, Difficulty> {

        DifficultyMap() {
            add(0, Difficulty.PEACEFUL);
            add(1, Difficulty.EASY);
            add(2, Difficulty.NORMAL);
            add(3, Difficulty.HARD);
        }
    }

    static class EntityEffectMap extends DataHolder<Integer, Effect> {

        EntityEffectMap() {
            add(1, Effect.FASTER_MOVEMENT);
            add(2, Effect.SLOWER_MOVEMENT);
            add(3, Effect.FASTER_DIG);
            add(4, Effect.SLOWER_DIG);
            add(5, Effect.INCREASE_DAMAGE);
            add(6, Effect.HEAL);
            add(7, Effect.HARM);
            add(8, Effect.JUMP);
            add(9, Effect.CONFUSION);
            add(10, Effect.REGENERATION);
            add(11, Effect.RESISTANCE);
            add(12, Effect.FIRE_RESISTANCE);
            add(13, Effect.WATER_BREATHING);
            add(14, Effect.INVISIBILITY);
            add(15, Effect.BLINDNESS);
            add(16, Effect.NIGHT_VISION);
            add(17, Effect.HUNGER);
            add(18, Effect.WEAKNESS);
            add(19, Effect.POISON);
        }
    }

    static class MobTypeMap extends DataHolder<Integer, MobType> {

        MobTypeMap() {
            add(50, MobType.CREEPER);
            add(51, MobType.SKELETON);
            add(52, MobType.SPIDER);
            add(53, MobType.GIANT);
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
            add(97, MobType.SNOW_GOLEM);
            add(120, MobType.VILLAGER);
        }
    }

    static class PaintingTypeMap extends DataHolder<String, PaintingType> {

        PaintingTypeMap() {
            add("Kebab", PaintingType.KEBAB);
            add("Aztec", PaintingType.AZTEC);
            add("Alban", PaintingType.ALBAN);
            add("Aztec2", PaintingType.AZTEC2);
            add("Bomb", PaintingType.BOMB);
            add("Plant", PaintingType.PLANT);
            add("Wasteland", PaintingType.WASTELAND);
            add("Pool", PaintingType.POOL);
            add("Courbet", PaintingType.COURBET);
            add("Sea", PaintingType.SEA);
            add("Sunset", PaintingType.SUNSET);
            add("Creebet", PaintingType.CREEBET);
            add("Wanderer", PaintingType.WANDERER);
            add("Graham", PaintingType.GRAHAM);
            add("Match", PaintingType.MATCH);
            add("Bust", PaintingType.BUST);
            add("Stage", PaintingType.STAGE);
            add("Void", PaintingType.VOID);
            add("SkullAndRoses", PaintingType.SKULL_AND_ROSES);
            add("Fighters", PaintingType.FIGHTERS);
            add("Pointer", PaintingType.POINTER);
            add("Pigscene", PaintingType.PIG_SCENE);
            add("BurningSkull", PaintingType.BURNING_SKULL);
            add("Skeleton", PaintingType.SKELETON);
            add("DonkeyKong", PaintingType.DONKEY_KONG);
        }
    }

    static class SoundEffectMap extends DataHolder<Integer, WorldEffect> {

        SoundEffectMap() {
            add(1000, SoundEffect.BLOCK_DISPENSER_DISPENSE);
            add(1001, SoundEffect.BLOCK_DISPENSER_FAIL);
            add(1002, SoundEffect.BLOCK_DISPENSER_LAUNCH);
            add(1003, SoundEffect.BLOCK_WOODEN_DOOR_OPEN);
            add(1004, SoundEffect.BLOCK_FIRE_EXTINGUISH);
            add(1005, SoundEffect.RECORD);
            add(1007, SoundEffect.ENTITY_GHAST_WARN);
            add(1008, SoundEffect.ENTITY_GHAST_SHOOT);
            add(1009, SoundEffect.ENTITY_BLAZE_SHOOT);
            add(2000, ParticleEffect.SMOKE);
            add(2001, ParticleEffect.BREAK_BLOCK);
        }
    }

}
