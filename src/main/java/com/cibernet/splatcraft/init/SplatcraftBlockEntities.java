package com.cibernet.splatcraft.init;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.block.entity.InkableBlockEntity;
import com.cibernet.splatcraft.block.entity.InkedBlockEntity;
import com.cibernet.splatcraft.block.entity.StageBarrierBlockEntity;
import com.mojang.datafixers.types.Type;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;

public class SplatcraftBlockEntities {
    public static final BlockEntityType<InkableBlockEntity> INKABLE = register(InkableBlockEntity.id, FabricBlockEntityTypeBuilder.create(InkableBlockEntity::new, SplatcraftBlocks.CANVAS, SplatcraftBlocks.INKWELL));
    public static final BlockEntityType<InkedBlockEntity> INKED_BLOCK = register(InkedBlockEntity.id, FabricBlockEntityTypeBuilder.create(InkedBlockEntity::new, SplatcraftBlocks.INKED_BLOCK, SplatcraftBlocks.GLOWING_INKED_BLOCK));

    public static final BlockEntityType<StageBarrierBlockEntity> STAGE_BARRIER = register(StageBarrierBlockEntity.id, FabricBlockEntityTypeBuilder.create(StageBarrierBlockEntity::new, SplatcraftBlocks.STAGE_BARRIER, SplatcraftBlocks.STAGE_VOID));

    private static <T extends BlockEntity> BlockEntityType<T> register(String id, FabricBlockEntityTypeBuilder<T> builder) {
        Identifier identifier = new Identifier(Splatcraft.MOD_ID, id);

        Type<?> type = Util.getChoiceType(TypeReferences.BLOCK_ENTITY, identifier.toString());
        return Registry.register(Registry.BLOCK_ENTITY_TYPE, identifier, builder.build(type));
    }
}
