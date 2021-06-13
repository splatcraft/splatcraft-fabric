package com.cibernet.splatcraft.block;

import com.cibernet.splatcraft.block.entity.InkableBlockEntity;
import com.cibernet.splatcraft.init.SplatcraftBlocks;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class CanvasBlock extends AbstractInkableBlock {
    public static final String id = "canvas";

    public CanvasBlock(AbstractBlock.Settings settings) {
        super(settings);
        SplatcraftBlocks.addToInkables(this);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new InkableBlockEntity(pos, state);
    }

    @Override
    public boolean canClimb() {
        return true;
    }

    @Override
    public boolean canSwim() {
        return true;
    }

    @Override
    public boolean canDamage() {
        return true;
    }

    @Override
    public boolean massInkClear(World world, BlockPos pos) {
        return false;
    }
}
