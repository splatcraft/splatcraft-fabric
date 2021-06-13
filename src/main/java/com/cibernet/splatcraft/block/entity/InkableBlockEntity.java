package com.cibernet.splatcraft.block.entity;

import com.cibernet.splatcraft.init.SplatcraftBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class InkableBlockEntity extends AbstractInkableBlockEntity {
    public InkableBlockEntity(BlockPos pos, BlockState state) {
        super(SplatcraftBlockEntities.INKABLE, pos, state);
    }
}
