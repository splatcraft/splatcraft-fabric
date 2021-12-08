package net.splatcraft.block.entity;

import com.mojang.datafixers.types.Type;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.splatcraft.Splatcraft;

public class SplatcraftBlockEntities {
    public static final BlockEntityType<InkableBlockEntity> INKABLE = register("inkable", (p, s) -> new InkableBlockEntity(SplatcraftBlockEntities.INKABLE, p, s));

    private static <T extends BlockEntity> BlockEntityType<T> register(String id, FabricBlockEntityTypeBuilder<T> builder) {
        Identifier identifier = new Identifier(Splatcraft.MOD_ID, id);
        Type<?> type = Util.getChoiceType(TypeReferences.BLOCK_ENTITY, identifier.toString());
        return Registry.register(Registry.BLOCK_ENTITY_TYPE, identifier, builder.build(type));
    }

    private static <T extends BlockEntity> BlockEntityType<T> register(String id, FabricBlockEntityTypeBuilder.Factory<? extends T> factory, Block... blocks) {
        return register(id, FabricBlockEntityTypeBuilder.create(factory, blocks));
    }
}
