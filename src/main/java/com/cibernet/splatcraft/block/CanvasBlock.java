package com.cibernet.splatcraft.block;

import com.cibernet.splatcraft.block.entity.CanvasBlockEntity;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class CanvasBlock extends AbstractInkableBlock {
    public static final String id = "canvas";

    public CanvasBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return new CanvasBlockEntity();
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
    public boolean remoteInkClear(World world, BlockPos pos) {
        return false;
    }

    @Override
    public boolean countsTowardsTurf(World world, BlockPos pos) {
        return false;
    }
}
