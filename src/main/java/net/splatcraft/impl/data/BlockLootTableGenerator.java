package net.splatcraft.impl.data;

import net.minecraft.loot.LootTable;
import net.moddingplayground.frame.api.toymaker.v0.generator.loot.AbstractBlockLootTableGenerator;
import net.splatcraft.api.Splatcraft;

import static net.splatcraft.api.block.SplatcraftBlocks.*;

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
