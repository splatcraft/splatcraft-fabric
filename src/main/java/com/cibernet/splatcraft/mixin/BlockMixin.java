package com.cibernet.splatcraft.mixin;

import com.cibernet.splatcraft.block.entity.InkedBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collections;
import java.util.List;

@Mixin(Block.class)
public class BlockMixin {
    @Inject(method = "getDroppedStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntity;)Ljava/util/List;", at = @At("HEAD"), cancellable = true)
    private static void getDroppedStacks(BlockState state, ServerWorld world, BlockPos pos, @Nullable BlockEntity blockEntity, CallbackInfoReturnable<List<ItemStack>> cir) {
        if (blockEntity instanceof InkedBlockEntity) {
            cir.setReturnValue(splatcraft_getDroppedStacks(state, world, pos, blockEntity, null, null));
        }
    }
    @Inject(method = "getDroppedStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/item/ItemStack;)Ljava/util/List;", at = @At("HEAD"), cancellable = true)
    private static void getDroppedStacks(BlockState state, ServerWorld world, BlockPos pos, @Nullable BlockEntity blockEntity, Entity entity, ItemStack stack, CallbackInfoReturnable<List<ItemStack>> cir) {
        if (blockEntity instanceof InkedBlockEntity) {
            cir.setReturnValue(splatcraft_getDroppedStacks(state, world, pos, blockEntity, entity, stack));
        }
    }

    /**
     * Replaces inked block dropped stacks with their saved state's.
     */
    private static List<ItemStack> splatcraft_getDroppedStacks(BlockState state, ServerWorld world, BlockPos pos, @NotNull BlockEntity blockEntity, @Nullable Entity entity, @Nullable ItemStack stack) {
        LootContext.Builder base = new LootContext.Builder(world).random(world.random).parameter(LootContextParameters.ORIGIN, Vec3d.ofCenter(pos));
        LootContext.Builder builder = entity == null
            ? base.parameter(LootContextParameters.TOOL, ItemStack.EMPTY)
                .optionalParameter(LootContextParameters.BLOCK_ENTITY, blockEntity)
            : base.parameter(LootContextParameters.TOOL, stack)
                .optionalParameter(LootContextParameters.THIS_ENTITY, entity)
                .optionalParameter(LootContextParameters.BLOCK_ENTITY, blockEntity);

        Identifier identifier = ((InkedBlockEntity) blockEntity).getSavedState().getBlock().getLootTableId();
        if (identifier == LootTables.EMPTY) {
            return Collections.emptyList();
        } else {
            LootContext lootContext = builder.parameter(LootContextParameters.BLOCK_STATE, state).build(LootContextTypes.BLOCK);
            ServerWorld serverWorld = lootContext.getWorld();
            LootTable lootTable = serverWorld.getServer().getLootManager().getTable(identifier);
            return lootTable.generateLoot(lootContext);
        }
    }
}
