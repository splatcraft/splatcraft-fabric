package net.splatcraft.entity;

import com.google.common.reflect.Reflection;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.registry.Registry;
import net.splatcraft.Splatcraft;
import net.splatcraft.entity.data.SplatcraftTrackedDataHandlers;
import net.splatcraft.item.SplatcraftItemGroups;

import static net.splatcraft.util.SplatcraftConstants.*;

public class SplatcraftEntities {
    static {
        //noinspection UnstableApiUsage
        Reflection.initialize(SplatcraftTrackedDataHandlers.class);
    }

    public static final EntityType<InkSquidEntity> INK_SQUID = register(
        "ink_squid",
        FabricEntityTypeBuilder.createMob()
            .entityFactory(InkSquidEntity::new).spawnGroup(SpawnGroup.AMBIENT)
            .dimensions(SQUID_FORM_DIMENSIONS)
            .defaultAttributes(MobEntity::createMobAttributes),
        colors(0xF78F2E, 0xFEDC0C)
    );

    @SuppressWarnings("unchecked")
    private static <T extends Entity> EntityType<T> register(String id, FabricEntityTypeBuilder<T> entityType, Pair<Integer, Integer> spawnEggColors) {
        EntityType<T> builtEntityType = entityType.build();

        if (spawnEggColors != null)
            Registry.register(
                Registry.ITEM,  new Identifier(Splatcraft.MOD_ID, "%s_spawn_egg".formatted(id)),
                new SpawnEggItem((EntityType<? extends MobEntity>) builtEntityType, spawnEggColors.getLeft(), spawnEggColors.getRight(), new FabricItemSettings().maxCount(64).group(SplatcraftItemGroups.ALL))
            );

        return Registry.register(Registry.ENTITY_TYPE, new Identifier(Splatcraft.MOD_ID, id), builtEntityType);
    }

    private static <T extends Entity> EntityType<T> register(String id, FabricEntityTypeBuilder<T> entityType) {
        return register(id, entityType, null);
    }

    private static Pair<Integer, Integer> colors(int primary, int secondary) {
        return new Pair<>(primary, secondary);
    }
}
