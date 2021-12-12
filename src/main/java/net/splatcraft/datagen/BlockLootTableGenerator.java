package net.splatcraft.datagen;

import net.minecraft.loot.LootTable;
import net.splatcraft.Splatcraft;
import net.splatcraft.block.SplatcraftBlocks;
import net.splatcraft.datagen.impl.generator.loot.AbstractBlockLootTableGenerator;

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

        this.add(SplatcraftBlocks.GRATE);
    }
}
