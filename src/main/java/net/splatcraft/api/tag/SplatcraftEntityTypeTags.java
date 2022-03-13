package net.splatcraft.api.tag;

import net.minecraft.entity.EntityType;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.splatcraft.api.Splatcraft;

public interface SplatcraftEntityTypeTags {
    TagKey<EntityType<?>> INK_PASSABLES = register("ink_passables");
    TagKey<EntityType<?>> HURT_BY_WATER = register("hurt_by_water");
    TagKey<EntityType<?>> HURT_BY_ENEMY_INK = register("hurt_by_enemy_ink");
    TagKey<EntityType<?>> KILLED_BY_STAGE_VOID = register("killed_by_stage_void");

    private static TagKey<EntityType<?>> register(String id) {
        return TagKey.of(Registry.ENTITY_TYPE_KEY, new Identifier(Splatcraft.MOD_ID, id));
    }
}
