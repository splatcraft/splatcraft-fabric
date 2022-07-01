package net.splatcraft.impl.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.SingleItemRecipeJsonBuilder;
import net.minecraft.util.Identifier;
import net.splatcraft.api.Splatcraft;

import java.util.function.Consumer;

import static net.minecraft.item.Items.*;
import static net.moddingplayground.frame.api.toymaker.v0.RecipeJsonBuilders.*;
import static net.splatcraft.api.block.SplatcraftBlocks.*;

public class RecipeProvider extends FabricRecipeProvider {
    private Consumer<RecipeJsonProvider> exporter;

    public RecipeProvider(FabricDataGenerator exporter) {
        super(exporter);
    }

    @Override
    protected void generateRecipes(Consumer<RecipeJsonProvider> gen) {
        this.exporter = gen;

        offer(ring(IRON_BARS, GRATE_BLOCK, 8));
        offer(generic2x2(GRATE_BLOCK, GRATE, 16));
        offer(woodenStairs(GRATE_BLOCK, GRATE_RAMP).group(null));
    }

    protected void offer(CraftingRecipeJsonBuilder recipe, String id) {
        recipe.offerTo(this.exporter, new Identifier(Splatcraft.MOD_ID, recipe instanceof SingleItemRecipeJsonBuilder ? id + "_from_stonecutting" : id));
    }

    protected void offer(CraftingRecipeJsonBuilder recipe) {
        this.offer(recipe, getItemPath(recipe.getOutputItem()));
    }
}
