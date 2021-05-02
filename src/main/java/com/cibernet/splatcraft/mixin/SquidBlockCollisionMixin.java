package com.cibernet.splatcraft.mixin;

import com.cibernet.splatcraft.entity.EntityAccessShapeContext;
import com.cibernet.splatcraft.tag.SplatcraftBlockTags;
import com.cibernet.splatcraft.util.InkBlockUtil;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ AbstractBlock.class, HorizontalConnectingBlock.class })
public class SquidBlockCollisionMixin {
    @Inject(method = "getCollisionShape", at = @At("HEAD"), cancellable = true)
    public void getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx, CallbackInfoReturnable<VoxelShape> cir) {
        Block $this = Block.class.cast(this);
        if (SplatcraftBlockTags.INK_SQUID_PASSTHROUGHABLES.contains($this)) {
            Entity entity = ((EntityAccessShapeContext) ctx).splatcraft_getEntity();
            if (entity != null && InkBlockUtil.entityPassesThroughGaps(entity)) {
                cir.setReturnValue(VoxelShapes.empty());
            }
        }
    }
}
