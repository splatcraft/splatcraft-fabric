package com.cibernet.splatcraft.block;

import net.minecraft.block.*;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.block.enums.StairShape;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

@SuppressWarnings("deprecation")
public class BarrierBarBlock extends Block implements Waterloggable {
    public static final String id = "barrier_bar";

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final EnumProperty<BlockHalf> HALF = Properties.BLOCK_HALF;
    public static final EnumProperty<StairShape> SHAPE = Properties.STAIR_SHAPE;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    protected static final VoxelShape NU_STRAIGHT = Block.createCuboidShape(0, 13, 0, 16, 16, 3);
    protected static final VoxelShape SU_STRAIGHT = modifyShapeForDirection(Direction.SOUTH, NU_STRAIGHT);
    protected static final VoxelShape WU_STRAIGHT = modifyShapeForDirection(Direction.WEST, NU_STRAIGHT);
    protected static final VoxelShape EU_STRAIGHT = modifyShapeForDirection(Direction.EAST, NU_STRAIGHT);
    protected static final VoxelShape ND_STRAIGHT = mirrorShapeY(NU_STRAIGHT);
    protected static final VoxelShape SD_STRAIGHT = mirrorShapeY(SU_STRAIGHT);
    protected static final VoxelShape WD_STRAIGHT = mirrorShapeY(WU_STRAIGHT);
    protected static final VoxelShape ED_STRAIGHT = mirrorShapeY(EU_STRAIGHT);


    protected static final VoxelShape NU_CORNER = Block.createCuboidShape(0, 13, 0, 3, 16, 3);
    protected static final VoxelShape SU_CORNER = modifyShapeForDirection(Direction.SOUTH, NU_CORNER);
    protected static final VoxelShape WU_CORNER = modifyShapeForDirection(Direction.WEST, NU_CORNER);
    protected static final VoxelShape EU_CORNER = modifyShapeForDirection(Direction.EAST, NU_CORNER);
    protected static final VoxelShape ND_CORNER = mirrorShapeY(NU_CORNER);
    protected static final VoxelShape SD_CORNER = mirrorShapeY(SU_CORNER);
    protected static final VoxelShape WD_CORNER = mirrorShapeY(WU_CORNER);
    protected static final VoxelShape ED_CORNER = mirrorShapeY(EU_CORNER);

    protected static final VoxelShape[] TOP_SHAPES = new VoxelShape[] {NU_STRAIGHT, SU_STRAIGHT, WU_STRAIGHT, EU_STRAIGHT, NU_CORNER, SU_CORNER, WU_CORNER, EU_CORNER};
    protected static final VoxelShape[] BOTTOM_SHAPES = new VoxelShape[] {ND_STRAIGHT, SD_STRAIGHT, WD_STRAIGHT, ED_STRAIGHT, ND_CORNER, SD_CORNER, WD_CORNER, ED_CORNER};

    public BarrierBarBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(HALF, BlockHalf.BOTTOM).with(SHAPE, StairShape.STRAIGHT).with(WATERLOGGED, false));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView worldIn, BlockPos pos, ShapeContext context) {
        VoxelShape[] shapeArray = state.get(HALF).equals(BlockHalf.TOP) ? TOP_SHAPES : BOTTOM_SHAPES;
        int dirIndex = state.get(FACING).ordinal()-2;
        int rotatedDirIndex = state.get(FACING).rotateYClockwise().ordinal()-2;
        int rotatedCCWDirIndex = state.get(FACING).rotateYCounterclockwise().ordinal()-2;

        switch(state.get(SHAPE)) {
            case STRAIGHT: return shapeArray[dirIndex];
            case OUTER_LEFT: return shapeArray[dirIndex+4];
            case OUTER_RIGHT: return shapeArray[rotatedDirIndex+4];
            case INNER_LEFT: return VoxelShapes.union(shapeArray[dirIndex], shapeArray[rotatedCCWDirIndex]);
            case INNER_RIGHT: return VoxelShapes.union(shapeArray[dirIndex], shapeArray[rotatedDirIndex]);
        }

        return Block.createCuboidShape(1, 1, 1, 15, 15, 15);
    }

    @Override
    public boolean isTranslucent(BlockState state, BlockView world, BlockPos pos) {
        return true;
    }

    protected static VoxelShape modifyShapeForDirection(Direction facing, VoxelShape shape) {
        Box box = shape.getBoundingBox();

        switch (facing) {
            case EAST:
                return VoxelShapes.cuboid(new Box(1 - box.maxZ, box.minY, box.minX, 1 - box.minZ, box.maxY, box.maxX));
            case SOUTH:
                return VoxelShapes.cuboid(new Box(1 - box.maxX, box.minY, 1- box.maxZ, 1 - box.minX, box.maxY, 1 - box.minZ));
            case WEST:
                return VoxelShapes.cuboid(new Box(box.minZ, box.minY, 1 - box.maxX, box.maxZ, box.maxY, 1 - box.minX));
        }
        return shape;
    }

    public static VoxelShape mirrorShapeY(VoxelShape shape) {
        Box box = shape.getBoundingBox();
        return VoxelShapes.cuboid(new Box(box.minX, 1 - box.minY, box.minZ, box.maxX, 1 - box.maxY, box.maxZ));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        Direction direction = context.getSide();
        BlockPos blockpos = context.getBlockPos();
        FluidState fluidstate = context.getWorld().getFluidState(blockpos);
        BlockState blockstate = this.getDefaultState().with(FACING, context.getPlayerFacing()).with(HALF, direction != Direction.DOWN && (direction == Direction.UP || !(context.getHitPos().y - (double)blockpos.getY() > 0.5d)) ? BlockHalf.BOTTOM : BlockHalf.TOP).with(WATERLOGGED, fluidstate.getFluid() == Fluids.WATER);
        return blockstate.with(SHAPE, getShapeProperty(blockstate, context.getWorld(), blockpos));
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
        if (state.get(WATERLOGGED)) {
            world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }

        return facing.getAxis().isHorizontal() ? state.with(SHAPE, getShapeProperty(state, world, pos)) : super.getStateForNeighborUpdate(state, facing, newState, world, pos, posFrom);
    }

    /**
     * Returns a stair shape property based on the surrounding stairs from the given blockstate and position
     */
    private static StairShape getShapeProperty(BlockState state, BlockView worldIn, BlockPos pos) {
        Direction direction = state.get(FACING);
        BlockState blockstate = worldIn.getBlockState(pos.offset(direction));
        if (isBar(blockstate) && state.get(HALF) == blockstate.get(HALF)) {
            Direction direction1 = blockstate.get(FACING);
            if (direction1.getAxis() != state.get(FACING).getAxis() && isDifferentBar(state, worldIn, pos, direction1.getOpposite())) {
                if (direction1 == direction.rotateYCounterclockwise()) {
                    return StairShape.OUTER_LEFT;
                }

                return StairShape.OUTER_RIGHT;
            }
        }

        BlockState blockstate1 = worldIn.getBlockState(pos.offset(direction.getOpposite()));
        if (isBar(blockstate1) && state.get(HALF) == blockstate1.get(HALF)) {
            Direction direction2 = blockstate1.get(FACING);
            if (direction2.getAxis() != state.get(FACING).getAxis() && isDifferentBar(state, worldIn, pos, direction2)) {
                if (direction2 == direction.rotateYCounterclockwise()) {
                    return StairShape.INNER_LEFT;
                }

                return StairShape.INNER_RIGHT;
            }
        }

        return StairShape.STRAIGHT;
    }

    private static boolean isDifferentBar(BlockState state, BlockView worldIn, BlockPos pos, Direction face) {
        BlockState blockstate = worldIn.getBlockState(pos.offset(face));
        return !isBar(blockstate) || blockstate.get(FACING) != state.get(FACING) || blockstate.get(HALF) != state.get(HALF);
    }

    public static boolean isBar(BlockState state) {
        return state.getBlock() instanceof BarrierBarBlock;
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rot) {
        return state.with(FACING, rot.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        Direction direction = state.get(FACING);
        StairShape stairsshape = state.get(SHAPE);
        switch(mirror) {
            case LEFT_RIGHT:
                if (direction.getAxis() == Direction.Axis.Z) {
                    switch(stairsshape) {
                        case INNER_LEFT:
                            return state.rotate(BlockRotation.CLOCKWISE_180).with(SHAPE, StairShape.INNER_RIGHT);
                        case INNER_RIGHT:
                            return state.rotate(BlockRotation.CLOCKWISE_180).with(SHAPE, StairShape.INNER_LEFT);
                        case OUTER_LEFT:
                            return state.rotate(BlockRotation.CLOCKWISE_180).with(SHAPE, StairShape.OUTER_RIGHT);
                        case OUTER_RIGHT:
                            return state.rotate(BlockRotation.CLOCKWISE_180).with(SHAPE, StairShape.OUTER_LEFT);
                        default:
                            return state.rotate(BlockRotation.CLOCKWISE_180);
                    }
                }
                break;
            case FRONT_BACK:
                if (direction.getAxis() == Direction.Axis.X) {
                    switch(stairsshape) {
                        case INNER_LEFT:
                            return state.rotate(BlockRotation.CLOCKWISE_180).with(SHAPE, StairShape.INNER_LEFT);
                        case INNER_RIGHT:
                            return state.rotate(BlockRotation.CLOCKWISE_180).with(SHAPE, StairShape.INNER_RIGHT);
                        case OUTER_LEFT:
                            return state.rotate(BlockRotation.CLOCKWISE_180).with(SHAPE, StairShape.OUTER_RIGHT);
                        case OUTER_RIGHT:
                            return state.rotate(BlockRotation.CLOCKWISE_180).with(SHAPE, StairShape.OUTER_LEFT);
                        case STRAIGHT:
                            return state.rotate(BlockRotation.CLOCKWISE_180);
                    }
                }
        }

        return super.mirror(state, mirror);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FACING, HALF, SHAPE, WATERLOGGED);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Override
    public boolean canPathfindThrough(BlockState state, BlockView worldIn, BlockPos pos, NavigationType type) {
        return false;
    }
}
