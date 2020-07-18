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

package com.github.dirtpowered.releasetobeta.network.protocol;

import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.AnimationPacketData;
import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.AttachEntityPacketData;
import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.BlockChangePacketData;
import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.BlockItemSwitchPacketData;
import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.ChatPacketData;
import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.CloseWindowPacketData;
import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.CollectPacketData;
import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.EntityDestroyPacketData;
import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.EntityEquipmentPacketData;
import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.EntityLookPacketData;
import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.EntityMetadataPacketData;
import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.EntityMoveLookPacketData;
import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.EntityPositionPacketData;
import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.EntityStatusPacketData;
import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.EntityTeleportPacketData;
import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.EntityVelocityPacketData;
import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.ExplosionPacketData;
import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.HandshakePacketData;
import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.KickDisconnectPacketData;
import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.MapChunkPacketData;
import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.MapDataPacketData;
import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.MobSpawnPacketData;
import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.MultiBlockChangePacketData;
import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.NamedEntitySpawnPacketData;
import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.OpenWindowPacketData;
import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.PaintingPacketData;
import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.PickupSpawnPacketData;
import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.PlayNoteblockPacketData;
import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.PlayerLookMovePacketData;
import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.PreChunkPacketData;
import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.SetSlotPacketData;
import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.SleepPacketData;
import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.SoundEffectPacketData;
import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.SpawnPositionPacketData;
import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.StatisticsPacketData;
import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.ThunderboltPacketData;
import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.TransactionPacketData;
import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.UpdateProgressPacketData;
import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.UpdateSignPacketData;
import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.UpdateTimePacketData;
import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.VehicleSpawnPacketData;
import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.WindowItemsPacketData;
import com.github.dirtpowered.betaprotocollib.packet.Version_B1_8.data.BedAndWeatherPacketData;
import com.github.dirtpowered.betaprotocollib.packet.Version_B1_8.data.EntityEffectPacketData;
import com.github.dirtpowered.betaprotocollib.packet.Version_B1_8.data.ExperienceOrbPacketData;
import com.github.dirtpowered.betaprotocollib.packet.Version_B1_8.data.ExperienceUpdatePacketData;
import com.github.dirtpowered.betaprotocollib.packet.Version_B1_8.data.KeepAlivePacketData;
import com.github.dirtpowered.betaprotocollib.packet.Version_B1_8.data.LoginPacketData;
import com.github.dirtpowered.betaprotocollib.packet.Version_B1_8.data.PlayerListItemPacketData;
import com.github.dirtpowered.betaprotocollib.packet.Version_B1_8.data.RemoveEntityEffectPacketData;
import com.github.dirtpowered.betaprotocollib.packet.Version_B1_8.data.RespawnPacketData;
import com.github.dirtpowered.betaprotocollib.packet.Version_B1_8.data.UpdateHealthPacketData;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_7.AttachEntityTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_7.BlockChangeTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_7.BlockItemSwitchTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_7.ChatTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_7.CloseWindowTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_7.CollectTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_7.EntityDestroyTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_7.EntityEquipmentTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_7.EntityLookTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_7.EntityMetadataTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_7.EntityMoveLookTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_7.EntityPositionTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_7.EntityStatusTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_7.EntityTeleportTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_7.EntityVelocityTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_7.ExplosionTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_7.HandshakeTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_7.KickDisconnectTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_7.MapChunkTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_7.MapDataTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_7.MobSpawnTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_7.MultiBlockChangeTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_7.NamedEntitySpawnTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_7.OpenWindowTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_7.PaintingTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_7.PickupSpawnTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_7.PlayNoteblockTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_7.PlayerLookMoveTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_7.PreChunkTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_7.SetSlotTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_7.SleepPacketTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_7.SoundEffectTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_7.SpawnPositionTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_7.StatisticsTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_7.ThunderboltTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_7.TransactionTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_7.UpdateProgressTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_7.UpdateSignTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_7.UpdateTimeTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_7.VehicleSpawnTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_7.WindowItemsTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_8.AnimationTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_8.BedAndWeatherTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_8.EntityEffectTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_8.ExperienceOrbTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_8.ExperienceUpdateTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_8.KeepAliveTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_8.LoginTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_8.PlayerListItemTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_8.RemoveEntityEffectTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_8.RespawnTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_8.UpdateHealthTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.moderntobeta.B_1_7.ClientChatTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.moderntobeta.B_1_7.ClientCloseWindowTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.moderntobeta.B_1_7.ClientConfirmTransactionTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.moderntobeta.B_1_7.ClientPlayerActionTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.moderntobeta.B_1_7.ClientPlayerChangeHeldItemTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.moderntobeta.B_1_7.ClientPlayerInteractEntityTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.moderntobeta.B_1_7.ClientPlayerPlaceBlockTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.moderntobeta.B_1_7.ClientPlayerPositionRotationTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.moderntobeta.B_1_7.ClientPlayerPositionTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.moderntobeta.B_1_7.ClientPlayerRotationTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.moderntobeta.B_1_7.ClientPlayerSwingArmTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.moderntobeta.B_1_7.ClientPlayerUseItemTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.moderntobeta.B_1_7.ClientSteerVehicleTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.moderntobeta.B_1_7.ClientTeleportConfirmTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.moderntobeta.B_1_7.ClientUpdateSignTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.moderntobeta.B_1_7.ClientWindowActionTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.moderntobeta.B_1_8.ClientCreativeInventoryActionTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.moderntobeta.B_1_8.ClientKeepAliveTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.moderntobeta.B_1_8.ClientPlayerStateTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.moderntobeta.B_1_8.ClientRequestTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.moderntobeta.B_1_8.LoginStartTranslator;
import com.github.dirtpowered.releasetobeta.network.translator.registry.BetaToModernTranslatorRegistry;
import com.github.dirtpowered.releasetobeta.network.translator.registry.ModernToBetaTranslatorRegistry;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientKeepAlivePacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientRequestPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerActionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerChangeHeldItemPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerInteractEntityPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPlaceBlockPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPositionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPositionRotationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerRotationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerStatePacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerSwingArmPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerUseItemPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.window.ClientCloseWindowPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.window.ClientConfirmTransactionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.window.ClientCreativeInventoryActionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.window.ClientWindowActionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.world.ClientSteerVehiclePacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.world.ClientTeleportConfirmPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.world.ClientUpdateSignPacket;
import com.github.steveice10.mc.protocol.packet.login.client.LoginStartPacket;

public class B_1_8 {

    public B_1_8(BetaToModernTranslatorRegistry betaToModernTranslatorRegistry, ModernToBetaTranslatorRegistry modernToBetaTranslatorRegistry) {
        //1.8
        betaToModernTranslatorRegistry.registerTranslator(UpdateHealthPacketData.class, new UpdateHealthTranslator());
        betaToModernTranslatorRegistry.registerTranslator(LoginPacketData.class, new LoginTranslator());
        betaToModernTranslatorRegistry.registerTranslator(RespawnPacketData.class, new RespawnTranslator());
        betaToModernTranslatorRegistry.registerTranslator(BedAndWeatherPacketData.class, new BedAndWeatherTranslator());
        betaToModernTranslatorRegistry.registerTranslator(ExperienceOrbPacketData.class, new ExperienceOrbTranslator());
        betaToModernTranslatorRegistry.registerTranslator(ExperienceUpdatePacketData.class, new ExperienceUpdateTranslator());
        betaToModernTranslatorRegistry.registerTranslator(PlayerListItemPacketData.class, new PlayerListItemTranslator());
        betaToModernTranslatorRegistry.registerTranslator(AnimationPacketData.class, new AnimationTranslator());
        betaToModernTranslatorRegistry.registerTranslator(KeepAlivePacketData.class, new KeepAliveTranslator());
        betaToModernTranslatorRegistry.registerTranslator(EntityEffectPacketData.class, new EntityEffectTranslator());
        betaToModernTranslatorRegistry.registerTranslator(RemoveEntityEffectPacketData.class, new RemoveEntityEffectTranslator());

        modernToBetaTranslatorRegistry.registerTranslator(ClientPlayerStatePacket.class, new ClientPlayerStateTranslator());
        modernToBetaTranslatorRegistry.registerTranslator(ClientKeepAlivePacket.class, new ClientKeepAliveTranslator());
        modernToBetaTranslatorRegistry.registerTranslator(ClientCreativeInventoryActionPacket.class, new ClientCreativeInventoryActionTranslator());

        //1.7.3
        betaToModernTranslatorRegistry.registerTranslator(KickDisconnectPacketData.class, new KickDisconnectTranslator());
        betaToModernTranslatorRegistry.registerTranslator(HandshakePacketData.class, new HandshakeTranslator());
        betaToModernTranslatorRegistry.registerTranslator(ChatPacketData.class, new ChatTranslator());
        betaToModernTranslatorRegistry.registerTranslator(SpawnPositionPacketData.class, new SpawnPositionTranslator());
        betaToModernTranslatorRegistry.registerTranslator(UpdateTimePacketData.class, new UpdateTimeTranslator());
        betaToModernTranslatorRegistry.registerTranslator(PlayerLookMovePacketData.class, new PlayerLookMoveTranslator());
        betaToModernTranslatorRegistry.registerTranslator(BlockChangePacketData.class, new BlockChangeTranslator());
        betaToModernTranslatorRegistry.registerTranslator(PreChunkPacketData.class, new PreChunkTranslator());
        betaToModernTranslatorRegistry.registerTranslator(MapChunkPacketData.class, new MapChunkTranslator());
        betaToModernTranslatorRegistry.registerTranslator(SetSlotPacketData.class, new SetSlotTranslator());
        betaToModernTranslatorRegistry.registerTranslator(MultiBlockChangePacketData.class, new MultiBlockChangeTranslator());
        betaToModernTranslatorRegistry.registerTranslator(BlockItemSwitchPacketData.class, new BlockItemSwitchTranslator());
        betaToModernTranslatorRegistry.registerTranslator(WindowItemsPacketData.class, new WindowItemsTranslator());
        betaToModernTranslatorRegistry.registerTranslator(MobSpawnPacketData.class, new MobSpawnTranslator());
        betaToModernTranslatorRegistry.registerTranslator(EntityPositionPacketData.class, new EntityPositionTranslator());
        betaToModernTranslatorRegistry.registerTranslator(EntityTeleportPacketData.class, new EntityTeleportTranslator());
        betaToModernTranslatorRegistry.registerTranslator(EntityMoveLookPacketData.class, new EntityMoveLookTranslator());
        betaToModernTranslatorRegistry.registerTranslator(EntityDestroyPacketData.class, new EntityDestroyTranslator());
        betaToModernTranslatorRegistry.registerTranslator(EntityStatusPacketData.class, new EntityStatusTranslator());
        betaToModernTranslatorRegistry.registerTranslator(ExplosionPacketData.class, new ExplosionTranslator());
        betaToModernTranslatorRegistry.registerTranslator(EntityVelocityPacketData.class, new EntityVelocityTranslator());
        betaToModernTranslatorRegistry.registerTranslator(VehicleSpawnPacketData.class, new VehicleSpawnTranslator());
        betaToModernTranslatorRegistry.registerTranslator(PickupSpawnPacketData.class, new PickupSpawnTranslator());
        betaToModernTranslatorRegistry.registerTranslator(EntityMetadataPacketData.class, new EntityMetadataTranslator());
        betaToModernTranslatorRegistry.registerTranslator(CollectPacketData.class, new CollectTranslator());
        betaToModernTranslatorRegistry.registerTranslator(OpenWindowPacketData.class, new OpenWindowTranslator());
        betaToModernTranslatorRegistry.registerTranslator(TransactionPacketData.class, new TransactionTranslator());
        betaToModernTranslatorRegistry.registerTranslator(PlayNoteblockPacketData.class, new PlayNoteblockTranslator());
        betaToModernTranslatorRegistry.registerTranslator(UpdateProgressPacketData.class, new UpdateProgressTranslator());
        betaToModernTranslatorRegistry.registerTranslator(UpdateSignPacketData.class, new UpdateSignTranslator());
        betaToModernTranslatorRegistry.registerTranslator(SleepPacketData.class, new SleepPacketTranslator());
        betaToModernTranslatorRegistry.registerTranslator(AttachEntityPacketData.class, new AttachEntityTranslator());
        betaToModernTranslatorRegistry.registerTranslator(EntityEquipmentPacketData.class, new EntityEquipmentTranslator());
        betaToModernTranslatorRegistry.registerTranslator(ThunderboltPacketData.class, new ThunderboltTranslator());
        betaToModernTranslatorRegistry.registerTranslator(CloseWindowPacketData.class, new CloseWindowTranslator());
        betaToModernTranslatorRegistry.registerTranslator(MapDataPacketData.class, new MapDataTranslator());
        betaToModernTranslatorRegistry.registerTranslator(PaintingPacketData.class, new PaintingTranslator());
        betaToModernTranslatorRegistry.registerTranslator(SoundEffectPacketData.class, new SoundEffectTranslator());
        betaToModernTranslatorRegistry.registerTranslator(NamedEntitySpawnPacketData.class, new NamedEntitySpawnTranslator());
        betaToModernTranslatorRegistry.registerTranslator(EntityLookPacketData.class, new EntityLookTranslator());
        betaToModernTranslatorRegistry.registerTranslator(StatisticsPacketData.class, new StatisticsTranslator());

        modernToBetaTranslatorRegistry.registerTranslator(LoginStartPacket.class, new LoginStartTranslator());
        modernToBetaTranslatorRegistry.registerTranslator(ClientChatPacket.class, new ClientChatTranslator());
        modernToBetaTranslatorRegistry.registerTranslator(ClientPlayerChangeHeldItemPacket.class, new ClientPlayerChangeHeldItemTranslator());
        modernToBetaTranslatorRegistry.registerTranslator(ClientPlayerPositionPacket.class, new ClientPlayerPositionTranslator());
        modernToBetaTranslatorRegistry.registerTranslator(ClientPlayerPositionRotationPacket.class, new ClientPlayerPositionRotationTranslator());
        modernToBetaTranslatorRegistry.registerTranslator(ClientRequestPacket.class, new ClientRequestTranslator());
        modernToBetaTranslatorRegistry.registerTranslator(ClientPlayerRotationPacket.class, new ClientPlayerRotationTranslator());
        modernToBetaTranslatorRegistry.registerTranslator(ClientPlayerSwingArmPacket.class, new ClientPlayerSwingArmTranslator());
        modernToBetaTranslatorRegistry.registerTranslator(ClientPlayerActionPacket.class, new ClientPlayerActionTranslator());
        modernToBetaTranslatorRegistry.registerTranslator(ClientPlayerPlaceBlockPacket.class, new ClientPlayerPlaceBlockTranslator());
        modernToBetaTranslatorRegistry.registerTranslator(ClientCloseWindowPacket.class, new ClientCloseWindowTranslator());
        modernToBetaTranslatorRegistry.registerTranslator(ClientPlayerInteractEntityPacket.class, new ClientPlayerInteractEntityTranslator());
        modernToBetaTranslatorRegistry.registerTranslator(ClientPlayerUseItemPacket.class, new ClientPlayerUseItemTranslator());
        modernToBetaTranslatorRegistry.registerTranslator(ClientTeleportConfirmPacket.class, new ClientTeleportConfirmTranslator());
        modernToBetaTranslatorRegistry.registerTranslator(ClientWindowActionPacket.class, new ClientWindowActionTranslator());
        modernToBetaTranslatorRegistry.registerTranslator(ClientConfirmTransactionPacket.class, new ClientConfirmTransactionTranslator());
        modernToBetaTranslatorRegistry.registerTranslator(ClientUpdateSignPacket.class, new ClientUpdateSignTranslator());
        modernToBetaTranslatorRegistry.registerTranslator(ClientSteerVehiclePacket.class, new ClientSteerVehicleTranslator());
    }
}