package net.splatcraft.api.block;

import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;

public class CanvasBlock extends InkableBlock {
    public CanvasBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
}
