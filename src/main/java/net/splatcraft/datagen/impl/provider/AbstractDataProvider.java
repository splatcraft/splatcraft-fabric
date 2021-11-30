package net.splatcraft.datagen.impl.provider;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import net.splatcraft.datagen.impl.DataType;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public abstract class AbstractDataProvider<T> implements DataProvider {
    protected static final Logger LOGGER = LogManager.getLogger();
    protected static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    protected final DataGenerator root;

    public AbstractDataProvider(DataGenerator root) {
        this.root = root;
    }

    public abstract String getName();
    public abstract String getFolder();
    public abstract DataType getDataType();

    public abstract List<T> getGenerators();
    public abstract Map<Identifier, JsonElement> createFileMap();

    @Override
    public void run(DataCache cache) {
        this.write(cache, this.createFileMap());
    }

    public void write(DataCache cache, Map<Identifier, JsonElement> map, BiFunction<Path, Identifier, Path> pathCreator) {
        Path path = this.root.getOutput();
        map.forEach((id, json) -> {
            Path output = pathCreator.apply(path, id);
            try {
                DataProvider.writeToPath(GSON, cache, json, output);
            } catch (IOException e) {
                LOGGER.error("Couldn't save {} {}", this.getFolder(), output, e);
            }
        });
    }

    public void write(DataCache cache, Map<Identifier, JsonElement> map) {
        this.write(cache, map, this::getOutput);
    }

    public Path getOutput(Path root, Identifier id, DataType type, String folder) {
        return root.resolve(String.format("%s/%s/%s/%s.json", type.getId(), id.getNamespace(), folder, id.getPath()));
    }

    public Path getOutput(Path root, Identifier id, String folder) {
        return this.getOutput(root, id, this.getDataType(), folder);
    }

    public Path getOutput(Path root, Identifier id, DataType type) {
        return this.getOutput(root, id, type, this.getFolder());
    }

    public Path getOutput(Path root, Identifier id) {
        return this.getOutput(root, id, this.getDataType(), this.getFolder());
    }
}
