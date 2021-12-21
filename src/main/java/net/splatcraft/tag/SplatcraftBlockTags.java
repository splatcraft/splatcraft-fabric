package net.splatcraft.tag;

import net.fabricmc.fabric.api.tag.TagFactory;
import net.minecraft.block.Block;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.splatcraft.Splatcraft;

public class SplatcraftBlockTags {
    public static final Tag.Identified<Block> INK_COLOR_CHANGERS = register("ink_color_changers");
    public static final Tag.Identified<Block> INK_CLIMBABLE = register("ink_climbable");

    private static Tag.Identified<Block> register(String id) {
        return TagFactory.BLOCK.create(new Identifier(Splatcraft.MOD_ID, id));
    }
}
