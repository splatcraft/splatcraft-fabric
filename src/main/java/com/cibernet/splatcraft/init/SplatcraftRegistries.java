package com.cibernet.splatcraft.init;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.inkcolor.InkColor;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;

public class SplatcraftRegistries {
    /**
     * Splatcraft's built-in ink colors.
     */
    public static final Registry<InkColor> INK_COLORS = createSimple(InkColor.class, "ink_colors");

    private static <T> SimpleRegistry<T> createSimple(Class<T> type, String id) {
        return FabricRegistryBuilder.createSimple(type, new Identifier(Splatcraft.MOD_ID, id)).buildAndRegister();
    }
}
