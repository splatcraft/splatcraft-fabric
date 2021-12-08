package net.splatcraft.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.splatcraft.block.entity.SplatcraftBlockEntities;

public class InkableBlock extends BlockWithEntity {
    public InkableBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return SplatcraftBlockEntities.INKABLE.instantiate(pos, state);
    }
}
