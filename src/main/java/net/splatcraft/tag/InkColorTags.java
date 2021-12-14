package net.splatcraft.tag;

import net.fabricmc.fabric.api.tag.TagFactory;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.splatcraft.Splatcraft;
import net.splatcraft.inkcolor.InkColor;
import net.splatcraft.registry.SplatcraftRegistries;

public class InkColorTags {
    public static final Tag.Identified<InkColor> STARTER_COLORS = register("starter_colors");

    public static Tag.Identified<InkColor> register(String id) {
        return TagFactory.of(SplatcraftRegistries.INK_COLOR.getKey(), "tags/ink_colors").create(new Identifier(Splatcraft.MOD_ID, id));
    }
}
