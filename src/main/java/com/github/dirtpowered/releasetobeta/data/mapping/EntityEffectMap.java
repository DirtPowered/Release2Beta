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

public class EntityEffectMap extends DataHolder<Effect> {

    public EntityEffectMap() {
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
