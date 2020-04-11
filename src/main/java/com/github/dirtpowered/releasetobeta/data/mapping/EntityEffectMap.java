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
