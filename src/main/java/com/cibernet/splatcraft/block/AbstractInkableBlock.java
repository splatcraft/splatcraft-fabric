package com.cibernet.splatcraft.block;

import com.cibernet.splatcraft.block.entity.AbstractInkableBlockEntity;
import com.cibernet.splatcraft.component.PlayerDataComponent;
import com.cibernet.splatcraft.component.SplatcraftComponents;
import com.cibernet.splatcraft.entity.damage.SplatcraftDamageSources;
import com.cibernet.splatcraft.inkcolor.ColorUtils;
import com.cibernet.splatcraft.inkcolor.InkBlockUtils;
import com.cibernet.splatcraft.inkcolor.InkColor;
import com.cibernet.splatcraft.inkcolor.InkColors;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public abstract class AbstractInkableBlock extends BlockWithEntity {
    public static final String id = "inkable";

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
    public abstract boolean remoteInkClear(World world, BlockPos pos);
    public abstract boolean countsTowardsTurf(World world, BlockPos pos);

    public boolean inkBlock(World world, BlockPos pos, InkColor color, float damage, InkBlockUtils.InkType inkType, boolean spawnParticles) {
        return ColorUtils.setInkColor(world.getBlockEntity(pos), color);
    }

    public InkColor getColor(World world, BlockPos pos) {
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
    public void onLandedUpon(World world, BlockPos pos, Entity entity, float distance) {
        if (entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            PlayerDataComponent data = SplatcraftComponents.PLAYER_DATA.get(player);
            if (data.isSquid() && InkBlockUtils.onEnemyInk(player) && player.world.getDifficulty() != Difficulty.PEACEFUL) {
                player.damage(SplatcraftDamageSources.ENEMY_INK, 2.0f);
            }
        }
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (stack.getTag() != null && blockEntity instanceof AbstractInkableBlockEntity) {
            ((AbstractInkableBlockEntity) blockEntity).setInkColor(ColorUtils.getInkColor(stack));
        }

        super.onPlaced(world, pos, state, entity, stack);
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state)  {
        ItemStack stack = super.getPickStack(world, pos, state);

        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof AbstractInkableBlockEntity) {
            ColorUtils.setInkColor(stack, ColorUtils.getInkColor(blockEntity));
        }

        return stack;
    }

    public boolean remoteColorChange(World world, BlockPos pos, InkColor newColor) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof AbstractInkableBlockEntity && !((AbstractInkableBlockEntity) blockEntity).getInkColor().equals(newColor)) {
            ColorUtils.setInkColor(blockEntity, newColor);
            return true;
        }

        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState getStateForNeighborUpdate(BlockState blockState, Direction direction, BlockState newState, WorldAccess world, BlockPos blockPos, BlockPos posFrom) {
        if (InkedBlock.isTouchingLiquid(world, blockPos)) {
            BlockEntity blockEntity = world.getBlockEntity(blockPos);
            if (blockEntity instanceof AbstractInkableBlockEntity) {
                ColorUtils.setInkColor(blockEntity, InkColors.NONE);
            }
        }

        return super.getStateForNeighborUpdate(blockState, direction, newState, world, blockPos, posFrom);
    }

    @Override
    public abstract BlockEntity createBlockEntity(BlockView world);

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
}
