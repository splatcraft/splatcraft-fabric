package net.splatcraft.datagen;

import net.minecraft.loot.LootTable;
import net.moddingplayground.toymaker.api.generator.loot.AbstractBlockLootTableGenerator;
import net.splatcraft.Splatcraft;

import static net.splatcraft.block.SplatcraftBlocks.*;

public class BlockLootTableGenerator extends AbstractBlockLootTableGenerator {
    public BlockLootTableGenerator() {
        super(Splatcraft.MOD_ID);
    }

    @Override
    public void generate() {
        this.add(CANVAS);
        this.add(INKED_BLOCK, block -> LootTable.builder());
        this.add(GLOWING_INKED_BLOCK, block -> LootTable.builder());

        this.add(EMPTY_INKWELL);
        this.add(INKWELL);

        this.add(GRATE_BLOCK);
        this.add(GRATE);
        this.add(GRATE_RAMP);
    }
}
