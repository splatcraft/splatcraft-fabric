package net.splatcraft.api.tag;

import net.minecraft.block.entity.BannerPattern;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.splatcraft.api.Splatcraft;

public interface SplatcraftBannerPatternTags {
    TagKey<BannerPattern> INKLING_PATTERN_ITEM = register("pattern_item/inkling");
    TagKey<BannerPattern> OCTOLING_PATTERN_ITEM = register("pattern_item/octoling");

    private static TagKey<BannerPattern> register(String id) {
        return TagKey.of(Registry.BANNER_PATTERN_KEY, new Identifier(Splatcraft.MOD_ID, id));
    }
}
