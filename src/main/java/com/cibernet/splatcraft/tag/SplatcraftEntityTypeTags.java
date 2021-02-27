package com.cibernet.splatcraft.tag;

import com.cibernet.splatcraft.Splatcraft;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

public class SplatcraftEntityTypeTags {
    public static final Tag<EntityType<?>> PASSES_THROUGH_GAPS = register("passes_through_gaps");

    public SplatcraftEntityTypeTags() {}

    private static Tag<EntityType<?>> register(String id) {
        return TagRegistry.entityType(new Identifier(Splatcraft.MOD_ID, id));
    }
}
