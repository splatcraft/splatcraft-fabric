package net.splatcraft.impl.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.moddingplayground.frame.api.toymaker.v0.recipe.RecipeExporter;

import java.util.function.Consumer;

import static net.minecraft.item.Items.*;
import static net.moddingplayground.frame.api.toymaker.v0.recipe.RecipeJsonBuilders.*;
import static net.splatcraft.api.block.SplatcraftBlocks.*;

public class RecipeProvider extends FabricRecipeProvider {
    public RecipeProvider(FabricDataGenerator exporter) {
        super(exporter);
    }

    @Override
    protected void generateRecipes(Consumer<RecipeJsonProvider> gen) {
        RecipeExporter exporter = RecipeExporter.of(gen, this);
        exporter.export(ring(IRON_BARS, GRATE_BLOCK, 8));
        exporter.export(generic2x2(GRATE_BLOCK, GRATE, 16));
        exporter.export(woodenStairs(GRATE_BLOCK, GRATE_RAMP).group(null));
    }
}
