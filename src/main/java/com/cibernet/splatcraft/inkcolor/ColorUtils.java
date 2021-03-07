package com.cibernet.splatcraft.inkcolor;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.block.AbstractInkableBlock;
import com.cibernet.splatcraft.block.entity.AbstractInkableBlockEntity;
import com.cibernet.splatcraft.component.PlayerDataComponent;
import com.cibernet.splatcraft.entity.InkableEntity;
import com.cibernet.splatcraft.init.SplatcraftComponents;
import com.cibernet.splatcraft.particle.InkSplashParticleEffect;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Objects;
import java.util.Random;

public class ColorUtils {
    public static final int DEFAULT = 0x00FFFFFF;
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
        PlayerDataComponent data = SplatcraftComponents.PLAYER_DATA.get(player);
        if (data.getInkColor() != color) {
            data.setInkColor(color);
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
            return ColorUtils.setInkColor((PlayerEntity) entity, color);
        }

        return false;
    }

    public static InkColor getInkColor(ItemStack stack) {
        CompoundTag tag = stack.getItem() instanceof BlockItem ? stack.getSubTag("BlockEntityTag") : stack.getTag();
        if (tag == null) {
            return InkColors.NONE;
        } else {
            CompoundTag splatcraft = tag.getCompound(Splatcraft.MOD_ID);
            if (splatcraft == null) {
                return InkColors.NONE;
            } else {
                String inkColor = splatcraft.getString("InkColor");
                return inkColor == null ? InkColors.NONE : InkColor.getFromId(inkColor);
            }
        }
    }
    public static ItemStack setInkColor(ItemStack stack, InkColor color) {
        CompoundTag tag = stack.getItem() instanceof BlockItem ? stack.getOrCreateSubTag("BlockEntityTag") : stack.getOrCreateTag();
        CompoundTag splatcraft = new CompoundTag();

        splatcraft.putString("InkColor", color.toString());

        tag.put(Splatcraft.MOD_ID, splatcraft);
        return stack;
    }

    public static InkColor getInkColor(BlockEntity blockEntity) {
        if (blockEntity == null) {
            return InkColors.NONE;
        } else if (blockEntity instanceof AbstractInkableBlockEntity) {
            return ((AbstractInkableBlockEntity) blockEntity).getInkColor();
        }

        Block block = blockEntity.getCachedState().getBlock();
        if (block instanceof AbstractInkableBlock) {
            return ((AbstractInkableBlock) block).getColor(Objects.requireNonNull(blockEntity.getWorld()), blockEntity.getPos());
        }

        return InkColors.NONE;
    }
    public static boolean setInkColor(BlockEntity blockEntity, InkColor color) {
        if (blockEntity instanceof AbstractInkableBlockEntity) {
            if (((AbstractInkableBlockEntity) blockEntity).getInkColor() != color) {
                ((AbstractInkableBlockEntity) blockEntity).setInkColor(color);

                World world = blockEntity.getWorld();
                if (world != null) {
                    if (!world.isClient) {
                        ((BlockEntityClientSerializable) blockEntity).sync();
                    }

                    blockEntity.getWorld().addSyncedBlockEvent(blockEntity.getPos(), blockEntity.getCachedState().getBlock(), 0, 0);
                }

                return true;
            }
        }

        return false;
    }

    public static Text getFormattedColorName(InkColor color, boolean colorless) {
        return color == InkColors.NONE
            ? new TranslatableText( (colorless ? Formatting.GRAY : "") + color.getTranslationKey())
            : new TranslatableText(color.getTranslationKey()).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(color.getColor())));
    }

    public static void setColorLocked(ItemStack stack, boolean isLocked) {
        stack.getOrCreateSubTag(Splatcraft.MOD_ID).putBoolean("ColorLocked", isLocked);
    }
    public static boolean isColorLocked(ItemStack stack) {
        CompoundTag tag = stack.getItem() instanceof BlockItem ? stack.getSubTag("BlockEntityTag") : stack.getTag();
        if (tag == null) {
            return false;
        } else {
            CompoundTag splatcraft = tag.getCompound(Splatcraft.MOD_ID);
            if (splatcraft == null) {
                return false;
            } else {
                return splatcraft.getBoolean("ColorLocked");
            }
        }
    }

    public static boolean colorEquals(PlayerEntity player, ItemStack stack) {
        return ColorUtils.getInkColor(player) == ColorUtils.getInkColor(stack);
    }

    public static void addInkSplashParticle(World world, InkColor inkColor, Vec3d spawnPos) {
        int colorInt = inkColor.getColor();
        float r = ((colorInt & 16711680) >> 16) / 255.0f;
        float g = ((colorInt & '\uff00') >> 8) / 255.0f;
        float b = (colorInt & 255) / 255.0f;

        world.addParticle(new InkSplashParticleEffect(r, g, b), spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), 0.0D, 0.0D, 0.0D);
    }
    public static void addInkSplashParticle(World world, BlockPos sourcePos, Vec3d spawnPos) {
        BlockEntity blockEntity = world.getBlockEntity(sourcePos);
        if (blockEntity instanceof AbstractInkableBlockEntity) {
            ColorUtils.addInkSplashParticle(world, ((AbstractInkableBlockEntity) blockEntity).getInkColor(), spawnPos);
        }
    }

    public static InkColor getRandomStarterColor(Random random) {
        return STARTER_COLORS[random.nextInt(STARTER_COLORS.length)];
    }
}
