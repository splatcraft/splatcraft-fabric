package net.splatcraft.datagen.impl.provider;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import net.splatcraft.datagen.StateModelGenerator;
import net.splatcraft.datagen.impl.DataType;
import net.splatcraft.datagen.impl.generator.model.block.AbstractStateModelGenerator;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class StateModelProvider extends AbstractDataProvider<Supplier<AbstractStateModelGenerator>> {
    public StateModelProvider(DataGenerator root) {
        super(root);
    }

    @Override
    public String getName() {
        return "States and Block Models";
    }

    @Override
    public String getFolder() {
        return "blockstates";
    }

    @Override
    public DataType getDataType() {
        return DataType.ASSET;
    }

    @Override
    public List<Supplier<AbstractStateModelGenerator>> getGenerators() {
        return List.of(StateModelGenerator::new);
    }

    @Override
    public void run(DataCache cache) {
        super.run(cache);
        this.write(cache, this.createFileMapModels(), (root, id) -> this.getOutput(root, id, "models"));
    }

    @Override
    public Map<Identifier, JsonElement> createFileMap() {
        Map<Identifier, JsonElement> map = Maps.newHashMap();
        for (Supplier<AbstractStateModelGenerator> generator : this.getGenerators()) {
            generator.get().accept((block, stateGen) -> {
                Identifier id = Registry.BLOCK.getId(block);
                if (map.put(id, stateGen.makeJson(id, block)) != null) {
                    throw new IllegalStateException("Duplicate state " + id);
                }
            });
        }
        return map;
    }

    public Map<Identifier, JsonElement> createFileMapModels() {
        Map<Identifier, JsonElement> map = Maps.newHashMap();
        for (Supplier<AbstractStateModelGenerator> generator : this.getGenerators()) {
            generator.get().accept((block, stateGen) -> stateGen.getModels((id, modelGen) -> map.put(id, modelGen.makeJson(id))));
        }
        return map;
    }
}
