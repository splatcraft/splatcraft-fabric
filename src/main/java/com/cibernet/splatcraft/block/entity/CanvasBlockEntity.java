package com.cibernet.splatcraft.block.entity;

import com.cibernet.splatcraft.block.CanvasBlock;
import com.cibernet.splatcraft.init.SplatcraftBlockEntities;
import net.minecraft.util.registry.Registry;

public class CanvasBlockEntity extends AbstractColorableBlockEntity {
    public static final String id = CanvasBlock.id;

    public CanvasBlockEntity() {
        super(SplatcraftBlockEntities.CANVAS);
    }

    @Override
    public int getRawId() {
        return Registry.BLOCK_ENTITY_TYPE.getRawId(SplatcraftBlockEntities.CANVAS);
    }
}
