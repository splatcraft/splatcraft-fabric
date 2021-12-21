package net.splatcraft.block;

import net.minecraft.block.*;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.splatcraft.inkcolor.InkColor;
import net.splatcraft.inkcolor.Inkable;
import net.splatcraft.world.SplatcraftGameRules;

import static net.splatcraft.util.SplatcraftUtil.getInkColorFromStack;
import static net.splatcraft.util.SplatcraftUtil.setInkColorOnStack;

@SuppressWarnings("deprecation")
public class InkwellBlock extends InkableBlock implements FluidFillable {
    private static final VoxelShape SHAPE = VoxelShapes.union(
        Block.createCuboidShape(0.0d, 0.0d, 0.0d, 16.0d, 12.0d, 16.0d),
        Block.createCuboidShape(1.0d, 12.0d, 1.0d, 14.0d, 13.0d, 14.0d),
        Block.createCuboidShape(0.0d, 13.0d, 0.0d, 16.0d, 16.0d, 16.0d)
    );

    public InkwellBlock(Settings settings) {
        super(settings);
    }

    @Override
    public boolean tryFillWithFluid(WorldAccess world, BlockPos pos, BlockState state, FluidState fluidState) {
        if (fluidState.getFluid() == Fluids.WATER) {
            world.setBlockState(pos, SplatcraftBlocks.EMPTY_INKWELL.getDefaultState().with(Properties.WATERLOGGED, true), Block.NOTIFY_ALL);
            world.createAndScheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
            return true;
        }

        return false;
    }

    @Override
    public boolean canFillWithFluid(BlockView world, BlockPos pos, BlockState state, Fluid fluid) {
        return true;
    }

    @Override
    public void onEntityLand(BlockView world, Entity entity) {
        super.onEntityLand(world, entity);

        // TODO generify this outside of inkwells (move to item entity class)
        // set ink color of item if dropped on inkwell
        if (entity.world.getGameRules().getBoolean(SplatcraftGameRules.INKWELL_CHANGES_INK_COLOR)) {
            if (entity instanceof ItemEntity itemEntity) {
                BlockPos pos = entity.getBlockPos();
                if (world.getBlockEntity(pos.down()) instanceof Inkable inkable) {
                    ItemStack stack = itemEntity.getStack();
                    if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof InkableBlock) {
                        InkColor inkColor = inkable.getInkColor();
                        if (!getInkColorFromStack(stack).equals(inkColor)/* && !isColorLocked(stack)*/) {
                            setInkColorOnStack(stack, inkColor);
                        }
                    }
                }
            }
        }
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public PistonBehavior getPistonBehavior(BlockState state) {
        return PistonBehavior.DESTROY;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
}
