package net.splatcraft.api.registry;

import com.mojang.serialization.Lifecycle;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.fabricmc.fabric.api.event.registry.RegistryAttributeHolder;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.splatcraft.api.Splatcraft;
import net.splatcraft.api.inkcolor.InkColor;

import java.util.function.Function;

public interface SplatcraftRegistry {
    DefaultedRegistry<InkColor> INK_COLOR = defaulted("ink_color", "white", InkColor::getRegistryEntry);
    RegistryKey<? extends Registry<InkColor>> INK_COLOR_KEY = INK_COLOR.getKey();

    private static <T> DefaultedRegistry<T> defaulted(String id, String defaultId, Function<T, RegistryEntry.Reference<T>> entry) {
        DefaultedRegistry<T> registry = new DefaultedRegistry<>(defaultId, RegistryKey.ofRegistry(new Identifier(Splatcraft.MOD_ID, id)), Lifecycle.stable(), entry);
        RegistryAttributeHolder.get(registry).addAttribute(RegistryAttribute.SYNCED);
        return registry;
    }
}
