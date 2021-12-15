package net.splatcraft.datagen;

import net.minecraft.item.Items;
import net.splatcraft.Splatcraft;
import net.splatcraft.block.SplatcraftBlocks;
import net.splatcraft.datagen.impl.generator.recipe.AbstractRecipeGenerator;

public class RecipeGenerator extends AbstractRecipeGenerator {
    public RecipeGenerator() {
        super(Splatcraft.MOD_ID);
    }

    @Override
    public void generate() {
        this.add("grate_block", this.ring(Items.IRON_BARS, SplatcraftBlocks.GRATE_BLOCK, 8));
        this.add("grate", this.generic2x2(SplatcraftBlocks.GRATE_BLOCK, SplatcraftBlocks.GRATE, 16));
    }
}
