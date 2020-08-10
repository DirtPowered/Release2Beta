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

package com.github.dirtpowered.releasetobeta.data.entity.object;

import com.github.dirtpowered.betaprotocollib.utils.Location;
import com.github.dirtpowered.releasetobeta.data.entity.model.Entity;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityTeleportPacket;
import com.github.steveice10.packetlib.Session;

public class EntityItem extends Entity {

    private Session session;
    private int lastCheck;

    public EntityItem(int entityId) {
        super(entityId);
    }

    @Override
    public void onSpawn(Session session) {
        this.session = session;
    }

    @Override
    public void tick() {
        lastCheck = (lastCheck + 1) % 20;
        if (lastCheck == 0) {
            if (((int) ((getLocation().getY() + 0.001) * 1000) % 1000) - 1 == 125) {
                Location l = getLocation();

                // update location
                setLocation(new Location(l.getX(), l.getY() - 0.125D, l.getZ()));

                // send location update
                session.send(new ServerEntityTeleportPacket(
                        getEntityId(), l.getX(), l.getY() - 0.125D, l.getZ(), l.getYaw(), l.getPitch(), true)
                );
            }
        }
    }
}
