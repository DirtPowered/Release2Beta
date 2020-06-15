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

package com.github.dirtpowered.releasetobeta.data.entity;

import com.github.dirtpowered.betaprotocollib.data.WatchableObject;
import com.github.dirtpowered.releasetobeta.data.Constants;
import com.github.dirtpowered.releasetobeta.data.entity.model.Entity;
import com.github.dirtpowered.releasetobeta.data.entity.monster.EntityCreeper;
import com.github.dirtpowered.releasetobeta.data.entity.monster.EntityEnderDragon;
import com.github.dirtpowered.releasetobeta.data.player.BetaPlayer;
import com.github.dirtpowered.releasetobeta.data.player.ModernPlayer;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.EntityMetadata;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.MetadataType;
import com.github.steveice10.mc.protocol.data.game.entity.type.MobType;
import com.github.steveice10.mc.protocol.data.game.world.block.BlockState;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntitySetPassengersPacket;
import com.github.steveice10.packetlib.Session;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class MetadataTranslator {

    public EntityMetadata[] toModernMetadata(ModernPlayer target, Session modernSession, Entity e, List<WatchableObject> oldMetadata) {
        MobType mobType = null;

        if (e != null)
            mobType = e.getMobType();

        List<EntityMetadata> metadataList = new ArrayList<>();

        for (WatchableObject watchableObject : oldMetadata) {
            MetadataType type = IntegerToDataType(watchableObject.getType());
            int index = watchableObject.getIndex();
            Object value = watchableObject.getValue();

            if (type == MetadataType.BYTE && index == 0) {
                if (((Byte) value).intValue() == 0x04) { //entity mount
                    if (e instanceof ModernPlayer) {
                        ModernPlayer modernPlayer = (ModernPlayer) e;
                        target.sendPacket(new ServerEntitySetPassengersPacket(modernPlayer.getVehicleEntityId(), e.getEntityId()));
                    } else if (e instanceof BetaPlayer) {
                        //TODO: Vehicle cache
                    }
                } else if (((Byte) value).intValue() == 0x00) {
                    if (e instanceof ModernPlayer) {
                        ModernPlayer modernPlayer = (ModernPlayer) e;

                        if (!modernPlayer.isInVehicle()) { //entity un-mount
                            if (modernPlayer.getVehicleEntityId() != -1) {
                                target.sendPacket(new ServerEntitySetPassengersPacket(modernPlayer.getVehicleEntityId()));

                                modernPlayer.setVehicleEntityId(-1);
                            }
                        }
                    }

                    metadataList.add(new EntityMetadata(0, MetadataType.BYTE, value));
                } else {
                    metadataList.add(new EntityMetadata(0, MetadataType.BYTE, value));
                }
            }

            if ((!(e instanceof BetaPlayer)) && (!(e instanceof ModernPlayer))) {

                if (type == MetadataType.BYTE && index == 16) {
                    //sheep color
                    if (mobType == MobType.SHEEP) {
                        metadataList.add(new EntityMetadata(13, MetadataType.BYTE, value));
                    } else if (mobType == MobType.CREEPER) {
                        //creeper fuse
                        Byte b = (Byte) value;
                        if (e instanceof EntityCreeper) {
                            double dist = target.getLocation().distanceTo(e.getLocation());
                            if (dist < Constants.SOUND_RANGE) {
                                ((EntityCreeper) e).onPrime(modernSession);
                            }
                        }

                        metadataList.add(new EntityMetadata(12, MetadataType.INT, b.intValue()));
                    } else if (mobType == MobType.WOLF) {
                        metadataList.add(new EntityMetadata(13, MetadataType.BYTE, value));
                    } else if (mobType == MobType.PIG) {
                        boolean hasSaddle = ((Byte) value).intValue() == 1;

                        metadataList.add(new EntityMetadata(13, MetadataType.BOOLEAN, hasSaddle));
                    } else if (mobType == MobType.SLIME || mobType == MobType.MAGMA_CUBE) {
                        Byte b = (Byte) value;

                        metadataList.add(new EntityMetadata(12, MetadataType.INT, b.intValue()));
                    } else if (mobType == MobType.GHAST) {
                        boolean isAggressive = ((Byte) value).intValue() == 1;

                        metadataList.add(new EntityMetadata(12, MetadataType.BOOLEAN, isAggressive));
                    } else if (mobType == MobType.ENDERMAN) {
                        int itemId = ((Byte) value).intValue();

                        if (((Byte) value).intValue() > 0) {
                            metadataList.add(new EntityMetadata(12, MetadataType.BLOCK_STATE, new BlockState(itemId, 0)));
                        }
                    } else if (mobType == MobType.BLAZE) {
                        metadataList.add(new EntityMetadata(12, MetadataType.BYTE, value));
                    }
                }

                if (type == MetadataType.BYTE && index == 17) {
                    if (mobType == MobType.CREEPER || mobType == MobType.ENDERMAN) {
                        //is powered or enderman screaming
                        boolean state = ((Byte) value).intValue() == 1;
                        metadataList.add(new EntityMetadata(13, MetadataType.BOOLEAN, state));
                    }
                }

                if (type == MetadataType.INT && index == 12) {
                    //baby animals
                    metadataList.add(new EntityMetadata(12, MetadataType.BOOLEAN, (int) value < 0));
                }

                if (type == MetadataType.INT && index == 16) {
                    if (mobType == MobType.ENDER_DRAGON) {
                        int health = (int) value;
                        EntityEnderDragon enderDragon = (EntityEnderDragon) e;
                        enderDragon.updateHealth(modernSession, health);
                    }
                }
            }
        }

        return metadataList.toArray(new EntityMetadata[0]);
    }

    private MetadataType IntegerToDataType(int type) {
        MetadataType metadataType;

        switch (type) {
            case 0:
                metadataType = MetadataType.BYTE;
                break;
            case 2:
                metadataType = MetadataType.INT;
                break;
            case 3:
                metadataType = MetadataType.FLOAT;
                break;
            case 4:
                metadataType = MetadataType.STRING;
                break;
            case 5:
                metadataType = MetadataType.ITEM;
                break;
            case 6:
                metadataType = MetadataType.POSITION;
                break;
            default:
                metadataType = null;
                break;
        }
        return metadataType;
    }
}
