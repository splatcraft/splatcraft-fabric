package net.splatcraft.impl.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.Block;
import net.minecraft.loot.LootTable;

import static net.splatcraft.api.block.SplatcraftBlocks.*;

public class BlockLootTableProvider extends FabricBlockLootTableProvider {
    protected BlockLootTableProvider(FabricDataGenerator gen) {
        super(gen);
    }

    @Override
    protected void generateBlockLootTables() {
        this.addDrops(
            CANVAS,
            EMPTY_INKWELL,
            INKWELL,
            GRATE_BLOCK,
            GRATE,
            GRATE_RAMP
        );

        this.addDrops(dropsNothing(),
            INKED_BLOCK,
            GLOWING_INKED_BLOCK
        );
    }

    private void addDrops(Block... blocks) {
        for (Block block : blocks) this.addDrop(block);
    }

    private void addDrops(LootTable.Builder builder, Block... blocks) {
        for (Block block : blocks) this.addDrop(block, builder);
    }
}
