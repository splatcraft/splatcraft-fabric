package com.cibernet.splatcraft.util;

import com.cibernet.splatcraft.block.AbstractInkableBlock;
import com.cibernet.splatcraft.block.AbstractPassableBlock;
import com.cibernet.splatcraft.block.InkedBlock;
import com.cibernet.splatcraft.block.entity.AbstractInkableBlockEntity;
import com.cibernet.splatcraft.block.entity.InkedBlockEntity;
import com.cibernet.splatcraft.component.LazyPlayerDataComponent;
import com.cibernet.splatcraft.init.SplatcraftStats;
import com.cibernet.splatcraft.inkcolor.ColorUtil;
import com.cibernet.splatcraft.inkcolor.InkColor;
import com.cibernet.splatcraft.inkcolor.InkColors;
import com.cibernet.splatcraft.inkcolor.InkType;
import com.cibernet.splatcraft.tag.SplatcraftBlockTags;
import com.cibernet.splatcraft.tag.SplatcraftEntityTypeTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class InkBlockUtil {
    public static boolean inkBlock(World world, BlockPos pos, InkColor inkColor, InkType inkType) {
        BlockState state = world.getBlockState(pos);

        if (!inkColor.equals(InkColors.NONE) && !InkedBlock.isTouchingLiquid(world, pos)) {
            if (state.getBlock() instanceof AbstractInkableBlock) {
                return ((AbstractInkableBlock) state.getBlock()).inkBlock(world, pos, inkColor, inkType, true);
            } else if (canInk(world, pos) && world.getBlockEntity(pos) == null) {
                InkedBlockEntity blockEntity = new InkedBlockEntity();
                blockEntity.setSavedState(state);
                blockEntity.setInkColor(inkColor);
                world.setBlockState(pos, inkType.asBlock().getDefaultState());
                world.setBlockEntity(pos, blockEntity);
                ColorUtil.addInkSplashParticle(world, inkColor, Vec3d.ofBottomCenter(pos.up()));

                if (!world.isClient) {
                    // sync
                    blockEntity.sync();
                }

                return true;
            }
        }

        return false;
    }
    public static boolean inkBlockAsPlayer(PlayerEntity player, World world, BlockPos pos, InkColor color, InkType inkType) {
        if (InkBlockUtil.inkBlock(world, pos, color, inkType)) {
            player.incrementStat(SplatcraftStats.BLOCKS_INKED);
            return true;
        }

        return false;
    }

    public static boolean canInk(World world, BlockPos pos) {
        if (InkedBlock.isTouchingLiquid(world, pos)) {
            return false;
        }

        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        if (state.isAir()) {
            return false;
        } else if (!(world.getBlockEntity(pos) instanceof AbstractInkableBlockEntity) && world.getBlockEntity(pos) != null) {
            return false;
        } else if (SplatcraftBlockTags.UNINKABLE_BLOCKS.contains(block)) {
            return false;
        } else if (SplatcraftBlockTags.INKABLE_BLOCKS.contains(block)) {
            return true;
        } else if (canInkPassthrough(world, pos)) {
            return false;
        } else if (world.getBlockState(pos).isFullCube(world, pos)) {
            return true;
        }

        return true;
    }

    public static boolean canInkPassthrough(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return state.getBlock() instanceof AbstractPassableBlock || state.getCollisionShape(world, pos).isEmpty();
    }

    public static boolean entityPassesThroughGaps(Entity entity) {
        return SplatcraftEntityTypeTags.PASSES_THROUGH_GAPS.contains(entity.getType()) || entity instanceof PlayerEntity && LazyPlayerDataComponent.isSquid((PlayerEntity) entity);
    }

}
