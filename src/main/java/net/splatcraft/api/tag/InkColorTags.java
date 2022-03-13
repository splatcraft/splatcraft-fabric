package net.splatcraft.api.tag;

import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.splatcraft.api.Splatcraft;
import net.splatcraft.api.inkcolor.InkColor;
import net.splatcraft.api.registry.SplatcraftRegistries;

public interface InkColorTags {
    TagKey<InkColor> STARTER_COLORS = register("starter_colors");

    private static TagKey<InkColor> register(String id) {
        return TagKey.of(SplatcraftRegistries.INK_COLOR.getKey(), new Identifier(Splatcraft.MOD_ID, id));
    }
}
