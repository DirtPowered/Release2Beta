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

package com.github.dirtpowered.releasetobeta.data.player;

import com.github.dirtpowered.betaprotocollib.data.BetaItemStack;
import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.WindowClickPacketData;
import com.github.dirtpowered.releasetobeta.configuration.R2BConfiguration;
import com.github.dirtpowered.releasetobeta.data.entity.model.Entity;
import com.github.dirtpowered.releasetobeta.data.entity.model.Mob;
import com.github.dirtpowered.releasetobeta.data.inventory.PlayerInventory;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.utils.Utils;
import com.github.dirtpowered.releasetobeta.utils.chat.ChatUtils;
import com.github.dirtpowered.releasetobeta.utils.interfaces.Callback;
import com.github.steveice10.mc.auth.data.GameProfile;
import com.github.steveice10.mc.protocol.data.game.MessageType;
import com.github.steveice10.mc.protocol.data.game.PlayerListEntry;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.ItemStack;
import com.github.steveice10.mc.protocol.data.game.entity.player.GameMode;
import com.github.steveice10.mc.protocol.data.game.window.WindowType;
import com.github.steveice10.mc.protocol.data.game.world.sound.BuiltinSound;
import com.github.steveice10.mc.protocol.data.game.world.sound.SoundCategory;
import com.github.steveice10.mc.protocol.data.message.Message;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerResourcePackSendPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.window.ServerCloseWindowPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.window.ServerSetSlotPacket;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.packet.Packet;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ModernPlayer extends Entity implements Mob {
    private String username;
    private BetaClientSession session;
    private GameProfile gameProfile;
    private PlayerInventory inventory;
    private WindowType openedInventoryType;
    private long lastInteractAtEntity;
    private UUID clientId;
    private boolean onGround;
    private int dimension;
    private boolean sneaking;
    private boolean inVehicle;
    private int vehicleEntityId;
    private int difficulty;
    private int gamemode;
    private int worldHeight;
    private long seed;
    private boolean sprinting;
    private float health;
    private long lastLocationUpdate;
    private PlayerEvent playerEvent;
    private Session modernSession;
    private long lastChatInteraction;

    public ModernPlayer(BetaClientSession betaSession, Session modernSession, UUID clientId) {
        super(0); //will be changed later

        this.session = betaSession;
        this.modernSession = modernSession;

        this.clientId = clientId;

        this.inventory = new PlayerInventory();
        this.openedInventoryType = WindowType.GENERIC_INVENTORY;

        this.playerEvent = new PlayerEvent(this);
    }

    public PlayerListEntry getTabEntry() {
        return new PlayerListEntry(getGameProfile(), GameMode.SURVIVAL, getPing(), Message.fromString(username));
    }

    public void fillProfile(String username, Callback<Boolean> callback) {
        this.username = username;

        if (R2BConfiguration.skinFix) {
            session.getMain().getServer().getProfileCache().getSkin(username).whenComplete((profile, throwable) -> {
                this.gameProfile = profile;
                callback.onComplete(true);
            });
        } else {
            this.gameProfile = new GameProfile(Utils.getOfflineUUID(username), username);
            callback.onComplete(true);
        }
    }

    public void sendPacket(Packet modernPacket) {
        modernSession.send(modernPacket);
    }

    public void sendResourcePack() {
        sendPacket(new ServerResourcePackSendPacket(
                R2BConfiguration.resourcePack, R2BConfiguration.resourcePackHash.toLowerCase()
        ));
    }

    public void updateInventory() {
        sendPacket(new ServerSetSlotPacket(0, 45, new ItemStack(0)));

        // force server to send inventory update packet
        session.sendPacket(new WindowClickPacketData(0, 0, 0, (short) 0, new BetaItemStack(1), false));
        session.sendPacket(new WindowClickPacketData(0, 1, 1, (short) 0, new BetaItemStack(2), false));
    }

    public void sendMessage(String message) {
        sendPacket(new ServerChatPacket(ChatUtils.toModernMessage(message, false)));
    }

    public void sendRawMessage(Message message, MessageType messageType) {
        sendPacket(new ServerChatPacket(message, messageType));
    }

    public int getPing() {
        return modernSession.getFlag("ping");
    }

    @Override
    public void onDeath(Session session) {
        playSound(session, BuiltinSound.ENTITY_PLAYER_DEATH, SoundCategory.PLAYER);
    }

    @Override
    public void onDamage(Session session) {
        playSound(session, BuiltinSound.ENTITY_PLAYER_HURT, SoundCategory.PLAYER);
    }

    @Override
    public void onUpdate(Session session) {
        // do nothing
    }
}
