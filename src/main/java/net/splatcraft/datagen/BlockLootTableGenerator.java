package net.splatcraft.datagen;

import net.minecraft.loot.LootTable;
import net.moddingplayground.toymaker.api.generator.loot.AbstractBlockLootTableGenerator;
import net.splatcraft.Splatcraft;
import net.splatcraft.block.SplatcraftBlocks;

public class BlockLootTableGenerator extends AbstractBlockLootTableGenerator {
    public BlockLootTableGenerator() {
        super(Splatcraft.MOD_ID);
    }

    @Override
    public void generate() {
        this.add(SplatcraftBlocks.CANVAS);
        this.add(SplatcraftBlocks.INKED_BLOCK, block -> LootTable.builder());
        this.add(SplatcraftBlocks.GLOWING_INKED_BLOCK, block -> LootTable.builder());

        this.add(SplatcraftBlocks.EMPTY_INKWELL);
        this.add(SplatcraftBlocks.INKWELL);

        this.add(SplatcraftBlocks.GRATE_BLOCK);
        this.add(SplatcraftBlocks.GRATE);
    }
}
