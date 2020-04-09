package com.github.dirtpowered.releasetobeta.data.player;

import com.github.dirtpowered.betaprotocollib.utils.Location;
import com.github.dirtpowered.releasetobeta.configuration.R2BConfiguration;
import com.github.dirtpowered.releasetobeta.data.entity.model.PlayerAction;
import com.github.dirtpowered.releasetobeta.data.inventory.PlayerInventory;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.utils.Callback;
import com.github.dirtpowered.releasetobeta.utils.Utils;
import com.github.steveice10.mc.auth.data.GameProfile;
import com.github.steveice10.mc.protocol.MinecraftConstants;
import com.github.steveice10.mc.protocol.data.game.PlayerListEntry;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.ItemStack;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.Position;
import com.github.steveice10.mc.protocol.data.game.entity.player.GameMode;
import com.github.steveice10.mc.protocol.data.game.window.WindowType;
import com.github.steveice10.mc.protocol.data.message.Message;
import com.github.steveice10.mc.protocol.data.message.TextMessage;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerDisconnectPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerResourcePackSendPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.window.ServerCloseWindowPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.window.ServerWindowItemsPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerOpenTileEntityEditorPacket;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.packet.Packet;

public class ModernPlayer implements PlayerAction {
    private String username;
    private int entityId;
    private BetaClientSession session;
    private String clientId;
    private GameProfile gameProfile;
    private int dimension;
    private boolean sneaking;
    private PlayerInventory inventory;
    private Location location;
    private boolean inVehicle;
    private int difficulty;
    private int gamemode;
    private int worldHeight;
    private long seed;
    private WindowType openedInventoryType;

    public ModernPlayer(BetaClientSession session) {
        this.session = session;

        this.inventory = new PlayerInventory();
        this.openedInventoryType = WindowType.GENERIC_INVENTORY;
    }

    public long getSeed() {
        return seed;
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }

    public int getWorldHeight() {
        return worldHeight;
    }

    public void setWorldHeight(int worldHeight) {
        this.worldHeight = worldHeight;
    }

    public int getGamemode() {
        return gamemode;
    }

    public void setGamemode(int gamemode) {
        this.gamemode = gamemode;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public int getDimension() {
        return dimension;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    public GameProfile getGameProfile() {
        return gameProfile;
    }

    public PlayerListEntry getTabEntry() {
        return new PlayerListEntry(getGameProfile(), GameMode.SURVIVAL, getPing(), Message.fromString(username));
    }

    public BetaClientSession getSession() {
        return session;
    }

    public String getUsername() {
        return username;
    }

    public void fillProfile(String username, Callback<ModernPlayer> result) {
        this.username = username;

        if (R2BConfiguration.skinFix) {
            session.getMain().getServer().getProfileCache().getSkin(username).whenComplete((profile, throwable) -> {
                this.gameProfile = profile;
                result.onComplete(this);
            });
        } else {
            this.gameProfile = new GameProfile(Utils.getOfflineUUID(username), username);
            result.onComplete(this);
        }
    }

    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void sendPacket(Packet modernPacket) {
        getModernSession().send(modernPacket);
    }

    private Session getModernSession() {
        return session.getMain().getSessionRegistry().getSession(clientId).getModernSession();
    }

    public void sendResourcePack() {
        sendPacket(new ServerResourcePackSendPacket(
                R2BConfiguration.resourcePack, R2BConfiguration.resourcePackHash.toLowerCase()
        ));
    }

    public void kick(String reason) {
        sendPacket(new ServerDisconnectPacket(reason));
    }

    public boolean isSneaking() {
        return sneaking;
    }

    public void setSneaking(boolean sneaking) {
        this.sneaking = sneaking;
    }

    public PlayerInventory getInventory() {
        return inventory;
    }

    public void updateInventory() {
        sendPacket(new ServerWindowItemsPacket(0, inventory.getItems()));
    }

    public void closeInventory() {
        sendPacket(new ServerCloseWindowPacket(0));
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public void onBlockPlace(Position pos, ItemStack itemstack) {
        int itemId = itemstack.getId();

        if (itemId == 323) {
            sendPacket(new ServerOpenTileEntityEditorPacket(pos));
        }
    }

    @Override
    public void onInventoryClose() {
        this.openedInventoryType = WindowType.GENERIC_INVENTORY;
    }

    @Override
    public void onInventoryOpen(WindowType windowType) {
        this.openedInventoryType = windowType;
    }

    public WindowType getOpenedInventoryType() {
        return openedInventoryType;
    }

    public boolean isInVehicle() {
        return inVehicle;
    }

    public void setInVehicle(boolean inVehicle) {
        this.inVehicle = inVehicle;
    }

    public void sendMessage(String message) {
        sendPacket(new ServerChatPacket(TextMessage.fromString(message)));
    }

    private int getPing() {
        return getModernSession().getFlag(MinecraftConstants.PING_KEY);
    }
}
