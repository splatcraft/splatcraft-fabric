package com.cibernet.splatcraft.block;

import com.cibernet.splatcraft.entity.EntityAccessShapeContext;
import com.cibernet.splatcraft.inkcolor.InkBlockUtils;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public abstract class AbstractPassableBlock extends Block {
    public AbstractPassableBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        Entity entity = ((EntityAccessShapeContext) ctx).splatcraft_getEntity();
        return entity != null && InkBlockUtils.entityPassesThroughGaps(entity)
            ? VoxelShapes.empty()
            : super.getCollisionShape(state, world, pos, ctx);
    }
}
