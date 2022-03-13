package net.splatcraft.api.block.entity;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.moddingplayground.frame.api.bannerpatterns.v0.FrameBannerPattern;
import net.moddingplayground.frame.api.bannerpatterns.v0.FrameBannerPatterns;
import net.splatcraft.api.Splatcraft;

public interface SplatcraftBannerPatterns {
    FrameBannerPattern INKLING = register("inkling", true);
    FrameBannerPattern OCTOLING = register("octoling", true);

    private static FrameBannerPattern register(String id, boolean isSpecial) {
        return Registry.register(FrameBannerPatterns.REGISTRY, new Identifier(Splatcraft.MOD_ID, id), new FrameBannerPattern(isSpecial));
    }
}
