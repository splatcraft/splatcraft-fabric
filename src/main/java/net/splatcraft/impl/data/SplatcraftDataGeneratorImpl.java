package net.splatcraft.impl.data;

import dev.emi.trinkets.TrinketsMain;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.splatcraft.api.Splatcraft;

public final class SplatcraftDataGeneratorImpl implements Splatcraft, DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator gen) {
        gen.addProvider(ModelProvider::new);
        gen.addProvider(RecipeProvider::new);
        gen.addProvider(BlockLootTableProvider::new);

        BlockTagProvider blockTagProvider = gen.addProvider(BlockTagProvider::new);
        gen.addProvider(g -> new ItemTagProvider(g, blockTagProvider));
        gen.addProvider(EntityTypeTagProvider::new);
        gen.addProvider(InkColorTagProvider::new);
        gen.addProvider(BannerPatternTagProvider::new);
    }

    public static <T> TagKey<T> trinketTag(Registry<T> registry, String id) {
        return TagKey.of(registry.getKey(), new Identifier(TrinketsMain.MOD_ID, id));
    }
}
