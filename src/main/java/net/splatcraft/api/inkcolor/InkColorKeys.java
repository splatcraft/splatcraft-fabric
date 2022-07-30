package net.splatcraft.api.inkcolor;

import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.RegistryKey;
import net.splatcraft.api.Splatcraft;
import net.splatcraft.api.registry.SplatcraftRegistry;

public interface InkColorKeys {
    RegistryKey<InkColor> DYE_WHITE = of(DyeColor.WHITE);
    RegistryKey<InkColor> DYE_ORANGE = of(DyeColor.ORANGE);
    RegistryKey<InkColor> DYE_MAGENTA = of(DyeColor.MAGENTA);
    RegistryKey<InkColor> DYE_LIGHT_BLUE = of(DyeColor.LIGHT_BLUE);
    RegistryKey<InkColor> DYE_YELLOW = of(DyeColor.YELLOW);
    RegistryKey<InkColor> DYE_LIME = of(DyeColor.LIME);
    RegistryKey<InkColor> DYE_PINK = of(DyeColor.PINK);
    RegistryKey<InkColor> DYE_GRAY = of(DyeColor.GRAY);
    RegistryKey<InkColor> DYE_LIGHT_GRAY = of(DyeColor.LIGHT_GRAY);
    RegistryKey<InkColor> DYE_CYAN = of(DyeColor.CYAN);
    RegistryKey<InkColor> DYE_PURPLE = of(DyeColor.PURPLE);
    RegistryKey<InkColor> DYE_BLUE = of(DyeColor.BLUE);
    RegistryKey<InkColor> DYE_BROWN = of(DyeColor.BROWN);
    RegistryKey<InkColor> DYE_GREEN = of(DyeColor.GREEN);
    RegistryKey<InkColor> DYE_RED = of(DyeColor.RED);
    RegistryKey<InkColor> DYE_BLACK = of(DyeColor.BLACK);

    RegistryKey<InkColor> MOJANG = of("mojang");
    RegistryKey<InkColor> COBALT = ofs("cobalt");
    RegistryKey<InkColor> ICE = ofs("ice");
    RegistryKey<InkColor> FLORAL = ofs("floral");

    RegistryKey<InkColor> PURE_WHITE = of("pure_white");
    RegistryKey<InkColor> PURE_BLACK = of("pure_black");
    RegistryKey<InkColor> PURE_RED = of("pure_red");
    RegistryKey<InkColor> PURE_YELLOW = of("pure_yellow");
    RegistryKey<InkColor> PURE_PURPLE = of("pure_purple");
    RegistryKey<InkColor> PURE_GREEN = of("pure_green");
    RegistryKey<InkColor> PURE_CYAN = of("pure_cyan");
    RegistryKey<InkColor> PURE_BLUE = of("pure_blue");

    RegistryKey<InkColor> PINK = ofs("pink");
    RegistryKey<InkColor> ORANGE = ofs("orange");
    RegistryKey<InkColor> YELLOW_ORANGE = ofs("yellow_orange");
    RegistryKey<InkColor> LIME_GREEN = ofs("lime_green");
    RegistryKey<InkColor> EMERALD_GREEN = ofs("emerald_green");
    RegistryKey<InkColor> TEAL = ofs("teal");
    RegistryKey<InkColor> BLUE = ofs("blue");
    RegistryKey<InkColor> VIOLET = ofs("violet");
    RegistryKey<InkColor> RED_ORANGE = ofs("red_orange");
    RegistryKey<InkColor> YELLOW = ofs("yellow");
    RegistryKey<InkColor> MINT = ofs("mint");
    RegistryKey<InkColor> GREEN = ofs("green");
    RegistryKey<InkColor> SEA_GREEN = ofs("sea_green");
    RegistryKey<InkColor> LIGHT_BLUE = ofs("light_blue");
    RegistryKey<InkColor> DARK_BLUE = ofs("dark_blue");
    RegistryKey<InkColor> FUCHSIA = ofs("fuchsia");
    RegistryKey<InkColor> NEON_GREEN = ofs("neon_green");
    RegistryKey<InkColor> CYAN = ofs("cyan");
    RegistryKey<InkColor> NEON_ORANGE = ofs("neon_orange");
    RegistryKey<InkColor> DARK_FUCHSIA = ofs("dark_fuchsia");
    RegistryKey<InkColor> TAN = ofs("tan");
    RegistryKey<InkColor> DARK_ORANGE = ofs("dark_orange");
    RegistryKey<InkColor> MAGENTA = ofs("magenta");
    RegistryKey<InkColor> DARK_VIOLET = ofs("dark_violet");
    RegistryKey<InkColor> IKA_MUSUME_BLUE = ofs("ika_musume_blue");
    RegistryKey<InkColor> FOREST_GREEN = ofs("forest_green");

    private static RegistryKey<InkColor> of(Identifier id) {
        return RegistryKey.of(SplatcraftRegistry.INK_COLOR_KEY, id);
    }

    private static RegistryKey<InkColor> of(String id) {
        return of(new Identifier(id));
    }

    private static RegistryKey<InkColor> ofs(String id) {
        return of(new Identifier(Splatcraft.MOD_ID, id));
    }

    private static RegistryKey<InkColor> of(DyeColor dye) {
        return of(dye.getName());
    }
}
