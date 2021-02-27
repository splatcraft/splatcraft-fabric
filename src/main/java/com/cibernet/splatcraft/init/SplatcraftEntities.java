package com.cibernet.splatcraft.init;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.entity.InkSquidEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class SplatcraftEntities {
    public static final EntityType<InkSquidEntity> INK_SQUID = register(InkSquidEntity.id, FabricEntityTypeBuilder.createLiving()
        .entityFactory(InkSquidEntity::new)
        .defaultAttributes(() -> LivingEntity.createLivingAttributes().add(EntityAttributes.GENERIC_FOLLOW_RANGE, 16.0D))
        .dimensions(EntityDimensions.fixed(0.6F, 0.6F))
        .spawnGroup(SpawnGroup.AMBIENT)
        , new int[]{ 0xe87422, 0xf5d7a6 }
    );

    public SplatcraftEntities() {}

    private static <T extends Entity> EntityType<T> register(String id, FabricEntityTypeBuilder<T> entityType, int[] spawnEggColors) {
        EntityType<T> builtEntityType = entityType.build();

        if (spawnEggColors != null) {
            Registry.register(Registry.ITEM, new Identifier(Splatcraft.MOD_ID, id + "_spawn_egg"), new SpawnEggItem(builtEntityType, spawnEggColors[0], spawnEggColors[1], new Item.Settings().maxCount(64).group(Splatcraft.ItemGroups.ITEM_GROUP)));
        }

        return Registry.register(Registry.ENTITY_TYPE, new Identifier(Splatcraft.MOD_ID, id), builtEntityType);
    }

    public static Identifier texture(String path) {
        return Splatcraft.texture("entity/" + path);
    }
}
