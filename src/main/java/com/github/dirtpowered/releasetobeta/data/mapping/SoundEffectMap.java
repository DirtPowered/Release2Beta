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
import com.github.steveice10.mc.protocol.data.game.world.effect.ParticleEffect;
import com.github.steveice10.mc.protocol.data.game.world.effect.SoundEffect;
import com.github.steveice10.mc.protocol.data.game.world.effect.WorldEffect;

public class SoundEffectMap extends DataHolder<WorldEffect> {

    public SoundEffectMap() {
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
