package com.cibernet.splatcraft.inkcolor;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.block.AbstractColorableBlock;
import com.cibernet.splatcraft.block.entity.AbstractColorableBlockEntity;
import com.cibernet.splatcraft.component.PlayerDataComponent;
import com.cibernet.splatcraft.entity.ColorableEntity;
import com.cibernet.splatcraft.init.SplatcraftComponents;
import com.cibernet.splatcraft.init.SplatcraftGameRules;
import com.cibernet.splatcraft.tag.SplatcraftInkColorTags;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
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
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;

import java.util.Objects;
import java.util.Random;

public class ColorUtils {
    public static final int DEFAULT = 0x00FFFFFF;

    public static final InkColor[] STARTER_COLORS = { InkColors.ORANGE, InkColors.BLUE, InkColors.GREEN, InkColors.PINK };

    public static InkColor getEntityColor(LivingEntity entity) {
        if (entity instanceof PlayerEntity) {
            return getPlayerColor((PlayerEntity) entity);
        } else if (entity instanceof ColorableEntity) {
            return ((ColorableEntity) entity).getColor();
        } else {
            return InkColors.NONE;
        }
    }

    public static InkColor getPlayerColor(PlayerEntity player) {
        try {
            PlayerDataComponent data = SplatcraftComponents.PLAYER_DATA.get(player);
            return data.getInkColor();
        } catch (NullPointerException e) {
            return InkColors.NONE;
        }
    }
    public static boolean setPlayerColor(PlayerEntity player, InkColor color) {
        PlayerDataComponent data = SplatcraftComponents.PLAYER_DATA.get(player);
        if (data.getInkColor() != color) {
            data.setInkColor(color);
            return true;
        } else {
            return false;
        }
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
        } else if (blockEntity instanceof AbstractColorableBlockEntity) {
            return ((AbstractColorableBlockEntity) blockEntity).getInkColor();
        }

        Block block = blockEntity.getCachedState().getBlock();
        if (block instanceof AbstractColorableBlock) {
            return ((AbstractColorableBlock) block).getColor(Objects.requireNonNull(blockEntity.getWorld()), blockEntity.getPos());
        }

        return InkColors.NONE;
    }

    public static void setInkColor(BlockEntity blockEntity, InkColor color) {
        BlockPos pos = blockEntity.getPos();
        World world = blockEntity.getWorld();

        if (blockEntity instanceof AbstractColorableBlockEntity) {
            ((AbstractColorableBlockEntity) blockEntity).setInkColor(color);
            return;
        }

        Block block = blockEntity.getCachedState().getBlock();
        if (block instanceof AbstractColorableBlock) {
            ((AbstractColorableBlock) block).setColor(world, pos, color);
        }
    }

    public static Text getFormattedColorName(InkColor color, boolean colorless) {
        return color == InkColors.NONE
            ? new TranslatableText( (colorless ? Formatting.GRAY : "") + color.getTranslationKey())
            : new TranslatableText(color.getTranslationKey()).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(color.getColor())));
    }

    public static boolean colorEquals(LivingEntity entity, BlockEntity blockEntity) {
        InkColor entityColor = ColorUtils.getEntityColor(entity);
        InkColor inkColor = ColorUtils.getInkColor(blockEntity);

        if (entityColor == InkColors.NONE || inkColor == InkColors.NONE) {
            return false;
        }
        return SplatcraftGameRules.getBoolean(entity.world, SplatcraftGameRules.UNIVERSAL_INK) || entityColor == inkColor;
    }

    public static boolean colorEquals(LivingEntity entity, ItemStack itemStack) {
        InkColor entityColor = getEntityColor(entity);
        InkColor inkColor = getInkColor(itemStack);

        if (entityColor == InkColors.NONE || inkColor == InkColors.NONE) {
            return false;
        }
        return SplatcraftGameRules.getBoolean(entity.world, SplatcraftGameRules.UNIVERSAL_INK) || entityColor == inkColor;
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

    public static InkColor getRandomStarterColor(Random random) {
        try {
            return SplatcraftInkColorTags.STARTER_COLORS.values().isEmpty()
                ? InkColors.NONE
                : SplatcraftInkColorTags.STARTER_COLORS.getRandom(random);
        } catch (ExceptionInInitializerError e) {
            Splatcraft.log(Level.ERROR, "Caught " + e.toString() + ", defaulting to hardcoded list!");
            return STARTER_COLORS[random.nextInt(STARTER_COLORS.length)];
        }
    }
}
