package net.splatcraft.api.registry;

import com.mojang.serialization.Lifecycle;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.splatcraft.api.Splatcraft;
import net.splatcraft.api.inkcolor.InkColor;

import java.util.function.Function;

import static net.splatcraft.api.util.SplatcraftConstants.*;

public interface SplatcraftRegistries {
    /**
     * Splatcraft's built-in ink colors.
     */
    Registry<InkColor> INK_COLOR = defaulted("ink_color", DEFAULT_INK_COLOR_IDENTIFIER, InkColor::getRegistryEntry);

    private static <T> DefaultedRegistry<T> defaulted(String id, Identifier def, Function<T, RegistryEntry.Reference<T>> entryFunction) {
        DefaultedRegistry<T> registry = new DefaultedRegistry<>(def.toString(), RegistryKey.ofRegistry(id(id)), Lifecycle.stable(), entryFunction);
        return FabricRegistryBuilder.from(registry)
                                    .attribute(RegistryAttribute.SYNCED)
                                    .buildAndRegister();
    }

    private static Identifier id(String path) {
        return new Identifier(Splatcraft.MOD_ID, path);
    }
}
