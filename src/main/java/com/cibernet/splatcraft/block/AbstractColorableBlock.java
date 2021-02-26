package com.cibernet.splatcraft.block;

import com.cibernet.splatcraft.block.entity.AbstractColorableBlockEntity;
import com.cibernet.splatcraft.inkcolor.ColorUtils;
import com.cibernet.splatcraft.inkcolor.InkBlockUtils;
import com.cibernet.splatcraft.inkcolor.InkColor;
import com.cibernet.splatcraft.inkcolor.InkColors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public abstract class AbstractColorableBlock extends BlockWithEntity {
    public static final String id = "colorable";

    protected AbstractColorableBlock(Settings settings) {
        super(settings);
    }

    public abstract boolean canClimb();
    public abstract boolean canSwim();
    public abstract boolean canDamage();
    public abstract boolean remoteInkClear(World world, BlockPos pos);
    public abstract boolean countsTowardsTurf(World world, BlockPos pos);

    public boolean inkBlock(World world, BlockPos pos, InkColor color, float damage, InkBlockUtils.InkType inkType) {
        return false;
    }

    public void setColor(World world, BlockPos pos, InkColor color) {}
    public InkColor getColor(World world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        return blockEntity instanceof AbstractColorableBlockEntity ? ((AbstractColorableBlockEntity) blockEntity).getInkColor() : InkColors.NONE;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public boolean onSyncedBlockEvent(BlockState state, World world, BlockPos pos, int type, int data) {
        if (world.isClient) {
            ((ClientWorld)world).scheduleBlockRenders(pos.getX(), pos.getY(), pos.getZ());
        }

        return super.onSyncedBlockEvent(state, world, pos, type, data);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (stack.getTag() != null && blockEntity instanceof AbstractColorableBlockEntity) {
            ColorUtils.setInkColor(blockEntity, ColorUtils.getInkColor(stack));
        }

        super.onPlaced(world, pos, state, entity, stack);
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state)  {
        ItemStack stack = super.getPickStack(world, pos, state);

        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof AbstractColorableBlockEntity) {
            ColorUtils.setInkColor(stack, ColorUtils.getInkColor(blockEntity));
        }

        return stack;
    }

    public boolean remoteColorChange(World world, BlockPos pos, InkColor newColor) {
        BlockState state = world.getBlockState(pos);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof AbstractColorableBlockEntity && ((AbstractColorableBlockEntity) blockEntity).getInkColor() != newColor) {
            ((AbstractColorableBlockEntity) blockEntity).setInkColor(newColor);
            world.updateListeners(pos, state, state, 2);
            return true;
        }

        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState getStateForNeighborUpdate(BlockState blockState, Direction direction, BlockState newState, WorldAccess world, BlockPos blockPos, BlockPos posFrom) {
        if (InkedBlock.isTouchingLiquid(world, blockPos)) {
            BlockEntity blockEntity = world.getBlockEntity(blockPos);
            if (blockEntity instanceof AbstractColorableBlockEntity) {
                ((AbstractColorableBlockEntity) blockEntity).setInkColor(InkColors.NONE);
            }
        }

        return super.getStateForNeighborUpdate(blockState, direction, newState, world, blockPos, posFrom);
    }

    @Override
    public abstract BlockEntity createBlockEntity(BlockView world);

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
}
