package net.splatcraft.datagen;

import net.fabricmc.fabric.api.mininglevel.v1.FabricMineableTags;
import net.minecraft.block.Block;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.registry.Registry;
import net.moddingplayground.toymaker.api.generator.tag.AbstractTagGenerator;
import net.splatcraft.Splatcraft;
import net.splatcraft.tag.SplatcraftBlockTags;

import static net.splatcraft.block.SplatcraftBlocks.*;

public class BlockTagGenerator extends AbstractTagGenerator<Block> {
    public BlockTagGenerator() {
        super(Splatcraft.MOD_ID, Registry.BLOCK);
    }

    @Override
    public void generate() {
        this.add(FabricMineableTags.SHEARS_MINEABLE,
            CANVAS
        );

        this.add(BlockTags.PICKAXE_MINEABLE,
            EMPTY_INKWELL,
            INKWELL,

            GRATE_BLOCK,
            GRATE,
            GRATE_RAMP
        );

        this.add(SplatcraftBlockTags.INK_COLOR_CHANGERS,
            INKWELL
        );

        this.add(SplatcraftBlockTags.INK_CLIMBABLE,
            CANVAS,
            INKED_BLOCK,
            GLOWING_INKED_BLOCK
        );
    }
}
