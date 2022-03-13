package net.splatcraft.api.block.entity;

import com.mojang.datafixers.types.Type;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.splatcraft.api.Splatcraft;
import net.splatcraft.api.block.SplatcraftBlocks;

public interface SplatcraftBlockEntityType {
    BlockEntityType<InkableBlockEntity> INKABLE = register(
        "inkable", (p, s) -> new InkableBlockEntity(SplatcraftBlockEntityType.INKABLE, p, s),
        SplatcraftBlocks.CANVAS, SplatcraftBlocks.INKWELL
    );

    BlockEntityType<InkedBlockEntity> INKED_BLOCK = register(
        "inked_block", (p, s) -> new InkedBlockEntity(SplatcraftBlockEntityType.INKED_BLOCK, p, s),
        SplatcraftBlocks.INKED_BLOCK, SplatcraftBlocks.GLOWING_INKED_BLOCK
    );

    private static <T extends BlockEntity> BlockEntityType<T> register(String id, FabricBlockEntityTypeBuilder<T> builder) {
        Identifier identifier = new Identifier(Splatcraft.MOD_ID, id);
        Type<?> type = Util.getChoiceType(TypeReferences.BLOCK_ENTITY, identifier.toString());
        return Registry.register(Registry.BLOCK_ENTITY_TYPE, identifier, builder.build(type));
    }

    private static <T extends BlockEntity> BlockEntityType<T> register(String id, FabricBlockEntityTypeBuilder.Factory<? extends T> factory, Block... blocks) {
        return register(id, FabricBlockEntityTypeBuilder.create(factory, blocks));
    }
}
