package net.splatcraft.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

import java.util.function.Function;

@SuppressWarnings("deprecation")
public class GrateRampBlock extends Block implements Waterloggable, InkPassableBlock {
    public static final EnumProperty<Direction> FACING = Properties.HORIZONTAL_FACING;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    public static final VoxelShape START = createCuboidShape(0.0d, 0.0d, 0.0d, 3.0d, 3.0d, 16.0d);
    public static final VoxelShape END = createCuboidShape(13.0d, 13.0d, 0.0d, 16.0d, 16.0d, 16.0d);
    public static final VoxelShape SEGMENT = createCuboidShape(1.0d, 2.0d, 0.0d, 4.0d, 5.0d, 16.0d);

    public static final Function<Direction, VoxelShape> SHAPES = Util.memoize(d -> getVoxelShapes(START, END, SEGMENT)[d.ordinal() - 2]);

    public GrateRampBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(WATERLOGGED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder.add(FACING, WATERLOGGED));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        return SHAPES.apply(state.get(FACING));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockPos pos = ctx.getBlockPos();
        Direction direction = ctx.getSide();
        Direction facing = ctx.getPlayerFacing();
        FluidState fluidState = ctx.getWorld().getFluidState(pos);
        boolean flip = direction != Direction.DOWN && (direction == Direction.UP || !(ctx.getHitPos().y - (double)pos.getY() > 0.5d));
        return this.getDefaultState().with(FACING, flip ? facing.getOpposite() : facing).with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    public static VoxelShape[] getVoxelShapes(VoxelShape start, VoxelShape end, VoxelShape segment) {
        VoxelShape[] shapes = new VoxelShape[8];

        for (int i = 0; i < 6; i++) shapes[i] = segment.offset(0.125d * i, 0.125d * i, 0.0d);

        shapes[6] = start;
        shapes[7] = end;

        return createVoxelShapes(shapes);
    }

    public static VoxelShape[] createVoxelShapes(VoxelShape... shapes) {
        VoxelShape[] result = new VoxelShape[4];

        for (int i = 0; i < 4; i++) {
            result[i] = VoxelShapes.empty();
            for (VoxelShape shape : shapes) {
                result[i] = VoxelShapes.union(result[i], modifyShapeForDirection(Direction.fromHorizontal(i), shape));
            }
        }

        return result;
    }

    public static VoxelShape modifyShapeForDirection(Direction facing, VoxelShape shape) {
        Box box = shape.getBoundingBox();
        return switch (facing) {
            case SOUTH -> VoxelShapes.cuboid(new Box(1 - box.maxZ, box.minY, box.minX, 1 - box.minZ, box.maxY, box.maxX));
            case EAST -> VoxelShapes.cuboid(new Box(1 - box.maxX, box.minY, 1 - box.maxZ, 1 - box.minX, box.maxY, 1 - box.minZ));
            case WEST -> VoxelShapes.cuboid(new Box(box.minZ, box.minY, 1 - box.maxX, box.maxZ, box.maxY, 1 - box.minX));
            default -> shape;
        };
    }
}
