package net.splatcraft.impl.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.mininglevel.v1.FabricMineableTags;
import net.minecraft.tag.BlockTags;
import net.splatcraft.api.tag.SplatcraftBlockTags;

import static net.splatcraft.api.block.SplatcraftBlocks.*;

public class BlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public BlockTagProvider(FabricDataGenerator gen) {
        super(gen);
    }

    @Override
    protected void generateTags() {
        this.getOrCreateTagBuilder(SplatcraftBlockTags.INK_COLOR_CHANGERS).add(INKWELL);

        this.getOrCreateTagBuilder(SplatcraftBlockTags.INK_CLIMBABLE).add(
            CANVAS,
            INKED_BLOCK,
            GLOWING_INKED_BLOCK
        );

        this.getOrCreateTagBuilder(FabricMineableTags.SHEARS_MINEABLE).add(
            CANVAS
        );

        this.getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(
            EMPTY_INKWELL,
            INKWELL,
            GRATE_BLOCK,
            GRATE,
            GRATE_RAMP
        );
    }
}
