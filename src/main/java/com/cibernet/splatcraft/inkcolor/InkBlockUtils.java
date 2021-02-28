package com.cibernet.splatcraft.inkcolor;

import com.cibernet.splatcraft.block.AbstractInkableBlock;
import com.cibernet.splatcraft.block.AbstractPassableBlock;
import com.cibernet.splatcraft.block.InkedBlock;
import com.cibernet.splatcraft.block.entity.AbstractInkableBlockEntity;
import com.cibernet.splatcraft.block.entity.InkedBlockEntity;
import com.cibernet.splatcraft.init.SplatcraftComponents;
import com.cibernet.splatcraft.init.SplatcraftGameRules;
import com.cibernet.splatcraft.init.SplatcraftItems;
import com.cibernet.splatcraft.init.SplatcraftStats;
import com.cibernet.splatcraft.tag.SplatcraftBlockTags;
import com.cibernet.splatcraft.tag.SplatcraftEntityTypeTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class InkBlockUtils {
    public static boolean inkBlock(World world, BlockPos pos, InkColor color, float damage, InkType inkType) {
        BlockState state = world.getBlockState(pos);

        if (InkedBlock.isTouchingLiquid(world, pos)) {
            return false;
        } else if (state.getBlock() instanceof AbstractInkableBlock) {
            return ((AbstractInkableBlock) state.getBlock()).inkBlock(world, pos, color, damage, inkType);
        } else if (canInk(world, pos)) {
            return true;
        } else {
            world.setBlockEntity(pos, new InkedBlockEntity());

            InkedBlockEntity inkedBlockEntity = (InkedBlockEntity) world.getBlockEntity(pos);
            if (inkedBlockEntity == null) {
                return false;
            }
            inkedBlockEntity.setInkColor(color);
            inkedBlockEntity.setSavedState(state);

            return true;
        }
    }
    @SuppressWarnings("unused")
    public static boolean inkBlockAsPlayer(PlayerEntity player, World world, BlockPos pos, InkColor color, float damage, InkType inkType) {
        if (InkBlockUtils.inkBlock(world, pos, color, damage, inkType)) {
            player.incrementStat(SplatcraftStats.BLOCKS_INKED);
            return true;
        }

        return false;
    }

    public static boolean canInk(World world, BlockPos pos) {
        if (InkedBlock.isTouchingLiquid(world, pos)) {
            return false;
        }

        Block block = world.getBlockState(pos).getBlock();

        if (SplatcraftBlockTags.UNINKABLE_BLOCKS.contains(block)) {
            return false;
        } else if (!(world.getBlockEntity(pos) instanceof AbstractInkableBlockEntity) && world.getBlockEntity(pos) != null) {
            return false;
        } else if (SplatcraftBlockTags.INKABLE_BLOCKS.contains(block)) {
            return true;
        } else if (!canInkPassthrough(world, pos)) {
            return true;
        } else if (world.getBlockState(pos).isFullCube(world, pos)) {
            return true;
        }

        return !world.getBlockState(pos).hasSidedTransparency();
    }

    public static boolean canInkPassthrough(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);

        if (state.getBlock() instanceof AbstractPassableBlock) {
            return true;
        }
        return state.getCollisionShape(world, pos).isEmpty();
    }

    public static boolean shouldBeSubmerged(PlayerEntity entity) {
        return InkBlockUtils.canSwim(entity) || InkBlockUtils.canClimb(entity);
    }

    public static boolean canSwim(PlayerEntity player) {
        return player.isOnGround() && InkBlockUtils.isOnInk(player) && (SplatcraftGameRules.getBoolean(player.world, SplatcraftGameRules.UNIVERSAL_INK) || !InkBlockUtils.onEnemyInk(player));
    }
    public static boolean takeDamage(PlayerEntity player) {
        return !SplatcraftGameRules.getBoolean(player.world, SplatcraftGameRules.UNIVERSAL_INK) && InkBlockUtils.onEnemyInk(player);
    }

    public static boolean onEnemyInk(World world, BlockPos pos, InkColor comparisonColor) {
        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (blockEntity instanceof AbstractInkableBlockEntity) {
            InkColor inkColor = ColorUtils.getInkColor(blockEntity);
            return comparisonColor != InkColors.NONE && inkColor != InkColors.NONE && comparisonColor != inkColor;
        }

        return false;
    }
    public static boolean onEnemyInk(PlayerEntity player) {
        return InkBlockUtils.onEnemyInk(player.world, player.getVelocityAffectingPos(), ColorUtils.getInkColor(player));
    }

    public static boolean isOnInk(World world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (blockEntity instanceof AbstractInkableBlockEntity) {
            return ColorUtils.getInkColor(blockEntity) != InkColors.NONE && ((AbstractInkableBlock) blockEntity.getCachedState().getBlock()).canSwim();
        }

        return false;
    }
    public static boolean isOnInk(PlayerEntity player) {
        return InkBlockUtils.isOnInk(player.world, player.getVelocityAffectingPos());
    }

    public static boolean entityPassesThroughGaps(Entity entity) {
        return SplatcraftEntityTypeTags.PASSES_THROUGH_GAPS.contains(entity.getType()) || entity instanceof PlayerEntity && SplatcraftComponents.PLAYER_DATA.get(entity).isSquid();
    }

    public static boolean canClimb(PlayerEntity player) {
        if (InkBlockUtils.onEnemyInk(player)) {
            return false;
        } else {
            for (int i = 0; i < 4; i++) {
                float xOff = (i < 2 ? .32f : 0) * (i % 2 == 0 ? 1 : -1), zOff = (i < 2 ? 0 : .32f) * (i % 2 == 0 ? 1 : -1);
                BlockPos pos = new BlockPos(player.getX() - xOff, player.getY(), player.getZ() - zOff);
                Block block = player.world.getBlockState(pos).getBlock();

                if (!(block instanceof AbstractInkableBlock) || ((AbstractInkableBlock) block).canClimb()) {
                    BlockEntity blockEntity = player.world.getBlockEntity(pos);
                    if (blockEntity instanceof AbstractInkableBlockEntity && ((AbstractInkableBlockEntity) blockEntity).getInkColor() == ColorUtils.getEntityColor(player) && !player.hasVehicle()) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @SuppressWarnings("unused")
    public static InkBlockUtils.InkType getInkType(PlayerEntity entity) {
        if (entity != null && entity.inventory.contains(new ItemStack(SplatcraftItems.SPLATFEST_BAND))) {
            return InkType.GLOWING;
        }

        return InkType.NORMAL;
    }

    public enum InkType {
        NORMAL,
        GLOWING
    }
}
