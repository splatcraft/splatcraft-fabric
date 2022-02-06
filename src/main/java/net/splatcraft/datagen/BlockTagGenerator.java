package net.splatcraft.datagen;

import net.fabricmc.fabric.api.mininglevel.v1.FabricMineableTags;
import net.minecraft.block.Block;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.registry.Registry;
import net.moddingplayground.toymaker.api.generator.tag.AbstractTagGenerator;
import net.splatcraft.Splatcraft;
import net.splatcraft.block.SplatcraftBlocks;
import net.splatcraft.tag.SplatcraftBlockTags;

public class BlockTagGenerator extends AbstractTagGenerator<Block> {
    public BlockTagGenerator() {
        super(Splatcraft.MOD_ID, Registry.BLOCK);
    }

    @Override
    public void generate() {
        this.add(FabricMineableTags.SHEARS_MINEABLE,
            SplatcraftBlocks.CANVAS
        );

        this.add(BlockTags.PICKAXE_MINEABLE,
            SplatcraftBlocks.EMPTY_INKWELL,
            SplatcraftBlocks.INKWELL,

            SplatcraftBlocks.GRATE_BLOCK,
            SplatcraftBlocks.GRATE
        );

        this.add(SplatcraftBlockTags.INK_COLOR_CHANGERS,
            SplatcraftBlocks.INKWELL
        );

        this.add(SplatcraftBlockTags.INK_CLIMBABLE,
            SplatcraftBlocks.CANVAS,
            SplatcraftBlocks.INKED_BLOCK,
            SplatcraftBlocks.GLOWING_INKED_BLOCK
        );
    }
}
