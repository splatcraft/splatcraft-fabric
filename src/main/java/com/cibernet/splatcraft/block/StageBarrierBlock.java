package com.cibernet.splatcraft.block;

import com.cibernet.splatcraft.block.entity.StageBarrierBlockEntity;
import com.cibernet.splatcraft.entity.damage.SplatcraftDamageSources;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
public class StageBarrierBlock extends BlockWithEntity {
    public static final String id = "stage_barrier";

    public static final VoxelShape SHAPE_DAMAGES_PLAYER = Block.createCuboidShape(0.1D, 0.1D, 0.1D, 15.9D, 15.9D, 15.9D);
    public static final VoxelShape SHAPE = Block.createCuboidShape(0.1D, 0.0D, 0.1D, 15.9D, 16.0D, 15.9D);

    public final boolean damagesEntity;

    public StageBarrierBlock(AbstractBlock.Settings settings, boolean damagesEntity) {
        super(settings);
        this.damagesEntity = damagesEntity;
    }

    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return new StageBarrierBlockEntity();
    }

    @Override
    public void onEntityLand(BlockView world, Entity entity) {
        super.onEntityLand(world, entity);
        this.touchWithNeighbors(world, entity, entity.getBlockPos(), new TouchStatus(TouchStatus.ActiveTimeChangeType.INCREMENT, true));
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        super.onEntityCollision(state, world, pos, entity);
        this.touch(world, entity, pos, new TouchStatus(TouchStatus.ActiveTimeChangeType.INCREMENT, true));
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        this.touchWithNeighbors(world, placer, pos, new TouchStatus(TouchStatus.ActiveTimeChangeType.SET, 100, false));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        this.touchWithNeighbors(world, player, pos, new TouchStatus(TouchStatus.ActiveTimeChangeType.SET, false));
        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBreak(world, pos, state, player);
        this.touchWithNeighbors(world, player, pos, new TouchStatus(TouchStatus.ActiveTimeChangeType.RESET, false));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return this.damagesEntity ? SHAPE_DAMAGES_PLAYER : SHAPE;
    }

    @Override
    public float calcBlockBreakingDelta(BlockState state, PlayerEntity player, BlockView world, BlockPos pos) {
        this.touchWithNeighbors(world, player, pos, new TouchStatus(TouchStatus.ActiveTimeChangeType.RESET, false));
        return super.calcBlockBreakingDelta(state, player, world, pos);
    }

    @Override
    public boolean onSyncedBlockEvent(BlockState state, World world, BlockPos pos, int type, int data) {
        if (world != null) {
            world.updateListeners(pos, state, state, 2);
        }

        return super.onSyncedBlockEvent(state, world, pos, type, data);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public boolean isTranslucent(BlockState state, BlockView reader, BlockPos pos) {
        return true;
    }

    @Override
    public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
        return 1.0F;
    }

    public void touchWithNeighbors(BlockView world, Entity entity, BlockPos pos, TouchStatus touchStatus) {
        this.touch(world, entity, pos, touchStatus);

        TouchStatus halfActiveTimeStatus = touchStatus.copyButHalf();
        this.touch(world, entity, pos.west(), halfActiveTimeStatus);
        this.touch(world, entity, pos.east(), halfActiveTimeStatus);
        this.touch(world, entity, pos.down(), halfActiveTimeStatus);
        this.touch(world, entity, pos.up(), halfActiveTimeStatus);
        this.touch(world, entity, pos.north(), halfActiveTimeStatus);
        this.touch(world, entity, pos.south(), halfActiveTimeStatus);
    }
    public void touch(BlockView world, Entity entity, BlockPos pos, TouchStatus touchStatus) {
        if (entity instanceof LivingEntity && touchStatus.dealDamage && this.damagesEntity) {
            entity.damage(SplatcraftDamageSources.VOID_DAMAGE, Float.MAX_VALUE);
        }

        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof StageBarrierBlockEntity) {
            switch (touchStatus.changeType) {
                case SET:
                    ((StageBarrierBlockEntity) blockEntity).setActiveTime(touchStatus.activeTime);
                case INCREMENT:
                    ((StageBarrierBlockEntity) blockEntity).incrementActiveTime();
                case RESET:
                    ((StageBarrierBlockEntity) blockEntity).resetActiveTime();
            }
        }
    }

    protected static class TouchStatus {
        protected ActiveTimeChangeType changeType;
        protected int activeTime;
        protected boolean dealDamage;

        protected TouchStatus(ActiveTimeChangeType changeType, int activeTime, boolean dealDamage) {
            this.changeType = changeType;
            this.activeTime = activeTime;
            this.dealDamage = dealDamage;
        }

        protected TouchStatus(ActiveTimeChangeType changeType, boolean dealDamage) {
            this.changeType = changeType;
            this.activeTime = 20;
            this.dealDamage = dealDamage;
        }

        protected TouchStatus copyButHalf() {
            return new TouchStatus(this.changeType, this.activeTime / 2, this.dealDamage);
        }

        public enum ActiveTimeChangeType {
            SET,
            INCREMENT,
            RESET
        }
    }
}
