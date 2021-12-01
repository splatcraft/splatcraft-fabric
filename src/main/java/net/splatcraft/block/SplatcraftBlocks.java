package net.splatcraft.block;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.splatcraft.Splatcraft;

public class SplatcraftBlocks {
    public static final Block GRATE = register("grate", new GrateBlock(
        FabricBlockSettings.of(Material.METAL)
                           .requiresTool().strength(4.0f)
                           .nonOpaque().sounds(BlockSoundGroup.METAL)
    ));

    private static Block register(String id, Block block, boolean item) {
        Identifier identifier = new Identifier(Splatcraft.MOD_ID, id);
        if (item) Registry.register(Registry.ITEM, identifier, new BlockItem(block, new FabricItemSettings().group(Splatcraft.ITEM_GROUP)));
        return Registry.register(Registry.BLOCK, identifier, block);
    }

    private static Block register(String id, Block block) {
        return register(id, block, true);
    }
}