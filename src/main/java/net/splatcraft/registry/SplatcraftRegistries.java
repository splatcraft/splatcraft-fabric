package net.splatcraft.registry;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;
import net.splatcraft.Splatcraft;
import net.splatcraft.inkcolor.InkColor;

import static net.splatcraft.util.SplatcraftConstants.*;

public class SplatcraftRegistries {
    /**
     * Splatcraft's built-in ink colors.
     */
    public static final Registry<InkColor> INK_COLOR = defaulted("ink_color", InkColor.class, STRING_DEFAULT_INK_COLOR);

    private static <T> SimpleRegistry<T> simple(String id, Class<T> type) {
        return FabricRegistryBuilder.createSimple(type, id(id)).buildAndRegister();
    }

    private static <T> DefaultedRegistry<T> defaulted(String id, Class<T> type, String def) {
        return FabricRegistryBuilder.createDefaulted(type, id(id), id(def)).buildAndRegister();
    }

    private static Identifier id(String path) {
        return new Identifier(Splatcraft.MOD_ID, path);
    }
}
