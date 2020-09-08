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

package com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_7;

import com.github.dirtpowered.betaprotocollib.data.BetaItemStack;
import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.PickupSpawnPacketData;
import com.github.dirtpowered.betaprotocollib.utils.Location;
import com.github.dirtpowered.releasetobeta.ReleaseToBeta;
import com.github.dirtpowered.releasetobeta.data.entity.object.EntityItem;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.dirtpowered.releasetobeta.utils.Utils;
import com.github.dirtpowered.releasetobeta.utils.item.ItemConverter;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.EntityMetadata;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.MetadataType;
import com.github.steveice10.mc.protocol.data.game.entity.type.object.ObjectType;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityMetadataPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnObjectPacket;
import com.github.steveice10.packetlib.Session;

import java.util.Arrays;
import java.util.UUID;

public class PickupSpawnTranslator implements BetaToModern<PickupSpawnPacketData> {

    @Override
    public void translate(ReleaseToBeta main, PickupSpawnPacketData packet, BetaClientSession session, Session modernSession) {
        int entityId = packet.getEntityId();
        UUID uuid = UUID.randomUUID();

        double x = Utils.toModernPos(packet.getX());
        double y = Utils.toModernPos(packet.getY()) - 0.125D;
        double z = Utils.toModernPos(packet.getZ());

        BetaItemStack itemStack = packet.getItemStack();
        int itemId = itemStack.getBlockId();

        itemStack.setBlockId(session.remapBlock(itemId, itemStack.getData(), true));
        itemStack.setData((session.remapMetadata(itemId, itemStack.getData(), ItemConverter.isItemDataIgnored(itemId))));

        EntityMetadata[] metadata = Arrays.asList(
                new EntityMetadata(1, MetadataType.INT, 300), //air time
                new EntityMetadata(4, MetadataType.BOOLEAN, false), //silent
                new EntityMetadata(5, MetadataType.BOOLEAN, false), //no gravity
                new EntityMetadata(6, MetadataType.ITEM, ItemConverter.betaToModern(session, itemStack))
        ).toArray(new EntityMetadata[0]);

        modernSession.send(new ServerSpawnObjectPacket(entityId, uuid, ObjectType.ITEM, x, y, z, 0, 0));
        modernSession.send(new ServerEntityMetadataPacket(entityId, metadata));

        // cache entity
        EntityItem object = new EntityItem(entityId);
        object.setLocation(new Location(x, y, z));
        object.onSpawn(modernSession);

        session.getEntityCache().addEntity(entityId, object);
    }
}
