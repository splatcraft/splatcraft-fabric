package net.splatcraft.mixin;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.EntityShapeContext;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.splatcraft.block.InkPassableBlock;
import net.splatcraft.entity.access.InkEntityAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.class)
public class AbstractBlockMixin {
    /**
     * Removes collision from the block if ink passable and the entity is squid-like.
     */
    @Inject(method = "getCollisionShape", at = @At("HEAD"), cancellable = true)
    private void onGetCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx, CallbackInfoReturnable<VoxelShape> cir) {
        if (state.getBlock() instanceof InkPassableBlock) {
            if (ctx instanceof EntityShapeContext ectx && ectx.getEntity() instanceof InkEntityAccess access) {
                if (access.doesInkPassing()) cir.setReturnValue(VoxelShapes.empty());
            }
        }
    }
}
