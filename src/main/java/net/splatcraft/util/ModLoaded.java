package net.splatcraft.util;

import net.fabricmc.loader.api.FabricLoader;

public class ModLoaded {
    private static final FabricLoader INSTANCE = FabricLoader.getInstance();

    public static final boolean SODIUM = INSTANCE.isModLoaded("sodium");
}
