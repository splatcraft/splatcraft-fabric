package com.cibernet.splatcraft.init;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.block.*;
import com.cibernet.splatcraft.sound.SplatcraftBlockSoundGroups;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("unused")
public class SplatcraftBlocks {
    private static final List<Block> INKABLES = new LinkedList<>();

    /*
     * INKABLE BLOCKS
     */

    public static final Block INKWELL = register(InkwellBlock.id, new InkwellBlock(
        FabricBlockSettings.of(Material.GLASS)
            .strength(0.35f).breakByTool(FabricToolTags.PICKAXES)
            .sounds(SplatcraftBlockSoundGroups.INKWELL)
        ), false
    );
    public static final Block CANVAS = register(CanvasBlock.id, new CanvasBlock(
        FabricBlockSettings.of(Material.WOOL)
            .strength(0.8f).sounds(BlockSoundGroup.WOOL)
        ), false
    );

    public static final Block INKED_BLOCK = register(InkedBlock.id, new InkedBlock(false), false);
    public static final Block GLOWING_INKED_BLOCK = register("glowing_" + InkedBlock.id, new InkedBlock(true), false);

    /*
     * STAGE BLOCKS
     */

    public static final Block GRATE = register(GrateBlock.id, new GrateBlock(
        FabricBlockSettings.of(Material.METAL)
            .requiresTool().breakByTool(FabricToolTags.PICKAXES)
            .nonOpaque().sounds(BlockSoundGroup.METAL)
            .strength(4.0f)
        )
    );
    public static final Block GRATE_RAMP = register(GrateRampBlock.id, new GrateRampBlock(FabricBlockSettings.copy(GRATE)));

    public static final Block BARRIER_BAR = register(BarrierBarBlock.id, new BarrierBarBlock(
        FabricBlockSettings.of(Material.METAL, MapColor.CLEAR)
            .strength(3.0f).breakByTool(FabricToolTags.PICKAXES)
            .requiresTool().nonOpaque()
    ));

    public static final Block STAGE_BARRIER = register(StageBarrierBlock.id, new StageBarrierBlock(
            FabricBlockSettings.of(Material.BARRIER, MapColor.CLEAR)
                .strength(-1.0f, 3600000.8f).dropsNothing()
                .nonOpaque(),
            false
        )
    );
    public static final Block STAGE_VOID = register("stage_void", new StageBarrierBlock(FabricBlockSettings.copy(STAGE_BARRIER), true));

    private static Block register(String id, Block block, boolean registerItem) {
        Identifier identifier = new Identifier(Splatcraft.MOD_ID, id);

        Block registeredBlock = Registry.register(Registry.BLOCK, identifier, block);
        if (registerItem) {
            Registry.register(Registry.ITEM, identifier, new BlockItem(registeredBlock, new FabricItemSettings().maxCount(64).group(Splatcraft.ITEM_GROUP)));
        }

        return registeredBlock;
    }
    private static Block register(String id, Block block) {
        return register(id, block, true);
    }

    public static Block[] getInkables() {
        return INKABLES.toArray(new Block[]{});
    }
    public static void addToInkables(Block block) {
        INKABLES.add(block);
    }
}
