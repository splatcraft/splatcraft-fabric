package com.cibernet.splatcraft.inkcolor;

import com.cibernet.splatcraft.block.AbstractInkableBlock;
import com.cibernet.splatcraft.block.AbstractPassableBlock;
import com.cibernet.splatcraft.block.InkedBlock;
import com.cibernet.splatcraft.block.entity.AbstractInkableBlockEntity;
import com.cibernet.splatcraft.block.entity.InkedBlockEntity;
import com.cibernet.splatcraft.component.PlayerDataComponent;
import com.cibernet.splatcraft.init.*;
import com.cibernet.splatcraft.tag.SplatcraftBlockTags;
import com.cibernet.splatcraft.tag.SplatcraftEntityTypeTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class InkBlockUtils {
    public static boolean inkBlock(World world, BlockPos pos, InkColor inkColor, float damage, InkType inkType) {
        BlockState state = world.getBlockState(pos);

        if (!inkColor.equals(InkColors.NONE) && !InkedBlock.isTouchingLiquid(world, pos)) {
            if (state.getBlock() instanceof AbstractInkableBlock) {
                return ((AbstractInkableBlock) state.getBlock()).inkBlock(world, pos, inkColor, damage, inkType, true);
            } else if (canInk(world, pos) && world.getBlockEntity(pos) == null) {
                if (!world.isClient) {
                    InkedBlockEntity blockEntity = new InkedBlockEntity();
                    blockEntity.setSavedState(state);
                    blockEntity.setInkColor(inkColor);
                    world.setBlockState(pos, inkType.asBlock().getDefaultState());
                    world.setBlockEntity(pos, blockEntity);
                    ColorUtils.addInkSplashParticle(world, inkColor, Vec3d.ofBottomCenter(pos));

                    // sync
                    blockEntity.sync();
                }

                return true;
            }
        }

        return false;
    }
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

    public static boolean shouldBeSubmerged(PlayerEntity player) {
        return PlayerDataComponent.isSquid(player) && (InkBlockUtils.canSwim(player) || InkBlockUtils.canClimb(player));
    }

    public static boolean canSwim(PlayerEntity player, boolean ignoreOnGround) {
        return (ignoreOnGround || player.isOnGround()) && InkBlockUtils.isOnInk(player) && !PlayerDataComponent.getInkColor(player).equals(InkColors.NONE) && !InkBlockUtils.onEnemyInk(player);
    }
    public static boolean canSwim(PlayerEntity player) {
        return canSwim(player, false);
    }
    public static boolean takeDamage(PlayerEntity player) {
        return InkBlockUtils.onEnemyInk(player);
    }

    public static boolean onEnemyInk(World world, BlockPos pos, InkColor comparisonColor) {
        if (!SplatcraftGameRules.getBoolean(world, SplatcraftGameRules.UNIVERSAL_INK)) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof AbstractInkableBlockEntity) {
                Block block = blockEntity.getCachedState().getBlock();
                if (block instanceof AbstractInkableBlock && ((AbstractInkableBlock) block).canDamage()) {
                    InkColor inkColor = ColorUtils.getInkColor(blockEntity);
                    return !comparisonColor.equals(InkColors.NONE) && !inkColor.equals(InkColors.NONE) && !comparisonColor.matches(inkColor.getColor());
                }
            }
        }

        return false;
    }
    public static boolean onEnemyInk(PlayerEntity player) {
        return InkBlockUtils.onEnemyInk(player.world, player.getVelocityAffectingPos(), ColorUtils.getInkColor(player));
    }

    public static boolean isOnInk(World world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (blockEntity instanceof AbstractInkableBlockEntity) {
            return !ColorUtils.getInkColor(blockEntity).equals(InkColors.NONE) && ((AbstractInkableBlock) blockEntity.getCachedState().getBlock()).canSwim();
        }

        return false;
    }
    public static boolean isOnInk(PlayerEntity player) {
        return InkBlockUtils.isOnInk(player.world, player.getVelocityAffectingPos());
    }

    public static boolean entityPassesThroughGaps(Entity entity) {
        return SplatcraftEntityTypeTags.PASSES_THROUGH_GAPS.contains(entity.getType()) || entity instanceof PlayerEntity && SplatcraftComponents.PLAYER_DATA.get(entity).isSquid();
    }

    public static BlockPos getVelocityAffectingPos(PlayerEntity player) {
        if (!player.hasVehicle()) {
            BlockPos pos = InkBlockUtils.getClimbingPos(player);
            if (pos != null) {
                return pos;
            }
        }

        return player.getVelocityAffectingPos();
    }
    public static BlockPos getClimbingPos(PlayerEntity player) {
        for (int i = 0; i < 4; i++) {
            float xOff = (i < 2 ? 0.32f : 0) * (i % 2 == 0 ? 1 : -1), zOff = (i < 2 ? 0 : 0.32f) * (i % 2 == 0 ? 1 : -1);
            BlockPos pos = new BlockPos(player.getX() - xOff, player.getY(), player.getZ() - zOff);
            Block block = player.world.getBlockState(pos).getBlock();

            if (block instanceof AbstractInkableBlock && ((AbstractInkableBlock) block).canClimb()) {
                BlockEntity blockEntity = player.world.getBlockEntity(pos);
                if (blockEntity instanceof AbstractInkableBlockEntity && ((AbstractInkableBlockEntity) blockEntity).getInkColor().matches(ColorUtils.getInkColor(player).getColor())) {
                    return pos;
                }
            }
        }

        return null;
    }

    public static boolean canClimb(PlayerEntity player) {
        if (InkBlockUtils.onEnemyInk(player) || PlayerDataComponent.getInkColor(player).equals(InkColors.NONE)) {
            return false;
        } else {
            BlockPos pos = InkBlockUtils.getClimbingPos(player);
            if (pos != null) {
                BlockEntity blockEntity = player.world.getBlockEntity(pos);
                if (blockEntity instanceof AbstractInkableBlockEntity) {
                    return ((AbstractInkableBlockEntity) blockEntity).getInkColor().matches(ColorUtils.getInkColor(player).getColor());
                }
            }
        }

        return false;
    }

    public static InkBlockUtils.InkType getInkType(PlayerEntity entity) {
        return entity != null && entity.inventory.contains(new ItemStack(SplatcraftItems.SPLATFEST_BAND)) ? InkType.GLOWING : InkType.NORMAL;
    }

    public enum InkType {
        NORMAL,
        GLOWING;

        public Block asBlock() {
            switch (this) {
                default:
                case NORMAL:
                    return SplatcraftBlocks.INKED_BLOCK;
                case GLOWING:
                    return SplatcraftBlocks.GLOWING_INKED_BLOCK;
            }
        }
        public static InkType fromBlock(InkedBlock block) {
            return block.isGlowing() ? GLOWING : NORMAL;
        }
    }
}
