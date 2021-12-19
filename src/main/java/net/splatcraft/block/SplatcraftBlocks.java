package net.splatcraft.block;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;
import net.splatcraft.Splatcraft;
import net.splatcraft.inkcolor.InkType;
import net.splatcraft.item.InkableBlockItem;
import net.splatcraft.item.SplatcraftItemGroups;
import net.splatcraft.sound.SplatcraftBlockSoundGroup;

import java.util.function.Function;

public class SplatcraftBlocks {
    public static final Block CANVAS = register("canvas", new CanvasBlock(
        FabricBlockSettings.of(Material.WOOL)
                           .strength(0.8f)
                           .sounds(BlockSoundGroup.WOOL)
    ), SplatcraftBlocks::inkableBlockItem);

    public static final Block INKED_BLOCK = register("inked_block", new InkedBlock(InkType.NORMAL), null);
    public static final Block GLOWING_INKED_BLOCK = register("glowing_inked_block", new InkedBlock(InkType.GLOWING), null);

    public static final Block EMPTY_INKWELL = register("empty_inkwell", new EmptyInkwellBlock(
        FabricBlockSettings.of(Material.GLASS)
                           .strength(0.3f)
                           .nonOpaque().sounds(SplatcraftBlockSoundGroup.EMPTY_INKWELL)
    ));

    public static final Block INKWELL = register("inkwell", new InkwellBlock(
        FabricBlockSettings.of(Material.GLASS)
                           .strength(0.35f)
                           .sounds(SplatcraftBlockSoundGroup.INKWELL)
    ), SplatcraftBlocks::inkableBlockItem);

    public static final Block GRATE_BLOCK = register("grate_block", new GrateBlockBlock(
        FabricBlockSettings.of(Material.METAL)
                           .requiresTool().strength(4.0f)
                           .nonOpaque().suffocates(SplatcraftBlocks::never)
                           .sounds(BlockSoundGroup.METAL)
    ));

    public static final Block GRATE = register("grate", new GrateBlock(FabricBlockSettings.copyOf(GRATE_BLOCK)));

    public static final Block STAGE_BARRIER = register("stage_barrier", new StageBarrierBlock(
        FabricBlockSettings.of(Material.BARRIER)
                           .strength(-1.0f, 3600000.8f).dropsNothing()
                           .nonOpaque().allowsSpawning(SplatcraftBlocks::never)
    ));

    public static final Block STAGE_VOID = register("stage_void", new StageVoidBlock(
        FabricBlockSettings.of(Material.BARRIER)
                           .strength(-1.0f, 3600000.8f).dropsNothing()
                           .nonOpaque().allowsSpawning(SplatcraftBlocks::never)
    ));

    private static Block register(String id, Block block, Function<Block, Item> item) {
        Identifier identifier = new Identifier(Splatcraft.MOD_ID, id);
        if (item != null) Registry.register(Registry.ITEM, identifier, item.apply(block));
        return Registry.register(Registry.BLOCK, identifier, block);
    }

    private static Block register(String id, Block block) {
        return register(id, block, (b) -> new BlockItem(b, new FabricItemSettings().group(SplatcraftItemGroups.ALL)));
    }

    private static Item inkableBlockItem(Block block) {
        return new InkableBlockItem(block, new FabricItemSettings().group(SplatcraftItemGroups.ALL));
    }

    private static boolean never(BlockState state, BlockView world, BlockPos pos) {
        return false;
    }

    private static boolean never(BlockState state, BlockView world, BlockPos pos, EntityType<?> entityType) {
        return false;
    }
}
