package net.splatcraft.block;

import net.minecraft.block.*;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

@SuppressWarnings("deprecation")
public class GrateBlock extends Block implements Waterloggable, InkPassableBlock {
    public static final EnumProperty<BlockHalf> HALF = Properties.BLOCK_HALF;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    protected static final VoxelShape SHAPE_BOTTOM = Block.createCuboidShape(0.0d, 0.0d, 0.0d, 16.0d, 3.0d, 16.0d);
    protected static final VoxelShape SHAPE_TOP = Block.createCuboidShape(0.0d, 13.0d, 0.0d, 16.0d, 16.0d, 16.0d);

    public GrateBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(HALF, BlockHalf.TOP).with(WATERLOGGED, false));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return state.get(HALF) == BlockHalf.TOP ? SHAPE_TOP : SHAPE_BOTTOM;
    }

    @Override
    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return !(type == NavigationType.WATER && state.get(WATERLOGGED));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState state = this.getDefaultState();
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        Direction direction = ctx.getSide();
        if (!ctx.canReplaceExisting() && direction.getAxis().isHorizontal()) {
            state = state.with(HALF, ctx.getHitPos().y - (double)ctx.getBlockPos().getY() > 0.5d ? BlockHalf.TOP : BlockHalf.BOTTOM);
        } else {
            state = state.with(HALF, direction == Direction.UP ? BlockHalf.BOTTOM : BlockHalf.TOP);
        }

        return state.with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(HALF, WATERLOGGED);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
        if (state.get(WATERLOGGED)) {
            world.createAndScheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }

        return super.getStateForNeighborUpdate(state, facing, newState, world, pos, posFrom);
    }
}
