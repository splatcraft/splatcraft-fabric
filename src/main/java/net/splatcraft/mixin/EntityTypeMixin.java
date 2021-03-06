package net.splatcraft.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.splatcraft.api.inkcolor.Inkable;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityType.class)
public class EntityTypeMixin<T extends Entity> {
    /**
     * Sets the ink color of the spawned entity to the ink color of the targeted block.
     */
    @Inject(method = "create(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/nbt/NbtCompound;Lnet/minecraft/text/Text;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/SpawnReason;ZZ)Lnet/minecraft/entity/Entity;", at = @At("RETURN"))
    private void onSpawn(ServerWorld world, @Nullable NbtCompound nbt, @Nullable Text name, @Nullable PlayerEntity player, BlockPos spawnPos, SpawnReason spawnReason, boolean alignPosition, boolean invertY, CallbackInfoReturnable<@Nullable T> cir) {
        Entity ret = cir.getReturnValue();
        if (ret instanceof Inkable entity && player != null) {
            HitResult result = player.raycast(8.0d, 0.0f, false);
            if (result instanceof BlockHitResult hit) {
                BlockPos hpos = new BlockPos(hit.getPos());
                Direction side = hit.getSide();
                BlockPos pos = side != Direction.DOWN && side != Direction.NORTH ? hpos.offset(side, -1) : hpos;
                if (world.getBlockEntity(pos) instanceof Inkable inkable) inkable.copyInkableTo(entity);
            }
        }
    }
}
