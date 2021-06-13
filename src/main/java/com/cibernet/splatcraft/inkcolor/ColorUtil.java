package com.cibernet.splatcraft.inkcolor;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.block.AbstractInkableBlock;
import com.cibernet.splatcraft.block.entity.AbstractInkableBlockEntity;
import com.cibernet.splatcraft.component.PlayerDataComponent;
import com.cibernet.splatcraft.entity.InkableEntity;
import com.cibernet.splatcraft.network.SplatcraftNetworkingConstants;
import com.cibernet.splatcraft.util.TagUtil;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;
import java.util.Random;

public class ColorUtil {
    public static final int DEFAULT = 0xFFFFFF;
    public static final int DEFAULT_COLOR_LOCK_FRIENDLY = 0xDEA801;
    public static final int DEFAULT_COLOR_LOCK_HOSTILE = 0x4717A9;

    public static final InkColor[] STARTER_COLORS = { InkColors.ORANGE, InkColors.BLUE, InkColors.GREEN, InkColors.PINK };

    public static InkColor getEntityColor(Entity entity) {
        if (entity instanceof PlayerEntity) {
            return getInkColor((PlayerEntity) entity);
        } else if (entity instanceof InkableEntity) {
            return ((InkableEntity) entity).getInkColor();
        } else {
            return InkColors.NONE;
        }
    }

    public static InkColor getInkColor(PlayerEntity player) {
        try {
            return PlayerDataComponent.getInkColor(player);
        } catch (NullPointerException e) {
            return InkColors.NONE;
        }
    }
    public static boolean setInkColor(PlayerEntity player, InkColor color) {
        PlayerDataComponent data = PlayerDataComponent.getComponent(player);
        if (!data.getInkColor().equals(color)) {
            data.setInkColor(color);

            if (!player.world.isClient) {
                ServerPlayNetworking.send((ServerPlayerEntity) player, SplatcraftNetworkingConstants.SYNC_INK_COLOR_CHANGE_FOR_COLOR_LOCK_PACKET_ID, PacketByteBufs.empty());
            }

            return true;
        } else {
            return false;
        }
    }

    public static InkColor getInkColor(Entity entity) {
        if (entity instanceof InkableEntity) {
            return ((InkableEntity) entity).getInkColor();
        } else {
            return InkColors.NONE;
        }
    }
    public static boolean setInkColor(Entity entity, InkColor color) {
        if (entity instanceof InkableEntity) {
            return ((InkableEntity) entity).setInkColor(color);
        } else if (entity instanceof PlayerEntity) {
            return ColorUtil.setInkColor((PlayerEntity) entity, color);
        }

        return false;
    }

    public static InkColor getInkColor(ItemStack stack) {
        NbtCompound tag = TagUtil.getBlockEntityTagOrRoot(stack);
        if (tag == null) {
            return InkColors.NONE;
        } else {
            NbtCompound splatcraft = tag.getCompound(Splatcraft.MOD_ID);
            if (splatcraft == null) {
                return InkColors.NONE;
            } else {
                String inkColor = splatcraft.getString("InkColor");
                return inkColor == null ? InkColors.NONE : InkColor.fromNonNull(inkColor);
            }
        }
    }
    public static ItemStack setInkColor(ItemStack stack, InkColor color, boolean setColorLocked) {
        NbtCompound tag = TagUtil.getBlockEntityTagOrRoot(stack);
        NbtCompound splatcraft = TagUtil.getOrCreateSplatcraftTag(tag);
        splatcraft.putString("InkColor", color.toString());
        tag.put(Splatcraft.MOD_ID, splatcraft);

        if (setColorLocked) {
            ColorUtil.setColorLocked(stack, true);
        }

        return stack;
    }
    public static ItemStack setInkColor(ItemStack stack, InkColor color) {
        return ColorUtil.setInkColor(stack, color, false);
    }

    public static InkColor getInkColor(BlockEntity blockEntity) {
        if (blockEntity == null || blockEntity.getWorld() == null) {
            return InkColors.NONE;
        } else if (blockEntity instanceof AbstractInkableBlockEntity) {
            return ((AbstractInkableBlockEntity) blockEntity).getInkColor();
        }

        Block block = blockEntity.getCachedState().getBlock();
        if (block instanceof AbstractInkableBlock) {
            return ((AbstractInkableBlock) block).getInkColor(blockEntity.getWorld(), blockEntity.getPos());
        }

        return InkColors.NONE;
    }
    public static boolean setInkColor(BlockEntity blockEntity, InkColor color) {
        if (blockEntity instanceof AbstractInkableBlockEntity) {
            return ((AbstractInkableBlockEntity) blockEntity).setInkColor(color);
        }

        return false;
    }

    public static float[] getRgbFromDecimal(int color) {
        float r = ((color & 16711680) >> 16) / 255.0f;
        float g = ((color & '\uff00') >> 8) / 255.0f;
        float b = (color & 255) / 255.0f;

        return new float[]{ r, g, b };
    }
    public static int getDecimalFromRgb(float[] colorComponents) {
        return new Color(colorComponents[0], colorComponents[1], colorComponents[2]).getRGB();
    }

    public static Text getFormattedColorName(InkColor inkColor, boolean colorless) {
        TranslatableText text = new TranslatableText(inkColor.getTranslationKey());
        if (!colorless) {
            text.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(inkColor.color)));
        }

        return text;
    }
    public static Text getFormattedColorName(InkColor inkColor) {
        return getFormattedColorName(inkColor, false);
    }
    public static TranslatableText getTranslatableTextWithColor(String key, InkColor color, boolean colorless) {
        return new TranslatableText(key, getFormattedColorName(color, colorless));
    }
    public static TranslatableText getTranslatableTextWithColor(ItemStack stack, boolean colorless) {
        return getTranslatableTextWithColor(((TranslatableText)stack.getItem().getName()).getKey(), ColorUtil.getInkColor(stack), colorless);
    }

    public static void appendTooltip(ItemStack stack, List<Text> tooltip) {
        InkColor inkColor = ColorUtil.getInkColor(stack);
        if (!inkColor.equals(InkColors.NONE) || ColorUtil.isColorLocked(stack)) {
            tooltip.add(ColorUtil.getFormattedColorName(ColorUtil.getInkColor(stack), false));
        } else {
            tooltip.add(new TranslatableText("item." + Splatcraft.MOD_ID + ".tooltip.colorless").formatted(Formatting.GRAY));
        }
    }

    public static ItemStack setColorLocked(ItemStack stack, boolean isLocked) {
        NbtCompound tag = TagUtil.getBlockEntityTagOrRoot(stack);
        NbtCompound splatcraft = TagUtil.getOrCreateSplatcraftTag(tag);
        splatcraft.putBoolean("ColorLocked", isLocked);
        tag.put(Splatcraft.MOD_ID, splatcraft);

        return stack;
    }
    public static boolean isColorLocked(ItemStack stack) {
        NbtCompound tag = TagUtil.getBlockEntityTagOrRoot(stack);
        NbtCompound splatcraft = TagUtil.getOrCreateSplatcraftTag(tag);
        return splatcraft.getBoolean("ColorLocked");
    }

    public static boolean colorEquals(PlayerEntity player, ItemStack stack) {
        return ColorUtil.getInkColor(player).matches(ColorUtil.getInkColor(stack).color);
    }

    public static void addInkSplashParticle(World world, InkColor inkColor, Vec3d pos, Vec3d velocity, float scale, @Nullable PlayerEntity player) {
        if (!world.isClient) {
            PacketByteBuf buf = PacketByteBufs.create();

            buf.writeIdentifier(inkColor.id);
            buf.writeFloat(scale);

            // write spawn pos
            buf.writeDouble(pos.getX());
            buf.writeDouble(pos.getY());
            buf.writeDouble(pos.getZ());

            // write velocity
            buf.writeDouble(velocity.getX());
            buf.writeDouble(velocity.getY());
            buf.writeDouble(velocity.getZ());

            for (ServerPlayerEntity serverPlayer : PlayerLookup.tracking((ServerWorld) world, new BlockPos(pos))) {
                if (!serverPlayer.equals(player)) {
                    ServerPlayNetworking.send(serverPlayer, SplatcraftNetworkingConstants.PLAY_BLOCK_INKING_EFFECTS_PACKET_ID, buf);
                }
            }
        } else if (player != null) {
            com.cibernet.splatcraft.client.network.SplatcraftClientNetworking.playBlockInkingEffects(world, inkColor, scale, pos, velocity); // static import to prevent ClassNotFoundException
        }
    }
    public static void addInkSplashParticle(World world, InkColor inkColor, Vec3d pos, float scale, @Nullable PlayerEntity player) {
        addInkSplashParticle(world, inkColor, pos, Vec3d.ZERO, scale, player);
    }
    public static void addInkSplashParticle(World world, InkColor inkColor, Vec3d pos, float scale) {
        addInkSplashParticle(world, inkColor, pos, scale, null);
    }
    public static void addInkSplashParticle(World world, InkColor inkColor, Vec3d pos) {
        ColorUtil.addInkSplashParticle(world, inkColor, pos, 1.0f);
    }
    public static void addInkSplashParticle(World world, BlockPos sourcePos, Vec3d spawnPos, float scale) {
        BlockEntity blockEntity = world.getBlockEntity(sourcePos);
        if (blockEntity instanceof AbstractInkableBlockEntity) {
            ColorUtil.addInkSplashParticle(world, ((AbstractInkableBlockEntity) blockEntity).getInkColor(), spawnPos, scale);
        }
    }
    public static void addInkSplashParticle(World world, BlockPos sourcePos, Vec3d spawnPos) {
        ColorUtil.addInkSplashParticle(world, sourcePos, spawnPos, 1.0f);
    }

    public static InkColor getRandomStarterColor(Random random) {
        return STARTER_COLORS[random.nextInt(STARTER_COLORS.length)];
    }

}
