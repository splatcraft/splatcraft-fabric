package com.cibernet.splatcraft.block;

import com.cibernet.splatcraft.block.entity.AbstractInkableBlockEntity;
import com.cibernet.splatcraft.block.entity.InkedBlockEntity;
import com.cibernet.splatcraft.client.config.SplatcraftConfig;
import com.cibernet.splatcraft.init.SplatcraftBlocks;
import com.cibernet.splatcraft.init.SplatcraftGameRules;
import com.cibernet.splatcraft.inkcolor.ColorUtils;
import com.cibernet.splatcraft.inkcolor.InkBlockUtils;
import com.cibernet.splatcraft.inkcolor.InkColor;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import java.util.Optional;
import java.util.Random;

@SuppressWarnings("deprecation")
public class InkedBlock extends AbstractInkableBlock {
    public static final String id = "inked_block";

    private boolean glowing;

    public static final AbstractBlock.Settings DEFAULT_PROPERTIES = FabricBlockSettings.of(Material.ORGANIC_PRODUCT, MaterialColor.BLACK_TERRACOTTA)
        .breakByTool(FabricToolTags.PICKAXES).requiresTool()
        .sounds(BlockSoundGroup.SLIME).nonOpaque()
        .ticksRandomly().suffocates((state, world, pos) -> false);
    public static final int GLOWING_LIGHT_LEVEL = 6;

    public InkedBlock(AbstractBlock.Settings settings) {
        super(settings);
    }
    public InkedBlock(boolean glowing) {
        this(glowing ? DEFAULT_PROPERTIES.luminance(state -> GLOWING_LIGHT_LEVEL) : DEFAULT_PROPERTIES);
        this.glowing = glowing;

        SplatcraftBlocks.addToInkables(this);
    }

    public boolean isGlowing() {
        return this.glowing;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
        super.onPlaced(world, pos, state, entity, stack);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof InkedBlockEntity) {
            ((InkedBlockEntity) blockEntity).setSavedState(((InkedBlockEntity) blockEntity).getSavedState());
        }
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (SplatcraftGameRules.getBoolean(world, SplatcraftGameRules.INK_DECAY) && world.getBlockEntity(pos) instanceof InkedBlockEntity && random.nextFloat() <= 0.7f) {
            InkedBlock.clearInk(world, pos);
        }
    }

    public static boolean isTouchingLiquid(BlockView reader, BlockPos pos) {
        boolean flag = false;
        BlockPos.Mutable mutable = pos.mutableCopy();

        BlockState currentState = reader.getBlockState(pos);

        if (currentState.contains(Properties.WATERLOGGED) && currentState.get(Properties.WATERLOGGED)) {
            return true;
        }

        for (Direction direction : Direction.values()) {
            BlockState state = reader.getBlockState(mutable);
            if (direction != Direction.DOWN || causesClear(state)) {
                mutable.set(pos, direction);
                state = reader.getBlockState(mutable);
                if (causesClear(state) && !state.isSideSolidFullSquare(reader, pos, direction.getOpposite())) {
                    flag = true;
                    break;
                }
            }
        }

        return flag;
    }

    public static boolean causesClear(BlockState state) {
        return state.getFluidState().isIn(FluidTags.WATER);
    }

    protected static BlockState clearInk(WorldAccess world, BlockPos pos) {
        InkedBlockEntity inkedBlockEntity = (InkedBlockEntity) world.getBlockEntity(pos);
        if (inkedBlockEntity != null && inkedBlockEntity.hasSavedState()) {
            world.setBlockState(pos, inkedBlockEntity.getSavedState(), 3);

            if (inkedBlockEntity.isColored() && inkedBlockEntity.getSavedState().getBlock() instanceof AbstractInkableBlock) {
                ((World)world).setBlockEntity(pos, new InkedBlockEntity());
                if (world.getBlockEntity(pos) instanceof AbstractInkableBlockEntity) {
                    AbstractInkableBlockEntity inkableBlockEntity = (AbstractInkableBlockEntity) world.getBlockEntity(pos);
                    if (inkableBlockEntity != null) {
                        inkableBlockEntity.setInkColor(inkedBlockEntity.getInkColor());
                    }
                }
            }

            return inkedBlockEntity.getSavedState();
        }

        return world.getBlockState(pos);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return new InkedBlockEntity();
    }

    @Override
    public boolean canClimb() {
        return true;
    }

    @Override
    public boolean canSwim() {
        return true;
    }

    @Override
    public boolean canDamage() {
        return true;
    }

    @Override
    public boolean remoteColorChange(World world, BlockPos pos, InkColor newColor) {
        return false;
    }

    @Override
    public boolean remoteInkClear(World world, BlockPos pos) {
        if (world.getBlockEntity(pos) instanceof InkedBlockEntity) {
            return !clearInk(world, pos).equals(world.getBlockState(pos));
        }

        return false;
    }

    @Override
    public boolean countsTowardsTurf(World world, BlockPos pos) {
        return true;
    }

    @Override
    public boolean inkBlock(World world, BlockPos pos, InkColor color, float damage, InkBlockUtils.InkType inkType, boolean spawnParticles) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof InkedBlockEntity) {
            InkedBlockEntity inkedBlockEntity = (InkedBlockEntity) blockEntity;
            if (!inkedBlockEntity.getInkColor().equals(color)) {
                inkedBlockEntity.setInkColor(color);
                return true;
            } else {
                world.updateListeners(pos, world.getBlockState(pos), world.getBlockState(pos), 2);
            }
        }

        return false;
    }

    //
    // BASE MIMICKING
    //

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof InkedBlockEntity) {
            return ((InkedBlockEntity) blockEntity).getSavedState().getCollisionShape(world, pos, ctx);
        }

        return super.getCollisionShape(state, world, pos, ctx);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof InkedBlockEntity) {
            return ((InkedBlockEntity) blockEntity).getSavedState().getOutlineShape(world, pos, ctx);
        }

        return super.getOutlineShape(state, world, pos, ctx);
    }

    @Override
    public float calcBlockBreakingDelta(BlockState state, PlayerEntity player, BlockView world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof InkedBlockEntity) {
            return ((InkedBlockEntity) blockEntity).getSavedState().calcBlockBreakingDelta(player, world, pos);
        }

        return super.calcBlockBreakingDelta(state, player, world, pos);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof InkedBlockEntity) {
            Block block = world.getBlockState(pos).getBlock();
            InkBlockUtils.InkType inkType = InkBlockUtils.InkType.fromBlock((InkedBlock) block);
            InkColor inkColor = ColorUtils.getInkColor(blockEntity);

            BlockState savedState = ((InkedBlockEntity) blockEntity).getSavedState();
            ActionResult actionResult = savedState.getBlock().onUse(savedState, world, pos, player, hand, hit);

            if (actionResult.isAccepted()) {
                block = world.getBlockState(pos).getBlock();
                if (!(block instanceof InkedBlock)) {
                    InkBlockUtils.inkBlock(world, pos, inkColor, 0.0f, inkType);
                }
            }

            return actionResult;
        }

        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof InkedBlockEntity) {
            CompoundTag tag = new CompoundTag();
            CompoundTag blockEntityTag = blockEntity.toTag(new CompoundTag());
            tag.put("BlockEntityTag", blockEntityTag);

            return new ItemStack(this, 1, Optional.of(tag));
        }

        return super.getPickStack(world, pos, state);
    }

    @Override
    public boolean isTranslucent(BlockState state, BlockView world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof InkedBlockEntity) {
            BlockState savedState = ((InkedBlockEntity) blockEntity).getSavedState();
            return savedState.getBlock().isTranslucent(savedState, world, pos);
        }

        return super.isTranslucent(state, world, pos);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState blockState, Direction direction, BlockState newState, WorldAccess world, BlockPos blockPos, BlockPos posFrom) {
        BlockEntity blockEntity = world.getBlockEntity(blockPos);
        if (InkedBlock.isTouchingLiquid(world, blockPos)) {
            if (blockEntity instanceof InkedBlockEntity) {
                return clearInk(world, blockPos);
            }
        } else if (blockEntity instanceof InkedBlockEntity) {
            BlockState savedState = ((InkedBlockEntity) blockEntity).getSavedState();
            if (savedState != null && !savedState.getBlock().equals(this)) {
                ((InkedBlockEntity) blockEntity).setSavedState(savedState.getBlock().getStateForNeighborUpdate(savedState, direction, newState, world, blockPos, posFrom));
            }
        }

        return super.getStateForNeighborUpdate(blockState, direction, newState, world, blockPos, posFrom);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        try {
            return SplatcraftConfig.RENDER.inkedBlocksColorLayerIsTransparent.value ? BlockRenderType.INVISIBLE : BlockRenderType.MODEL;
        } catch (RuntimeException ignored) {
            return BlockRenderType.MODEL;
        }
    }
}
