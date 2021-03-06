package net.splatcraft.api.block;

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
import net.splatcraft.api.Splatcraft;
import net.splatcraft.api.inkcolor.InkType;
import net.splatcraft.api.inkcolor.Inkable;
import net.splatcraft.api.item.InkableBlockItem;
import net.splatcraft.api.item.SplatcraftItemGroups;
import net.splatcraft.api.sound.SplatcraftBlockSoundGroup;

import java.util.function.Function;

public interface SplatcraftBlocks {
    Block CANVAS = register("canvas", new CanvasBlock(
        FabricBlockSettings.of(Material.WOOL)
                           .strength(0.8f).sounds(BlockSoundGroup.WOOL)
                           .emissiveLighting((state, world, pos) -> world.getBlockEntity(pos) instanceof Inkable inkable && inkable.getInkType() == InkType.GLOWING)
    ), SplatcraftBlocks::inkableBlockItem);

    Block INKED_BLOCK = register("inked_block", new InkedBlock(InkType.NORMAL), null);
    Block GLOWING_INKED_BLOCK = register("glowing_inked_block", new InkedBlock(InkType.GLOWING), null);

    Block EMPTY_INKWELL = register("empty_inkwell", new EmptyInkwellBlock(
        FabricBlockSettings.of(Material.GLASS)
                           .strength(0.3f).nonOpaque()
                           .sounds(SplatcraftBlockSoundGroup.EMPTY_INKWELL)
    ));

    Block INKWELL = register("inkwell", new InkwellBlock(
        FabricBlockSettings.of(Material.GLASS)
                           .strength(0.35f).nonOpaque()
                           .sounds(SplatcraftBlockSoundGroup.INKWELL)
    ), SplatcraftBlocks::inkableBlockItem);

    Block GRATE_BLOCK = register("grate_block", new GrateBlockBlock(
        FabricBlockSettings.of(Material.METAL)
                           .requiresTool().strength(4.0f)
                           .nonOpaque().suffocates(SplatcraftBlocks::never)
                           .sounds(BlockSoundGroup.METAL)
    ));

    Block GRATE = register("grate", new GrateBlock(FabricBlockSettings.copyOf(GRATE_BLOCK)));
    Block GRATE_RAMP = register("grate_ramp", new GrateRampBlock(FabricBlockSettings.copyOf(GRATE_BLOCK)));

    Block STAGE_BARRIER = register("stage_barrier", new StageBarrierBlock(
        FabricBlockSettings.of(Material.BARRIER)
                           .strength(-1.0f, 3600000.8f).dropsNothing()
                           .nonOpaque().allowsSpawning(SplatcraftBlocks::never)
    ));

    Block STAGE_VOID = register("stage_void", new StageVoidBlock(
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
        return register(id, block, b -> new BlockItem(b, new FabricItemSettings().group(SplatcraftItemGroups.ALL)));
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
