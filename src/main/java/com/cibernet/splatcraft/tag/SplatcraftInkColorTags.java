package com.cibernet.splatcraft.tag;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.inkcolor.InkColor;
import com.google.common.collect.ImmutableMap;
import net.minecraft.tag.RequiredTagList;
import net.minecraft.tag.RequiredTagListRegistry;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagGroup;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SplatcraftInkColorTags {
    public static final Tag<InkColor> STARTER_COLORS = register("starter_colors");

    protected static final Map<Identifier, Tag<InkColor>> REQUIRED_TAGS_LIST = ImmutableMap.of(new Identifier(Splatcraft.MOD_ID, "starter_colors"), STARTER_COLORS);
    protected static final RequiredTagList<InkColor> REQUIRED_TAGS = RequiredTagListRegistry.register(new Identifier(Splatcraft.MOD_ID, "ink_colors"), tagManager -> TagGroup.create(REQUIRED_TAGS_LIST));

    public SplatcraftInkColorTags() {}

    private static Tag.Identified<InkColor> register(String id) {
        return Objects.requireNonNull(REQUIRED_TAGS).add(new Identifier(Splatcraft.MOD_ID, id).toString());
    }

    public static TagGroup<InkColor> getTagGroup() {
        return REQUIRED_TAGS.getGroup();
    }

    public static List<? extends Tag.Identified<InkColor>> getRequiredTags() {
        return REQUIRED_TAGS.getTags();
    }
}
