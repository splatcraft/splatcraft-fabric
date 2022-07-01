package net.splatcraft.impl.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.util.registry.Registry;
import net.splatcraft.api.block.entity.SplatcraftBannerPatterns;
import net.splatcraft.api.tag.SplatcraftBannerPatternTags;

public class BannerPatternTagProvider extends FabricTagProvider<BannerPattern> {
    public BannerPatternTagProvider(FabricDataGenerator gen) {
        super(gen, Registry.BANNER_PATTERN);
    }

    @Override
    protected void generateTags() {
        this.getOrCreateTagBuilder(SplatcraftBannerPatternTags.INKLING_PATTERN_ITEM).add(SplatcraftBannerPatterns.INKLING);
        this.getOrCreateTagBuilder(SplatcraftBannerPatternTags.OCTOLING_PATTERN_ITEM).add(SplatcraftBannerPatterns.OCTOLING);
    }
}
