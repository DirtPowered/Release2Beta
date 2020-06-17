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

package com.github.dirtpowered.releasetobeta.network.server;

import com.github.dirtpowered.releasetobeta.ReleaseToBeta;
import com.github.dirtpowered.releasetobeta.data.command.CommandRegistry;
import com.github.dirtpowered.releasetobeta.data.command.R2BCommand;
import com.github.dirtpowered.releasetobeta.data.command.model.Command;
import com.github.dirtpowered.releasetobeta.data.entity.EntityRegistry;
import com.github.dirtpowered.releasetobeta.data.entity.MetadataTranslator;
import com.github.dirtpowered.releasetobeta.data.item.ArmorItem;
import com.github.dirtpowered.releasetobeta.data.location.MovementTranslator;
import com.github.dirtpowered.releasetobeta.data.player.ModernPlayer;
import com.github.dirtpowered.releasetobeta.data.skin.ProfileCache;
import com.github.steveice10.mc.protocol.data.game.entity.attribute.Attribute;
import com.github.steveice10.mc.protocol.data.game.entity.attribute.AttributeType;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.ItemStack;
import com.github.steveice10.mc.protocol.data.game.world.sound.BuiltinSound;
import com.github.steveice10.mc.protocol.data.game.world.sound.SoundCategory;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityPropertiesPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerAbilitiesPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerPlayBuiltinSoundPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerWorldBorderPacket;
import com.github.steveice10.packetlib.Session;
import lombok.Getter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Getter
public class ModernServer {
    private ReleaseToBeta main;
    private ServerConnection serverConnection;
    private EntityRegistry entityRegistry;
    private ProfileCache profileCache;
    private CommandRegistry commandRegistry;
    private String[] commands;
    private BufferedImage serverIcon;

    private MetadataTranslator metadataTranslator;
    private MovementTranslator movementTranslator;

    public ModernServer(ReleaseToBeta main) {
        this.main = main;

        this.serverConnection = new ServerConnection(this);
        this.entityRegistry = new EntityRegistry();
        this.profileCache = new ProfileCache(main);
        this.commandRegistry = new CommandRegistry();

        registerInternalCommands();
        this.commands = commandRegistry.getCommands().keySet().toArray(new String[0]);

        this.metadataTranslator = new MetadataTranslator();
        this.movementTranslator = new MovementTranslator();

        try {
            this.serverIcon = ImageIO.read(new File("server-icon.png"));
        } catch (IOException e) {
            main.getLogger().warning("unable to read server-icon.png (missing?)");
        }
    }

    private void registerInternalCommands() {
        commandRegistry.register("releasetobeta", new R2BCommand());
    }

    public boolean executeCommand(ModernPlayer player, String message) {
        message = message.substring(1);
        String[] args = message.trim().split("\\s+");

        Command command;
        if (commandRegistry.getCommands().containsKey(args[0])) {
            command = commandRegistry.getCommands().get(args[0]);
            command.execute(player, args);
            return true;
        }

        return false;
    }


    public void playWorldSound(Session session, int x, int y, int z, BuiltinSound sound, SoundCategory category) {
        session.send(new ServerPlayBuiltinSoundPacket(sound, category, x, y, z, 1.f, 1.f));
    }

    public void updatePlayerProperties(Session session, ModernPlayer player) {
        List<Attribute> attributes = new ArrayList<>();
        double aVal = 0;

        ItemStack[] items = player.getInventory().getArmorItems();

        for (ItemStack item : items) {
            if (item != null)
                aVal += ArmorItem.getArmorValueFromItem(item);
        }

        attributes.add(new Attribute(AttributeType.GENERIC_ARMOR, aVal));
        attributes.add(new Attribute(AttributeType.GENERIC_ATTACK_SPEED, 32.0D)); //disables 1.9+ pvp delay

        session.send(new ServerEntityPropertiesPacket(player.getEntityId(), attributes));
    }

    public void sendInitialPlayerAbilities(ModernPlayer player) {
        boolean creative = player.getGamemode() == 1;
        player.sendPacket(new ServerPlayerAbilitiesPacket(false, creative, creative, creative, 0.05f, 0.1f));
    }

    public void sendWorldBorder(Session session) {
        //default border size
        session.send(new ServerWorldBorderPacket(0.0D, 0.0D, 6.0E07D, 6.0E07D, 0L, 29999984, 5, 15));
    }

    public BufferedImage getServerIcon() {
        return serverIcon;
    }

    public ModernPlayer getPlayer(int entityId) {
        ModernPlayer m = null;
        for (ModernPlayer player : serverConnection.getPlayerList().getPlayers()) {
            if (player.getEntityId() == entityId) {
                m = player;
            }
        }

        return m;
    }
}
