package net.splatcraft.block.entity;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.moddingplayground.frame.api.banner.FrameBannerPattern;
import net.moddingplayground.frame.api.banner.FrameBannerPatterns;
import net.splatcraft.Splatcraft;

public class SplatcraftBannerPatterns {
    public static final FrameBannerPattern INKLING = register("inkling", true);
    public static final FrameBannerPattern OCTOLING = register("octoling", true);

    private static FrameBannerPattern register(String id, boolean isSpecial) {
        return Registry.register(FrameBannerPatterns.REGISTRY, new Identifier(Splatcraft.MOD_ID, id), new FrameBannerPattern(isSpecial));
    }
}
