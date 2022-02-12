package net.splatcraft.datagen;

import net.minecraft.loot.context.LootContextTypes;
import net.moddingplayground.toymaker.api.ToymakerEntrypoint;
import net.moddingplayground.toymaker.api.registry.generator.ItemModelGeneratorStore;
import net.moddingplayground.toymaker.api.registry.generator.LootGeneratorStore;
import net.moddingplayground.toymaker.api.registry.generator.RecipeGeneratorStore;
import net.moddingplayground.toymaker.api.registry.generator.StateModelGeneratorStore;
import net.moddingplayground.toymaker.api.registry.generator.TagGeneratorStore;

public class SplatcraftToymaker implements ToymakerEntrypoint {
    @Override
    public void onInitializeToymaker() {
        LootGeneratorStore.register(BlockLootTableGenerator::new, LootContextTypes.BLOCK);
        ItemModelGeneratorStore.register(ItemModelGenerator::new);
        RecipeGeneratorStore.register(RecipeGenerator::new);
        StateModelGeneratorStore.register(StateModelGenerator::new);
        TagGeneratorStore.register(BlockTagGenerator::new);
        TagGeneratorStore.register(ItemTagGenerator::new);
        TagGeneratorStore.register(EntityTagGenerator::new);
        TagGeneratorStore.register(InkColorTagGenerator::new);
    }
}
