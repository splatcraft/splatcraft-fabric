package net.splatcraft.block.entity;

import io.github.fablabsmc.fablabs.api.bannerpattern.v1.LoomPattern;
import io.github.fablabsmc.fablabs.api.bannerpattern.v1.LoomPatterns;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.splatcraft.Splatcraft;

public class SplatcraftBannerPatterns {
    public static final LoomPattern INKLING = register("inkling", true);
    public static final LoomPattern OCTOLING = register("octoling", true);

    private static LoomPattern register(String id, boolean isSpecial) {
        return Registry.register(LoomPatterns.REGISTRY, new Identifier(Splatcraft.MOD_ID, id), new LoomPattern(isSpecial));
    }
}
