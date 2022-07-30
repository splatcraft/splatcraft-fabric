package net.splatcraft.api.inkcolor;

import net.minecraft.util.DyeColor;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.splatcraft.api.registry.SplatcraftRegistry;

public interface InkColors {
    InkColor DYE_WHITE = register(InkColorKeys.DYE_WHITE, InkColor.fromDye(DyeColor.WHITE));
    InkColor DYE_ORANGE = register(InkColorKeys.DYE_ORANGE, InkColor.fromDye(DyeColor.ORANGE));
    InkColor DYE_MAGENTA = register(InkColorKeys.DYE_MAGENTA, InkColor.fromDye(DyeColor.MAGENTA));
    InkColor DYE_LIGHT_BLUE = register(InkColorKeys.DYE_LIGHT_BLUE, InkColor.fromDye(DyeColor.LIGHT_BLUE));
    InkColor DYE_YELLOW = register(InkColorKeys.DYE_YELLOW, InkColor.fromDye(DyeColor.YELLOW));
    InkColor DYE_LIME = register(InkColorKeys.DYE_LIME, InkColor.fromDye(DyeColor.LIME));
    InkColor DYE_PINK = register(InkColorKeys.DYE_PINK, InkColor.fromDye(DyeColor.PINK));
    InkColor DYE_GRAY = register(InkColorKeys.DYE_GRAY, InkColor.fromDye(DyeColor.GRAY));
    InkColor DYE_LIGHT_GRAY = register(InkColorKeys.DYE_LIGHT_GRAY, InkColor.fromDye(DyeColor.LIGHT_GRAY));
    InkColor DYE_CYAN = register(InkColorKeys.DYE_CYAN, InkColor.fromDye(DyeColor.CYAN));
    InkColor DYE_PURPLE = register(InkColorKeys.DYE_PURPLE, InkColor.fromDye(DyeColor.PURPLE));
    InkColor DYE_BLUE = register(InkColorKeys.DYE_BLUE, InkColor.fromDye(DyeColor.BLUE));
    InkColor DYE_BROWN = register(InkColorKeys.DYE_BROWN, InkColor.fromDye(DyeColor.BROWN));
    InkColor DYE_GREEN = register(InkColorKeys.DYE_GREEN, InkColor.fromDye(DyeColor.GREEN));
    InkColor DYE_RED = register(InkColorKeys.DYE_RED, InkColor.fromDye(DyeColor.RED));
    InkColor DYE_BLACK = register(InkColorKeys.DYE_BLACK, InkColor.fromDye(DyeColor.BLACK));

    InkColor MOJANG = register(InkColorKeys.MOJANG, InkColor.of(ColorHelper.Argb.getArgb(255, 239, 50, 61)));
    InkColor COBALT = register(InkColorKeys.COBALT, InkColor.of(0x005682));
    InkColor ICE = register(InkColorKeys.ICE, InkColor.of(0x88FFC1));
    InkColor FLORAL = register(InkColorKeys.FLORAL, InkColor.of(0xFF9BEE));

    InkColor PURE_WHITE = register(InkColorKeys.PURE_WHITE, InkColor.of(0xFFFFFF));
    InkColor PURE_BLACK = register(InkColorKeys.PURE_BLACK, InkColor.of(0x000000));
    InkColor PURE_RED = register(InkColorKeys.PURE_RED, InkColor.of(0xFF0000));
    InkColor PURE_YELLOW = register(InkColorKeys.PURE_YELLOW, InkColor.of(0xFFFF00));
    InkColor PURE_PURPLE = register(InkColorKeys.PURE_PURPLE, InkColor.of(0xFF00FF));
    InkColor PURE_GREEN = register(InkColorKeys.PURE_GREEN, InkColor.of(0x00FF00));
    InkColor PURE_CYAN = register(InkColorKeys.PURE_CYAN, InkColor.of(0x00FFFF));
    InkColor PURE_BLUE = register(InkColorKeys.PURE_BLUE, InkColor.of(0x0000FF));

    InkColor PINK = register(InkColorKeys.PINK, InkColor.of(0xFE447D));
    InkColor ORANGE = register(InkColorKeys.ORANGE, InkColor.of(0xF78F2E));
    InkColor YELLOW_ORANGE = register(InkColorKeys.YELLOW_ORANGE, InkColor.of(0xFEDC0C));
    InkColor LIME_GREEN = register(InkColorKeys.LIME_GREEN, InkColor.of(0xD1F20A));
    InkColor EMERALD_GREEN = register(InkColorKeys.EMERALD_GREEN, InkColor.of(0x5CD05B));
    InkColor TEAL = register(InkColorKeys.TEAL, InkColor.of(0x03C1CD));
    InkColor BLUE = register(InkColorKeys.BLUE, InkColor.of(0x0E10E6));
    InkColor VIOLET = register(InkColorKeys.VIOLET, InkColor.of(0x9208E7));
    InkColor RED_ORANGE = register(InkColorKeys.RED_ORANGE, InkColor.of(0xFF4900));
    InkColor YELLOW = register(InkColorKeys.YELLOW, InkColor.of(0xF3F354));
    InkColor MINT = register(InkColorKeys.MINT, InkColor.of(0xBFF1E5));
    InkColor GREEN = register(InkColorKeys.GREEN, InkColor.of(0x3BC335));
    InkColor SEA_GREEN = register(InkColorKeys.SEA_GREEN, InkColor.of(0x00D18A));
    InkColor LIGHT_BLUE = register(InkColorKeys.LIGHT_BLUE, InkColor.of(0x6565F9));
    InkColor DARK_BLUE = register(InkColorKeys.DARK_BLUE, InkColor.of(0x101AB3));
    InkColor FUCHSIA = register(InkColorKeys.FUCHSIA, InkColor.of(0xD645C8));
    InkColor NEON_GREEN = register(InkColorKeys.NEON_GREEN, InkColor.of(0x9AE528));
    InkColor CYAN = register(InkColorKeys.CYAN, InkColor.of(0x0ACDFE));
    InkColor NEON_ORANGE = register(InkColorKeys.NEON_ORANGE, InkColor.of(0xFF9600));
    InkColor DARK_FUCHSIA = register(InkColorKeys.DARK_FUCHSIA, InkColor.of(0xB21CA1));
    InkColor TAN = register(InkColorKeys.TAN, InkColor.of(0xE1D6B6));
    InkColor DARK_ORANGE = register(InkColorKeys.DARK_ORANGE, InkColor.of(0xE59D0D));
    InkColor MAGENTA = register(InkColorKeys.MAGENTA, InkColor.of(0xEC0B68));
    InkColor DARK_VIOLET = register(InkColorKeys.DARK_VIOLET, InkColor.of(0x461362));
    InkColor IKA_MUSUME_BLUE = register(InkColorKeys.IKA_MUSUME_BLUE, InkColor.of(0x00AAE4));
    InkColor FOREST_GREEN = register(InkColorKeys.FOREST_GREEN, InkColor.of(0x0EAA57));

    private static InkColor register(RegistryKey<InkColor> key, InkColor inkColor) {
        return Registry.register(SplatcraftRegistry.INK_COLOR, key, inkColor);
    }

    /**/

    static RegistryEntry<InkColor> initAndGetDefault(Registry<InkColor> registry) {
        BuiltinRegistries.add(registry, InkColorKeys.DYE_WHITE, DYE_WHITE);
        BuiltinRegistries.add(registry, InkColorKeys.DYE_ORANGE, DYE_ORANGE);
        BuiltinRegistries.add(registry, InkColorKeys.DYE_MAGENTA, DYE_MAGENTA);
        BuiltinRegistries.add(registry, InkColorKeys.DYE_LIGHT_BLUE, DYE_LIGHT_BLUE);
        BuiltinRegistries.add(registry, InkColorKeys.DYE_YELLOW, DYE_YELLOW);
        BuiltinRegistries.add(registry, InkColorKeys.DYE_LIME, DYE_LIME);
        BuiltinRegistries.add(registry, InkColorKeys.DYE_PINK, DYE_PINK);
        BuiltinRegistries.add(registry, InkColorKeys.DYE_GRAY, DYE_GRAY);
        BuiltinRegistries.add(registry, InkColorKeys.DYE_LIGHT_GRAY, DYE_LIGHT_GRAY);
        BuiltinRegistries.add(registry, InkColorKeys.DYE_CYAN, DYE_CYAN);
        BuiltinRegistries.add(registry, InkColorKeys.DYE_PURPLE, DYE_PURPLE);
        BuiltinRegistries.add(registry, InkColorKeys.DYE_BLUE, DYE_BLUE);
        BuiltinRegistries.add(registry, InkColorKeys.DYE_BROWN, DYE_BROWN);
        BuiltinRegistries.add(registry, InkColorKeys.DYE_GREEN, DYE_GREEN);
        BuiltinRegistries.add(registry, InkColorKeys.DYE_RED, DYE_RED);
        BuiltinRegistries.add(registry, InkColorKeys.DYE_BLACK, DYE_BLACK);
        BuiltinRegistries.add(registry, InkColorKeys.MOJANG, MOJANG);
        BuiltinRegistries.add(registry, InkColorKeys.COBALT, COBALT);
        BuiltinRegistries.add(registry, InkColorKeys.ICE, ICE);
        BuiltinRegistries.add(registry, InkColorKeys.FLORAL, FLORAL);
        BuiltinRegistries.add(registry, InkColorKeys.PURE_WHITE, PURE_WHITE);
        BuiltinRegistries.add(registry, InkColorKeys.PURE_BLACK, PURE_BLACK);
        BuiltinRegistries.add(registry, InkColorKeys.PURE_RED, PURE_RED);
        BuiltinRegistries.add(registry, InkColorKeys.PURE_YELLOW, PURE_YELLOW);
        BuiltinRegistries.add(registry, InkColorKeys.PURE_PURPLE, PURE_PURPLE);
        BuiltinRegistries.add(registry, InkColorKeys.PURE_GREEN, PURE_GREEN);
        BuiltinRegistries.add(registry, InkColorKeys.PURE_CYAN, PURE_CYAN);
        BuiltinRegistries.add(registry, InkColorKeys.PURE_BLUE, PURE_BLUE);
        BuiltinRegistries.add(registry, InkColorKeys.PINK, PINK);
        BuiltinRegistries.add(registry, InkColorKeys.ORANGE, ORANGE);
        BuiltinRegistries.add(registry, InkColorKeys.YELLOW_ORANGE, YELLOW_ORANGE);
        BuiltinRegistries.add(registry, InkColorKeys.LIME_GREEN, LIME_GREEN);
        BuiltinRegistries.add(registry, InkColorKeys.EMERALD_GREEN, EMERALD_GREEN);
        BuiltinRegistries.add(registry, InkColorKeys.TEAL, TEAL);
        BuiltinRegistries.add(registry, InkColorKeys.BLUE, BLUE);
        BuiltinRegistries.add(registry, InkColorKeys.VIOLET, VIOLET);
        BuiltinRegistries.add(registry, InkColorKeys.RED_ORANGE, RED_ORANGE);
        BuiltinRegistries.add(registry, InkColorKeys.YELLOW, YELLOW);
        BuiltinRegistries.add(registry, InkColorKeys.MINT, MINT);
        BuiltinRegistries.add(registry, InkColorKeys.GREEN, GREEN);
        BuiltinRegistries.add(registry, InkColorKeys.SEA_GREEN, SEA_GREEN);
        BuiltinRegistries.add(registry, InkColorKeys.LIGHT_BLUE, LIGHT_BLUE);
        BuiltinRegistries.add(registry, InkColorKeys.DARK_BLUE, DARK_BLUE);
        BuiltinRegistries.add(registry, InkColorKeys.FUCHSIA, FUCHSIA);
        BuiltinRegistries.add(registry, InkColorKeys.NEON_GREEN, NEON_GREEN);
        BuiltinRegistries.add(registry, InkColorKeys.CYAN, CYAN);
        BuiltinRegistries.add(registry, InkColorKeys.NEON_ORANGE, NEON_ORANGE);
        BuiltinRegistries.add(registry, InkColorKeys.DARK_FUCHSIA, DARK_FUCHSIA);
        BuiltinRegistries.add(registry, InkColorKeys.TAN, TAN);
        BuiltinRegistries.add(registry, InkColorKeys.DARK_ORANGE, DARK_ORANGE);
        BuiltinRegistries.add(registry, InkColorKeys.MAGENTA, MAGENTA);
        BuiltinRegistries.add(registry, InkColorKeys.DARK_VIOLET, DARK_VIOLET);
        BuiltinRegistries.add(registry, InkColorKeys.IKA_MUSUME_BLUE, IKA_MUSUME_BLUE);
        return BuiltinRegistries.add(registry, InkColorKeys.FOREST_GREEN, FOREST_GREEN);
    }
}
