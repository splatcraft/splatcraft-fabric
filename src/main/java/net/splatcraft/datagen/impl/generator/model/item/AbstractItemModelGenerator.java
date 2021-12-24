package net.splatcraft.datagen.impl.generator.model.item;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.splatcraft.datagen.impl.generator.model.AbstractModelGenerator;
import net.splatcraft.datagen.impl.generator.model.InheritingModelGen;
import net.splatcraft.datagen.impl.generator.model.ModelGen;

import java.util.function.Function;

public abstract class AbstractItemModelGenerator extends AbstractModelGenerator<Item, ModelGen> {
    public AbstractItemModelGenerator(String modId) {
        super(modId);
    }

    @Override
    public Registry<Item> getRegistry() {
        return Registry.ITEM;
    }

    public void add(ItemConvertible provider, Function<Item, ModelGen> factory) {
        Item item = provider.asItem();
        ModelGen gen = factory.apply(item);
        this.map.put(item, gen);
    }

    public void block(Block block) {
        this.add(block.asItem(), InheritingModelGen.inherit(name(block.asItem(), "block/%s")));
    }

    public void generated(Item item) {
        this.add(item, this::generatedItem);
    }

    public void generated(Block block) {
        this.add(block, this::generatedBlock);
    }

    public <T> T using(Identifier name, Function<Identifier, T> func) {
        return func.apply(name);
    }
    public ModelGen inherit(Identifier name) {
        return InheritingModelGen.inherit(name);
    }

    public ModelGen generatedItem(Item item) {
        return InheritingModelGen.generated(name(item, "item/%s"));
    }
    public ModelGen generatedBlock(Item item) {
        return InheritingModelGen.generated(name(item, "block/%s"));
    }

    public ModelGen wall(Item item) {
        return InheritingModelGen.wallInventory(name(item, "block/%s", "_wall"));
    }
    public ModelGen wallPlural(Item item) {
        return InheritingModelGen.wallInventory(name(item, "block/%ss", "_wall"));
    }

    public ModelGen spawnEgg() {
        return InheritingModelGen.inherit("item/template_spawn_egg");
    }
}
