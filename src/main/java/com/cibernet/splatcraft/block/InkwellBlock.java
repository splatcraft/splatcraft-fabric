package com.cibernet.splatcraft.block;

import com.cibernet.splatcraft.block.entity.AbstractInkableBlockEntity;
import com.cibernet.splatcraft.block.entity.InkableBlockEntity;
import com.cibernet.splatcraft.init.SplatcraftBlocks;
import com.cibernet.splatcraft.inkcolor.ColorUtil;
import com.cibernet.splatcraft.inkcolor.InkColor;
import com.cibernet.splatcraft.inkcolor.InkType;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

@SuppressWarnings("deprecation")
public class InkwellBlock extends AbstractInkableBlock implements Waterloggable, Stainable {
    public static final String id = "inkwell";

    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    private static final VoxelShape SHAPE = VoxelShapes.union(
        Block.createCuboidShape(0.0d, 0.0d, 0.0d, 16.0d, 12.0d, 16.0d),
        Block.createCuboidShape(1.0d, 12.0d, 1.0d, 14.0d, 13.0d, 14.0d),
        Block.createCuboidShape(0.0d, 13.0d, 0.0d, 16.0d, 16.0d, 16.0d)
    );

    public InkwellBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(WATERLOGGED, false));

        SplatcraftBlocks.addToInkables(this);
    }

    @Override
    public DyeColor getColor() {
        return DyeColor.WHITE;
    }

    @Override
    public boolean inkBlock(World world, BlockPos pos, InkColor color, InkType inkType, boolean spawnParticles) {
        return false;
    }

    @Override
    public void onEntityLand(BlockView world, Entity entity) {
        super.onEntityLand(world, entity);

        if (entity instanceof ItemEntity) {
            AbstractInkableBlockEntity blockEntity = (AbstractInkableBlockEntity) world.getBlockEntity(entity.getBlockPos().down());
            if (blockEntity != null) {
                ItemStack stack = ((ItemEntity) entity).getStack();
                Item stackItem = stack.getItem();
                if (stackItem instanceof BlockItem && ((BlockItem) stackItem).getBlock() instanceof InkedBlock && ColorUtil.getInkColor(stack) != blockEntity.getInkColor() && !ColorUtil.isColorLocked(stack)) {
                    ((ItemEntity) entity).setStack(ColorUtil.setInkColor(stack, blockEntity.getInkColor()));
                }
            }
        }
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        return this.getDefaultState().with(WATERLOGGED, context.getWorld().getFluidState(context.getBlockPos()).getFluid() == Fluids.WATER);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(WATERLOGGED);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
        if (state.get(WATERLOGGED)) {
            world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }

        return super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView worldIn, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public PistonBehavior getPistonBehavior(BlockState state) {
        return PistonBehavior.DESTROY;
    }

    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return new InkableBlockEntity();
    }

    @Override
    public boolean canClimb() {
        return false;
    }
    @Override
    public boolean canSwim() {
        return false;
    }
    @Override
    public boolean canDamage() {
        return true;
    }

    @Override
    public boolean massInkClear(World world, BlockPos pos) {
        return false;
    }
}
