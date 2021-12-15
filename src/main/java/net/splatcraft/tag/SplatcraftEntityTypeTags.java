package net.splatcraft.tag;

import net.fabricmc.fabric.api.tag.TagFactory;
import net.minecraft.entity.EntityType;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.splatcraft.Splatcraft;

public class SplatcraftEntityTypeTags {
    public static final Tag.Identified<EntityType<?>> INK_PASSABLES = register("ink_passables");
    public static final Tag.Identified<EntityType<?>> HURT_BY_WATER = register("hurt_by_water");
    public static final Tag.Identified<EntityType<?>> HURT_BY_ENEMY_INK = register("hurt_by_enemy_ink");
    public static final Tag.Identified<EntityType<?>> KILLED_BY_STAGE_VOID = register("killed_by_stage_void");

    private static Tag.Identified<EntityType<?>> register(String id) {
        return TagFactory.ENTITY_TYPE.create(new Identifier(Splatcraft.MOD_ID, id));
    }
}
