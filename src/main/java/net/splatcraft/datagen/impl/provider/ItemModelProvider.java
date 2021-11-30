package net.splatcraft.datagen.impl.provider;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import net.splatcraft.datagen.impl.DataType;
import net.splatcraft.datagen.impl.generator.model.item.AbstractItemModelGenerator;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ItemModelProvider extends AbstractDataProvider<Supplier<AbstractItemModelGenerator>> {
    public ItemModelProvider(DataGenerator root) {
        super(root);
    }

    @Override
    public String getName() {
        return "Item Models";
    }

    @Override
    public String getFolder() {
        return "models/item";
    }

    @Override
    public DataType getDataType() {
        return DataType.ASSET;
    }

    @Override
    public List<Supplier<AbstractItemModelGenerator>> getGenerators() {
        return List.of();
    }

    @Override
    public Map<Identifier, JsonElement> createFileMap() {
        Map<Identifier, JsonElement> map = Maps.newHashMap();
        for (Supplier<AbstractItemModelGenerator> generator : this.getGenerators()) {
            generator.get().accept((item, modelGen) -> {
                Identifier id = Registry.ITEM.getId(item);
                if (map.put(id, modelGen.makeJson(id)) != null) {
                    throw new IllegalStateException("Duplicate model " + id);
                }
            });
        }
        return map;
    }
}
