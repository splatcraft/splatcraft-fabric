package net.splatcraft.api.entity;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Item;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.splatcraft.api.Splatcraft;
import net.splatcraft.api.item.InkableSpawnEggItem;
import net.splatcraft.api.item.SplatcraftItemGroups;

import static net.splatcraft.api.util.SplatcraftConstants.*;

public interface SplatcraftEntityType {
    EntityType<InkSquidEntity> INK_SQUID = register("ink_squid",
        FabricEntityTypeBuilder.createMob()
            .entityFactory(InkSquidEntity::new).spawnGroup(SpawnGroup.AMBIENT)
            .dimensions(SQUID_FORM_DIMENSIONS)
            .defaultAttributes(MobEntity::createMobAttributes),
        0xF78F2E, 0xFEDC0C, InkableSpawnEggItem::new
    );

    EntityType<InkProjectileEntity> INK_PROJECTILE = register("ink_projectile",
        FabricEntityTypeBuilder.<InkProjectileEntity>create()
                               .entityFactory(InkProjectileEntity::new)
                               .spawnGroup(SpawnGroup.MISC)
    );

    @SuppressWarnings("unchecked")
    private static <T extends Entity> EntityType<T> register(String id, FabricEntityTypeBuilder<T> builder, int primary, int secondary, SpawnEggFactory egg) {
        EntityType<T> type = builder.build();
        if (egg != null) {
            Item.Settings settings = new FabricItemSettings().maxCount(64).group(SplatcraftItemGroups.ALL);
            Item item = egg.apply((EntityType<? extends MobEntity>) type, primary, secondary, settings);
            Registry.register(Registry.ITEM,  new Identifier(Splatcraft.MOD_ID, "%s_spawn_egg".formatted(id)), item);
        }
        return Registry.register(Registry.ENTITY_TYPE, new Identifier(Splatcraft.MOD_ID, id), type);
    }

    private static <T extends Entity> EntityType<T> register(String id, FabricEntityTypeBuilder<T> builder, int primary, int secondary) {
        return register(id, builder, primary, secondary, SpawnEggItem::new);
    }

    private static <T extends Entity> EntityType<T> register(String id, FabricEntityTypeBuilder<T> builder) {
        return register(id, builder, 0, 0, null);
    }

    @FunctionalInterface interface SpawnEggFactory { SpawnEggItem apply(EntityType<? extends MobEntity> type, int primary, int secondary, Item.Settings settings); }
}
