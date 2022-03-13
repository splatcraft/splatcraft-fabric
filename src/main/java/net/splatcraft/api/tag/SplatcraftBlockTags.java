package net.splatcraft.api.tag;

import net.minecraft.block.Block;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.splatcraft.api.Splatcraft;

public interface SplatcraftBlockTags {
    TagKey<Block> INK_COLOR_CHANGERS = register("ink_color_changers");
    TagKey<Block> INK_CLIMBABLE = register("ink_climbable");

    private static TagKey<Block> register(String id) {
        return TagKey.of(Registry.BLOCK_KEY, new Identifier(Splatcraft.MOD_ID, id));
    }
}
