package com.cibernet.splatcraft.block;

import com.cibernet.splatcraft.block.entity.AbstractInkableBlockEntity;
import com.cibernet.splatcraft.block.entity.InkedBlockEntity;
import com.cibernet.splatcraft.component.LazyPlayerDataComponent;
import com.cibernet.splatcraft.entity.damage.SplatcraftDamageSources;
import com.cibernet.splatcraft.handler.PlayerHandler;
import com.cibernet.splatcraft.inkcolor.ColorUtil;
import com.cibernet.splatcraft.inkcolor.InkColor;
import com.cibernet.splatcraft.inkcolor.InkColors;
import com.cibernet.splatcraft.inkcolor.InkType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractInkableBlock extends BlockWithEntity {
    protected AbstractInkableBlock(Settings settings) {
        super(settings);
    }

    public boolean canClimb() {
        return true;
    }
    public boolean canSwim() {
        return true;
    }
    public boolean canDamage() {
        return true;
    }
    public boolean massInkClear(World world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof InkedBlockEntity && ((InkedBlockEntity) blockEntity).isInked()) {
            return InkedBlock.clearInk(world, pos);
        }

        return false;
    }

    public boolean inkBlock(World world, BlockPos pos, InkColor color, InkType inkType, boolean spawnParticles) {
        return ColorUtil.setInkColor(world.getBlockEntity(pos), color);
    }

    public InkColor getInkColor(World world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        return blockEntity instanceof AbstractInkableBlockEntity ? ((AbstractInkableBlockEntity) blockEntity).getInkColor() : InkColors.NONE;
    }

    @Override
    public boolean onSyncedBlockEvent(BlockState state, World world, BlockPos pos, int type, int data) {
        world.updateListeners(pos, state, state, 2);
        if (world.isClient) {
            ((ClientWorld)world).scheduleBlockRenders(pos.getX(), pos.getY(), pos.getZ());
        }

        return super.onSyncedBlockEvent(state, world, pos, type, data);
    }

    @Override
    public void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity, float distance) {
        if (entity instanceof PlayerEntity player) {
            LazyPlayerDataComponent lazyData = LazyPlayerDataComponent.getComponent(player);
            if (lazyData.isSquid() && PlayerHandler.onEnemyInk(player) && player.world.getDifficulty() != Difficulty.PEACEFUL) {
                player.damage(SplatcraftDamageSources.ENEMY_INK, 2.0f);
            }
        }
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
        super.onPlaced(world, pos, state, entity, stack);

        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof AbstractInkableBlockEntity) {
            NbtCompound tag = stack.getTag();
            if (tag != null) {
                ((AbstractInkableBlockEntity) blockEntity).setInkColor(InkColor.fromNonNull(tag));
            }
        }
    }

    @Override
    @Environment(EnvType.CLIENT)
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state)  {
        ItemStack stack = super.getPickStack(world, pos, state);

        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof AbstractInkableBlockEntity) {
            ColorUtil.setInkColor(stack, ColorUtil.getInkColor(blockEntity));
        }

        return stack;
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState getStateForNeighborUpdate(BlockState blockState, Direction direction, BlockState newState, WorldAccess world, BlockPos blockPos, BlockPos posFrom) {
        if (InkedBlock.isTouchingLiquid(world, blockPos)) {
            BlockEntity blockEntity = world.getBlockEntity(blockPos);
            if (blockEntity instanceof AbstractInkableBlockEntity) {
                ColorUtil.setInkColor(blockEntity, InkColors.NONE);
            }
        }

        return super.getStateForNeighborUpdate(blockState, direction, newState, world, blockPos, posFrom);
    }

    @Nullable
    @Override
    public abstract BlockEntity createBlockEntity(BlockPos pos, BlockState state);

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
}
