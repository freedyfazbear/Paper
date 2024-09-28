package net.minecraft.world.level.block.entity;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.ICommandListener;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.ChatClickable;
import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.network.chat.ChatComponentUtils;
import net.minecraft.network.chat.ChatModifier;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.chat.IChatMutableComponent;
import net.minecraft.network.protocol.game.PacketPlayOutTileEntityData;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.FormattedString;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.EnumColor;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.Vec2F;
import net.minecraft.world.phys.Vec3D;

public class TileEntitySign extends TileEntity implements ICommandListener { // CraftBukkit - implements

    public final IChatBaseComponent[] lines;
    public boolean isEditable;
    private EntityHuman c;
    private final FormattedString[] g;
    private EnumColor color;
    public java.util.UUID signEditor; // Paper
    private static final boolean CONVERT_LEGACY_SIGNS = Boolean.getBoolean("convertLegacySigns"); // Paper
    private static final org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger(); // Paper

    public TileEntitySign() {
        super(TileEntityTypes.SIGN);
        this.lines = new IChatBaseComponent[]{ChatComponentText.d, ChatComponentText.d, ChatComponentText.d, ChatComponentText.d};
        this.isEditable = true;
        this.g = new FormattedString[4];
        this.color = EnumColor.BLACK;
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbttagcompound) {
        super.save(nbttagcompound);

        for (int i = 0; i < 4; ++i) {
            String s = IChatBaseComponent.ChatSerializer.a(this.lines[i]);

            nbttagcompound.setString("Text" + (i + 1), s);
        }

        // CraftBukkit start
        if (CONVERT_LEGACY_SIGNS) { // Paper
            nbttagcompound.setBoolean("Bukkit.isConverted", true);
        }
        // CraftBukkit end

        nbttagcompound.setString("Color", this.color.c());
        return nbttagcompound;
    }

    @Override
    public void load(IBlockData iblockdata, NBTTagCompound nbttagcompound) {
        this.isEditable = false;
        super.load(iblockdata, nbttagcompound);
        this.color = EnumColor.a(nbttagcompound.getString("Color"), EnumColor.BLACK);

        // CraftBukkit start - Add an option to convert signs correctly
        // This is done with a flag instead of all the time because
        // we have no way to tell whether a sign is from 1.7.10 or 1.8

        boolean oldSign = Boolean.getBoolean("convertLegacySigns") && !nbttagcompound.getBoolean("Bukkit.isConverted");

        for (int i = 0; i < 4; ++i) {
            String s = nbttagcompound.getString("Text" + (i + 1));
            if (s != null && s.length() > 2048) {
                s = "\"\"";
            }

            try {
                //IChatMutableComponent ichatmutablecomponent = IChatBaseComponent.ChatSerializer.a(s.isEmpty() ? "\"\"" : s); // Paper - move down - the old format might throw a json error

                if (oldSign && !isLoadingStructure) { // Paper - saved structures will be in the new format, but will not have isConverted
                    lines[i] = org.bukkit.craftbukkit.util.CraftChatMessage.fromString(s)[0];
                    continue;
                }
                // CraftBukkit end
                IChatMutableComponent ichatmutablecomponent = IChatBaseComponent.ChatSerializer.a(s.isEmpty() ? "\"\"" : s); // Paper - after old sign

                if (this.world instanceof WorldServer) {
                    try {
                        this.lines[i] = ChatComponentUtils.filterForDisplay(this.a((EntityPlayer) null), ichatmutablecomponent, (Entity) null, 0);
                    } catch (CommandSyntaxException commandsyntaxexception) {
                        this.lines[i] = ichatmutablecomponent;
                    }
                } else {
                    this.lines[i] = ichatmutablecomponent;
                }
            } catch (com.google.gson.JsonParseException jsonparseexception) {
                this.lines[i] = new ChatComponentText(s);
            }

            this.g[i] = null;
        }

    }

    public void a(int i, IChatBaseComponent ichatbasecomponent) {
        this.lines[i] = ichatbasecomponent;
        this.g[i] = null;
    }

    @Nullable
    @Override
    public PacketPlayOutTileEntityData getUpdatePacket() {
        return new PacketPlayOutTileEntityData(this.position, 9, this.b());
    }

    @Override
    public NBTTagCompound b() {
        return this.save(new NBTTagCompound());
    }

    @Override
    public boolean isFilteredNBT() {
        return true;
    }

    public boolean d() {
        return this.isEditable;
    }

    public void a(EntityHuman entityhuman) {
        // Paper start
        //this.c = entityhuman;
        signEditor = entityhuman != null ? entityhuman.getUniqueID() : null;
        // Paper end
    }

    public EntityHuman f() {
        return this.c;
    }

    public boolean b(EntityHuman entityhuman) {
        IChatBaseComponent[] aichatbasecomponent = this.lines;
        int i = aichatbasecomponent.length;

        for (int j = 0; j < i; ++j) {
            IChatBaseComponent ichatbasecomponent = aichatbasecomponent[j];
            ChatModifier chatmodifier = ichatbasecomponent == null ? null : ichatbasecomponent.getChatModifier();

            if (chatmodifier != null && chatmodifier.getClickEvent() != null) {
                ChatClickable chatclickable = chatmodifier.getClickEvent();

                if (chatclickable.a() == ChatClickable.EnumClickAction.RUN_COMMAND) {
                    // Paper start
                    EntityPlayer player = (EntityPlayer) entityhuman;
                    String command = chatclickable.b().startsWith("/") ? chatclickable.b() : "/" + chatclickable.b();
                    if (org.spigotmc.SpigotConfig.logCommands)  {
                        LOGGER.info("{} issued server command: {}", entityhuman.getName(), command);
                    }
                    io.papermc.paper.event.player.PlayerSignCommandPreprocessEvent event = new io.papermc.paper.event.player.PlayerSignCommandPreprocessEvent(player.getBukkitEntity(), command, new org.bukkit.craftbukkit.util.LazyPlayerSet(player.server), (org.bukkit.block.Sign) net.minecraft.server.MCUtil.toBukkitBlock(this.world, this.position).getState());
                    if (!event.callEvent()) {
                        return false;
                    }
                    player.server.getCommandDispatcher().performCommand(this.createCommandSourceStack(((org.bukkit.craftbukkit.entity.CraftPlayer) event.getPlayer()).getHandle()), event.getMessage());
                    // Paper end
                }
            }
        }

        return true;
    }

    // CraftBukkit start
    @Override
    public void sendMessage(IChatBaseComponent ichatbasecomponent, java.util.UUID uuid) {}

    @Override
    public org.bukkit.command.CommandSender getBukkitSender(CommandListenerWrapper wrapper) {
        return wrapper.getEntity() != null ? wrapper.getEntity().getBukkitSender(wrapper) : new org.bukkit.craftbukkit.command.CraftBlockCommandSender(wrapper, this);
    }

    @Override
    public boolean shouldSendSuccess() {
        return false;
    }

    @Override
    public boolean shouldSendFailure() {
        return false;
    }

    @Override
    public boolean shouldBroadcastCommands() {
        return false;
    }
    // CraftBukkit end

    public CommandListenerWrapper createCommandSourceStack(@Nullable EntityPlayer entityplayer) { return this.a(entityplayer); } // Paper - OBFHELPER
    public CommandListenerWrapper a(@Nullable EntityPlayer entityplayer) {
        String s = entityplayer == null ? "Sign" : entityplayer.getDisplayName().getString();
        Object object = entityplayer == null ? new ChatComponentText("Sign") : entityplayer.getScoreboardDisplayName();

        // Paper start - send messages back to the player
        ICommandListener commandSource = this.world.paperConfig.showSignClickCommandFailureMessagesToPlayer ? new io.papermc.paper.commands.DelegatingCommandSource(this) {
            @Override
            public void sendMessage(net.minecraft.network.chat.IChatBaseComponent message, java.util.UUID sender) {
                entityplayer.sendMessage(message, sender);
            }

            @Override
            public boolean shouldSendFailure() {
                return true;
            }
        } : this;
        // Paper end
        // CraftBukkit - this
        return new CommandListenerWrapper(commandSource, Vec3D.a((BaseBlockPosition) this.position), Vec2F.a, (WorldServer) this.world, 2, s, (IChatBaseComponent) object, this.world.getMinecraftServer(), entityplayer); // Paper
    }

    public EnumColor getColor() {
        return this.color;
    }

    public boolean setColor(EnumColor enumcolor) {
        if (enumcolor != this.getColor()) {
            this.color = enumcolor;
            this.update();
            if (this.world != null) this.world.notify(this.getPosition(), this.getBlock(), this.getBlock(), 3); // CraftBukkit - skip notify if world is null (SPIGOT-5122)
            return true;
        } else {
            return false;
        }
    }
}
