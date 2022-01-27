package net.splatcraft.entity;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Item;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.registry.Registry;
import net.splatcraft.Splatcraft;
import net.splatcraft.item.InkableSpawnEggItem;
import net.splatcraft.item.SplatcraftItemGroups;

import static net.splatcraft.util.SplatcraftConstants.*;

public class SplatcraftEntities {
    public static final EntityType<InkSquidEntity> INK_SQUID = register(
        "ink_squid",
        FabricEntityTypeBuilder.createMob()
            .entityFactory(InkSquidEntity::new).spawnGroup(SpawnGroup.AMBIENT)
            .dimensions(SQUID_FORM_DIMENSIONS)
            .defaultAttributes(MobEntity::createMobAttributes),
        colors(0xF78F2E, 0xFEDC0C), InkableSpawnEggItem::new
    );

    public static final EntityType<InkProjectileEntity> INK_PROJECTILE = register(
        "ink_projectile",
        FabricEntityTypeBuilder.<InkProjectileEntity>create()
            .entityFactory(InkProjectileEntity::new).spawnGroup(SpawnGroup.MISC)
    );

    @SuppressWarnings("unchecked")
    private static <T extends Entity> EntityType<T> register(String id, FabricEntityTypeBuilder<T> entityType, Pair<Integer, Integer> colors, SpawnEggFactory eggFactory) {
        EntityType<T> builtEntityType = entityType.build();
        if (eggFactory != null) {
            Item.Settings settings = new FabricItemSettings().maxCount(64).group(SplatcraftItemGroups.ALL);
            Item item = eggFactory.apply((EntityType<? extends MobEntity>) builtEntityType, colors.getLeft(), colors.getRight(), settings);
            Registry.register(Registry.ITEM,  new Identifier(Splatcraft.MOD_ID, "%s_spawn_egg".formatted(id)), item);
        }
        return Registry.register(Registry.ENTITY_TYPE, new Identifier(Splatcraft.MOD_ID, id), builtEntityType);
    }

    private static <T extends Entity> EntityType<T> register(String id, FabricEntityTypeBuilder<T> entityType, Pair<Integer, Integer> colors) {
        return register(id, entityType, colors, SpawnEggItem::new);
    }

    private static <T extends Entity> EntityType<T> register(String id, FabricEntityTypeBuilder<T> entityType) {
        return register(id, entityType, null, null);
    }

    private static Pair<Integer, Integer> colors(int primary, int secondary) {
        return new Pair<>(primary, secondary);
    }

    @FunctionalInterface
    private interface SpawnEggFactory {
        SpawnEggItem apply(EntityType<? extends MobEntity> type, int primaryColor, int secondaryColor, Item.Settings settings);
    }
}
