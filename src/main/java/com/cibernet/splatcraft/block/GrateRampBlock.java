package com.cibernet.splatcraft.block;

import net.minecraft.block.*;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

@SuppressWarnings("deprecation")
public class GrateRampBlock extends AbstractPassableBlock implements Waterloggable {
    public static final String id = GrateBlock.id + "_ramp";

    private static final VoxelShape START = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 3.0D, 3.0D, 16.0D);
    private static final VoxelShape END = Block.createCuboidShape(13.0D, 13.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape SEGMENT = Block.createCuboidShape(1.0D, 2.0D, 0.0D, 4.0D, 5.0D, 16.0D);

    public static final VoxelShape[] SHAPES = getVoxelShapes(START, END, SEGMENT);

    public static final EnumProperty<Direction> FACING = Properties.HORIZONTAL_FACING;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    public GrateRampBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(WATERLOGGED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FACING, WATERLOGGED);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView worldIn, BlockPos pos, ShapeContext context) {
        return SHAPES[state.get(FACING).ordinal()-2];
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        BlockPos blockPos = context.getBlockPos();
        Direction direction = context.getSide();
        FluidState fluidState = context.getWorld().getFluidState(blockPos);
        boolean flip = direction != Direction.DOWN && (direction == Direction.UP || !(context.getHitPos().y - (double)blockPos.getY() > 0.5D));
        return this.getDefaultState().with(FACING, flip ? context.getPlayerFacing().getOpposite() : context.getPlayerFacing()).with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    private static VoxelShape[] getVoxelShapes(VoxelShape start, VoxelShape end, VoxelShape segment) {
        VoxelShape[] shapes = new VoxelShape[8];

        for (int i = 0; i < 6; i++)
            shapes[i] = segment.offset(0.125D * i, 0.125D * i, 0.0D);

        shapes[6] = start;
        shapes[7] = end;

        return GrateRampBlock.createVoxelShapes(shapes);
    }

    protected static VoxelShape modifyShapeForDirection(Direction facing, VoxelShape shape) {
        Box bb = shape.getBoundingBox();

        switch(facing) {
            case SOUTH:
                return VoxelShapes.cuboid(new Box(1 - bb.maxZ, bb.minY, bb.minX, 1 - bb.minZ, bb.maxY, bb.maxX));
            case EAST:
                return VoxelShapes.cuboid(new Box(1 - bb.maxX, bb.minY, 1- bb.maxZ, 1 - bb.minX, bb.maxY, 1 - bb.minZ));
            case WEST:
                return VoxelShapes.cuboid(new Box(bb.minZ, bb.minY, 1 - bb.maxX, bb.maxZ, bb.maxY, 1 - bb.minX));
        }
        return shape;
    }

    protected static VoxelShape[] createVoxelShapes(VoxelShape... shapes) {
        VoxelShape[] result = new VoxelShape[4];

        for (int i = 0; i < 4; i++) {
            result[i] = VoxelShapes.empty();
            for (VoxelShape shape : shapes) {
                result[i] = VoxelShapes.union(result[i], modifyShapeForDirection(Direction.fromHorizontal(i), shape));
            }
        }

        return result;
    }
}
