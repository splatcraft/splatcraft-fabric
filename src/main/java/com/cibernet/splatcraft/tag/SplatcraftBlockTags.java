package com.cibernet.splatcraft.tag;

import com.cibernet.splatcraft.Splatcraft;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.block.Block;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

public class SplatcraftBlockTags {
    public static final Tag<Block> INKABLE_BLOCKS = register("inkable_blocks");
    public static final Tag<Block> UNINKABLE_BLOCKS = register("uninkable_blocks");
    public static final Tag<Block> STAGE_BARRIERS = register("stage_barriers");
    public static final Tag<Block> SCAN_TURF_IGNORED = register("scan_turf_ignored");
    public static final Tag<Block> INK_SQUID_PASSTHROUGHABLES = register("ink_squid_passthroughables");

    public SplatcraftBlockTags() {}

    private static Tag<Block> register(String id) {
        return TagRegistry.block(new Identifier(Splatcraft.MOD_ID, id));
    }
}
