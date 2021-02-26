package com.cibernet.splatcraft.block;

import com.cibernet.splatcraft.entity.EntityAccessShapeContext;
import com.cibernet.splatcraft.init.SplatcraftComponents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
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
    public VoxelShape getCollisionShape(BlockState state, BlockView worldIn, BlockPos pos, ShapeContext shapeContext) {
        Entity entity = ((EntityAccessShapeContext) shapeContext).getEntity();
        return entity instanceof PlayerEntity && SplatcraftComponents.PLAYER_DATA.get(entity).isSquid()
            ? VoxelShapes.empty()
            : super.getCollisionShape(state, worldIn, pos, shapeContext);
    }
}
