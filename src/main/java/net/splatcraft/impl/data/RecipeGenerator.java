package net.splatcraft.impl.data;

import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.moddingplayground.frame.api.toymaker.v0.generator.recipe.AbstractRecipeGenerator;
import net.splatcraft.api.Splatcraft;
import net.splatcraft.api.block.SplatcraftBlocks;

public class RecipeGenerator extends AbstractRecipeGenerator {
    public RecipeGenerator() {
        super(Splatcraft.MOD_ID);
    }

    @Override
    public void generate() {
        this.add("grate_block", this.ring(Items.IRON_BARS, SplatcraftBlocks.GRATE_BLOCK, 8));
        this.add("grate", this.generic2x2(SplatcraftBlocks.GRATE_BLOCK, SplatcraftBlocks.GRATE, 16));
        this.add("grate_ramp", this.stairs(SplatcraftBlocks.GRATE_BLOCK, SplatcraftBlocks.GRATE_RAMP));
    }

    public ShapedRecipeJsonBuilder stairs(ItemConvertible from, ItemConvertible to) {
        return ShapedRecipeJsonBuilder.create(to, 4)
                                      .input('#', from)
                                      .pattern("#  ")
                                      .pattern("## ")
                                      .pattern("###")
                                      .criterion("has_item", hasItem(from));
    }
}
