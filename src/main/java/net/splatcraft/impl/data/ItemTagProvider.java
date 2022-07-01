package net.splatcraft.impl.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Item;
import net.minecraft.tag.TagKey;
import net.minecraft.util.registry.Registry;

import static net.splatcraft.api.item.SplatcraftItems.*;
import static net.splatcraft.impl.data.SplatcraftDataGeneratorImpl.*;

public class ItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public static final TagKey<Item> TRINKETS_HAND_RING = trinketTag(Registry.ITEM, "hand/ring");
    public static final TagKey<Item> TRINKETS_OFFHAND_RING = trinketTag(Registry.ITEM, "offhand/ring");

    public ItemTagProvider(FabricDataGenerator gen, BlockTagProvider blockTagProvider) {
        super(gen, blockTagProvider);
    }

    @Override
    protected void generateTags() {
        this.getOrCreateTagBuilder(TRINKETS_HAND_RING).add(SPLATFEST_BAND);
        this.getOrCreateTagBuilder(TRINKETS_OFFHAND_RING).add(SPLATFEST_BAND);
    }
}
