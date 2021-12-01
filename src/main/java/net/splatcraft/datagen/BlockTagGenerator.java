package net.splatcraft.datagen;

import net.minecraft.block.Block;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.registry.Registry;
import net.splatcraft.Splatcraft;
import net.splatcraft.block.SplatcraftBlocks;
import net.splatcraft.datagen.impl.generator.tag.AbstractTagGenerator;

public class BlockTagGenerator extends AbstractTagGenerator<Block> {
    public BlockTagGenerator() {
        super(Splatcraft.MOD_ID, Registry.BLOCK);
    }

    @Override
    public void generate() {
        this.add(BlockTags.PICKAXE_MINEABLE,
            SplatcraftBlocks.GRATE
        );
    }
}
