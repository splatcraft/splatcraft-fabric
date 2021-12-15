package net.splatcraft.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.TransparentBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

@SuppressWarnings("deprecation")
public class GrateBlockBlock extends TransparentBlock implements InkPassableBlock {
    public GrateBlockBlock(Settings settings) {
        super(settings);
    }

    @Override
    public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
        return 1.0f;
    }
}
