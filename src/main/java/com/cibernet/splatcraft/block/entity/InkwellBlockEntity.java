package com.cibernet.splatcraft.block.entity;

import com.cibernet.splatcraft.block.InkwellBlock;
import com.cibernet.splatcraft.init.SplatcraftBlockEntities;
import net.minecraft.util.registry.Registry;

public class InkwellBlockEntity extends AbstractColorableBlockEntity {
    public static final String id = InkwellBlock.id;

    public InkwellBlockEntity() {
        super(SplatcraftBlockEntities.INKWELL);
    }

    @Override
    public int getRawId() {
        return Registry.BLOCK_ENTITY_TYPE.getRawId(SplatcraftBlockEntities.INKWELL);
    }
}
