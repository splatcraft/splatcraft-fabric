package net.splatcraft.datagen;

import net.splatcraft.Splatcraft;
import net.splatcraft.block.SplatcraftBlocks;
import net.splatcraft.datagen.impl.generator.loot.AbstractBlockLootTableGenerator;

public class BlockLootTableGenerator extends AbstractBlockLootTableGenerator {
    public BlockLootTableGenerator() {
        super(Splatcraft.MOD_ID);
    }

    @Override
    public void generate() {
        this.add(SplatcraftBlocks.GRATE);
    }
}
