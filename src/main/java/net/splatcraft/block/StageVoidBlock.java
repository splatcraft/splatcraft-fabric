package net.splatcraft.block;

import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.splatcraft.entity.damage.SplatcraftDamageSource;
import net.splatcraft.tag.SplatcraftEntityTypeTags;

@SuppressWarnings("deprecation")
public class StageVoidBlock extends Block {
    public StageVoidBlock(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        if (ctx instanceof EntityShapeContext entityCtx) {
            Entity entity = entityCtx.getEntity();
            if (entity != null && SplatcraftEntityTypeTags.KILLED_BY_STAGE_VOID.contains(entity.getType())) {
                if (entity instanceof LivingEntity) entity.damage(SplatcraftDamageSource.OUT_OF_ARENA, Float.MAX_VALUE);
                    else entity.kill();
            }
        }

        return super.getCollisionShape(state, world, pos, ctx);
    }

    @Override
    public boolean isTranslucent(BlockState state, BlockView world, BlockPos pos) {
        return true;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    @Override
    public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
        return 1.0f;
    }
}
