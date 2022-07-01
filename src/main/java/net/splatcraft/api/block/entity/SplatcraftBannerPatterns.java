package net.splatcraft.api.block.entity;

import net.minecraft.block.entity.BannerPattern;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.splatcraft.api.Splatcraft;

public interface SplatcraftBannerPatterns {
    BannerPattern INKLING = register("inkling");
    BannerPattern OCTOLING = register("octoling");

    private static BannerPattern register(String id) {
        Identifier identifier = new Identifier(Splatcraft.MOD_ID, id);
        return Registry.register(Registry.BANNER_PATTERN, identifier, new BannerPattern(identifier.toString()));
    }
}
