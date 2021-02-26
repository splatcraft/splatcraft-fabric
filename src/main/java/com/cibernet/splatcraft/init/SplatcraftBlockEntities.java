package com.cibernet.splatcraft.init;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.block.entity.CanvasBlockEntity;
import com.cibernet.splatcraft.block.entity.InkedBlockEntity;
import com.cibernet.splatcraft.block.entity.InkwellBlockEntity;
import com.cibernet.splatcraft.block.entity.StageBarrierBlockEntity;
import com.mojang.datafixers.types.Type;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;

public class SplatcraftBlockEntities {
    public static final BlockEntityType<InkwellBlockEntity> INKWELL = register(InkwellBlockEntity.id, BlockEntityType.Builder.create(InkwellBlockEntity::new, SplatcraftBlocks.INKWELL));
    public static final BlockEntityType<CanvasBlockEntity> CANVAS = register(CanvasBlockEntity.id, BlockEntityType.Builder.create(CanvasBlockEntity::new, SplatcraftBlocks.CANVAS));
    public static final BlockEntityType<InkedBlockEntity> INKED_BLOCK = register(InkedBlockEntity.id, BlockEntityType.Builder.create(InkedBlockEntity::new, SplatcraftBlocks.INKED_BLOCK));

    public static final BlockEntityType<StageBarrierBlockEntity> STAGE_BARRIER = register(StageBarrierBlockEntity.id, BlockEntityType.Builder.create(StageBarrierBlockEntity::new, SplatcraftBlocks.STAGE_BARRIER, SplatcraftBlocks.STAGE_VOID));

    public SplatcraftBlockEntities() {}

    private static <T extends BlockEntity> BlockEntityType<T> register(String id, BlockEntityType.Builder<T> builder) {
        Identifier identifier = new Identifier(Splatcraft.MOD_ID, id);

        Type<?> type = Util.getChoiceType(TypeReferences.BLOCK_ENTITY, identifier.toString());
        return Registry.register(Registry.BLOCK_ENTITY_TYPE, identifier, builder.build(type));
    }
}
