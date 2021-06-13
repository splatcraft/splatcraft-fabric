package com.cibernet.splatcraft.block;

import com.cibernet.splatcraft.block.entity.InkedBlockEntity;
import com.cibernet.splatcraft.client.config.SplatcraftConfig;
import com.cibernet.splatcraft.init.SplatcraftBlocks;
import com.cibernet.splatcraft.init.SplatcraftGameRules;
import com.cibernet.splatcraft.init.SplatcraftParticles;
import com.cibernet.splatcraft.init.SplatcraftSoundEvents;
import com.cibernet.splatcraft.inkcolor.ColorUtil;
import com.cibernet.splatcraft.inkcolor.InkColor;
import com.cibernet.splatcraft.inkcolor.InkType;
import com.cibernet.splatcraft.util.InkBlockUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
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
import org.jetbrains.annotations.Nullable;

import java.util.Random;

@SuppressWarnings("deprecation")
public class InkedBlock extends AbstractInkableBlock {
    public static final String id = "inked_block";

    protected boolean glowing;

    public static final AbstractBlock.Settings DEFAULT_PROPERTIES = FabricBlockSettings.of(Material.ORGANIC_PRODUCT, MapColor.TERRACOTTA_BLACK)
        .sounds(BlockSoundGroup.SLIME)
        .nonOpaque().ticksRandomly()
        .suffocates((state, world, pos) -> false)
        .solidBlock((state, world, pos) -> true);
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
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof InkedBlockEntity inkedBlockEntity) {
            ItemStack stack = player.getStackInHand(hand);
            Item item = stack.getItem();
            if (inkedBlockEntity.hasBaseInkColor()) {
                if (item instanceof AxeItem) {
                    inkedBlockEntity.setBaseInkColor(null);
                    InkedBlock.clearInk(world, pos);

                    if (!player.getAbilities().creativeMode) {
                        stack.damage(1, (LivingEntity)player, (p) -> p.sendToolBreakStatus(hand));
                    }

                    spawnParticles(SplatcraftParticles.WAX_INKED_BLOCK_OFF, world, state, pos);
                    if (!world.isClient) {
                        world.playSoundFromEntity(null, player, SplatcraftSoundEvents.BLOCK_INKED_BLOCK_WAX_OFF, SoundCategory.BLOCKS, 1.0f, 1.0f);
                    }

                    return ActionResult.SUCCESS;
                }
            } else if (item == Items.HONEYCOMB && inkedBlockEntity.setBaseInkColor((inkedBlockEntity.getInkColor()))) {
                if (!player.getAbilities().creativeMode) {
                    stack.decrement(1);
                }

                spawnParticles(SplatcraftParticles.WAX_INKED_BLOCK_ON, world, state, pos);
                if (!world.isClient) {
                    world.playSoundFromEntity(null, player, SplatcraftSoundEvents.BLOCK_INKED_BLOCK_WAX_ON, SoundCategory.BLOCKS, 1.0f, 1.0f);
                }

                return ActionResult.SUCCESS;
            }

            return this.onUseForSavedState(blockEntity, state, world, pos, player, hand, hit);
        }

        return super.onUse(state, world, pos, player, hand, hit);
    }

    public void spawnParticles(DefaultParticleType particleType, World world, BlockState state, BlockPos pos) {
        Random random = world.random;
        for (int i = 0; i < 40; i++) {
            Direction direction = Direction.random(random);
            BlockPos blockPos = pos.offset(direction);
            if (!state.isOpaque() || !state.isSideSolidFullSquare(world, blockPos, direction.getOpposite())) {
                double x = direction.getOffsetX() == 0 ? random.nextDouble() : 0.5D + (double) direction.getOffsetX() * 0.6D;
                double y = direction.getOffsetY() == 0 ? random.nextDouble() : 0.5D + (double) direction.getOffsetY() * 0.6D;
                double z = direction.getOffsetZ() == 0 ? random.nextDouble() : 0.5D + (double) direction.getOffsetZ() * 0.6D;
                world.addParticle(particleType, (double) pos.getX() + x, (double) pos.getY() + y, (double) pos.getZ() + z, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        this.checkAndClearInk(world, pos, random);
        super.randomTick(state, world, pos, random);
    }

    public void checkAndClearInk(World world, BlockPos pos, Random random) {
        if (random.nextFloat() <= 0.7f && SplatcraftGameRules.getBoolean(world, SplatcraftGameRules.INK_DECAY)) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof InkedBlockEntity inkedBlockEntity) {
                if (inkedBlockEntity.getSavedState().isAir() || inkedBlockEntity.isInked()) {
                    InkedBlock.clearInk(world, pos);
                }
            }
        }
    }

    public static boolean isTouchingLiquid(BlockView reader, BlockPos pos) {
        boolean isTouchingLiquid = false;
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
                    isTouchingLiquid = true;
                    break;
                }
            }
        }

        return isTouchingLiquid;
    }

    public static boolean causesClear(BlockState state) {
        return state.getFluidState().isIn(FluidTags.WATER);
    }

    protected static boolean clearInk(WorldAccess world, BlockPos pos) {
        InkedBlockEntity inkedBlockEntity = (InkedBlockEntity) world.getBlockEntity(pos);
        if (inkedBlockEntity != null && inkedBlockEntity.hasSavedState()) {
            if (inkedBlockEntity.hasBaseInkColor()) {
                InkColor baseInkColor = inkedBlockEntity.getBaseInkColor();
                if (baseInkColor != null) {
                    inkedBlockEntity.setInkColor(baseInkColor);
                }
            } else {
                world.setBlockState(pos, inkedBlockEntity.getSavedState(), 3);
            }

            return true;
        }

        return false;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new InkedBlockEntity(pos, state);
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
    public boolean inkBlock(World world, BlockPos pos, InkColor color, InkType inkType, boolean spawnParticles) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof InkedBlockEntity inkedBlockEntity) {
            if (!inkedBlockEntity.getInkColor().equals(color)) {
                inkedBlockEntity.setInkColor(color);
                return true;
            } else {
                world.updateListeners(pos, world.getBlockState(pos), world.getBlockState(pos), 2);
            }
        }

        return false;
    }

    /*
     * BASE MIMICKING
     */

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
    public VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof InkedBlockEntity) {
            return ((InkedBlockEntity) blockEntity).getSavedState().getCullingShape(world, pos);
        }

        return super.getCullingShape(state, world, pos);
    }

    @Override
    public float calcBlockBreakingDelta(BlockState state, PlayerEntity player, BlockView world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof InkedBlockEntity) {
            return ((InkedBlockEntity) blockEntity).getSavedState().calcBlockBreakingDelta(player, world, pos);
        }

        return super.calcBlockBreakingDelta(state, player, world, pos);
    }

    protected ActionResult onUseForSavedState(BlockEntity blockEntity, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        Block block = state.getBlock();
        InkType inkTypePre = InkType.from((InkedBlock) block);
        InkColor inkColorPre = ColorUtil.getInkColor(blockEntity);

        BlockState savedState = ((InkedBlockEntity) blockEntity).getSavedState();
        ActionResult actionResultForSavedState = savedState.getBlock().onUse(savedState, world, pos, player, hand, hit);

        if (actionResultForSavedState.isAccepted()) {
            block = world.getBlockState(pos).getBlock();
            if (!(block instanceof InkedBlock)) {
                InkBlockUtil.inkBlock(world, pos, inkColorPre, inkTypePre);
            }
        }

        return actionResultForSavedState;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof InkedBlockEntity) {
            ItemStack stack = super.getPickStack(world, pos, state);
            ((InkedBlockEntity) blockEntity).toClientTag(stack.getOrCreateSubTag("BlockEntityTag"));

            return stack;
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
    public BlockState getStateForNeighborUpdate(BlockState blockState, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof InkedBlockEntity) {
            if (InkedBlock.isTouchingLiquid(world, pos)) {
                InkedBlock.clearInk(world, pos);
                return world.getBlockState(pos);
            } else {
                BlockState savedState = ((InkedBlockEntity) blockEntity).getSavedState();
                if (savedState != null && !savedState.getBlock().equals(this)) {
                    ((InkedBlockEntity) blockEntity).setSavedState(savedState.getBlock().getStateForNeighborUpdate(savedState, direction, newState, world, pos, posFrom));
                }
            }
        }

        return super.getStateForNeighborUpdate(blockState, direction, newState, world, pos, posFrom);
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
