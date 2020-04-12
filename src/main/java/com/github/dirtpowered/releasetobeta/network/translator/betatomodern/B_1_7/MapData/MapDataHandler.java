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

package com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_7.MapData;

import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.MapDataPacketData;
import com.github.steveice10.mc.protocol.data.game.world.map.MapData;
import com.github.steveice10.mc.protocol.data.game.world.map.MapIcon;
import com.github.steveice10.mc.protocol.data.game.world.map.MapIconType;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerMapDataPacket;
import com.github.steveice10.packetlib.Session;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class MapDataHandler {
    private byte[] colors = new byte[128 * 128];

    public void translateMapData(MapDataPacketData data, Session session) {
        List<MapIcon> icons = new ArrayList<>();
        int mapId = data.getMapId();
        byte[] buffer = data.getData();

        if (buffer[0] == 0) {
            //updating map content
            int startX = buffer[1] & 0xFF;
            int startY = buffer[2] & 0xFF;

            for (int index = 0; index < buffer.length - 3; ++index) {
                colors[(index + startY) * 128 + startX] = buffer[index + 3];
            }

            session.send(new ServerMapDataPacket(mapId, (byte) 4, false, new MapIcon[0], new MapData(128, 128, 0, 0, colors)));
        } else if (buffer[0] == 1) {
            //updating cursor
            for (int locIndex = 0; locIndex < (buffer.length - 1) / 3; ++locIndex) {
                byte centerX = buffer[locIndex * 3 + 2];
                byte centerZ = buffer[locIndex * 3 + 3];
                byte iconRotation = (byte) (buffer[locIndex * 3 + 1] / 16);

                icons.add(new MapIcon(centerX, centerZ, MapIconType.WHITE_ARROW, iconRotation));
                session.send(new ServerMapDataPacket(mapId, (byte) 4, false, icons.toArray(new MapIcon[0]), null));
            }
        }
    }
}
