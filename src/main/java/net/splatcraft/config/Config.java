package net.splatcraft.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonWriter;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import net.splatcraft.Splatcraft;
import net.splatcraft.config.option.Option;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class Config {
    private final File file;
    private final File backupFile;
    private final HashMap<Identifier, Option<?>> map = new HashMap<>();

    private JsonObject oldJson;

    public Config(File file) {
        this.file = file;
        this.backupFile = new File(this.file.getAbsolutePath() + "_old");
    }

    protected <T, O extends Option<T>> O add(Identifier id, O option) {
        this.map.put(id, option);
        return option;
    }

    protected <T, O extends Option<T>> O add(String id, O option) {
        return this.add(new Identifier(Splatcraft.MOD_ID, id), option);
    }

    @Environment(EnvType.CLIENT)
    public void addConfigListEntries(ConfigEntryBuilder entryBuilder, Supplier<ConfigCategory> categoryCreator) {
        if (this.canDisplayInMenu()) {
            ConfigCategory category = categoryCreator.get();
            for (Map.Entry<Identifier, Option<?>> entry : this.map.entrySet()) {
                Identifier id = entry.getKey();
                Option<?> option = entry.getValue();
                category.addEntry(option.createConfigListEntry(id, entryBuilder));
            }
        }
    }

    @Environment(EnvType.CLIENT)
    public boolean canDisplayInMenu() {
        return !this.map.isEmpty();
    }

    public void save() {
        File folder = this.file.getParentFile();
        if (folder.exists() || folder.mkdirs()) {
            try (PrintWriter out = new PrintWriter(this.file)) {
                JsonObject jsonObject = this.oldJson == null ? new JsonObject() : this.oldJson.deepCopy();
                this.map.forEach((id, option) -> jsonObject.add(id.toString(), option.toJson()));

                StringWriter writer = new StringWriter();
                Streams.write(jsonObject, createJsonWriter(writer));
                out.println(writer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            throw new RuntimeException("Fatal error! Could not find config %s".formatted(this.file));
        }
    }

    public void load() {
        try {
            Files.copy(this.file.toPath(), this.backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (NoSuchFileException | FileNotFoundException e) {
            save();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            String json = new String(Files.readAllBytes(this.file.toPath()));
            if (!json.isEmpty()) {
                JsonObject jsonObject = (JsonObject) JsonParser.parseString(json);

                this.oldJson = jsonObject;
                Set<Identifier> loadedConfigs = new HashSet<>();

                for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                    String id = entry.getKey();
                    JsonElement jsonElement = entry.getValue();

                    Identifier identifier = Identifier.tryParse(id);
                    Option<?> option = this.map.get(identifier);
                    if (option != null) {
                        option.fromJson(jsonElement);
                        loadedConfigs.add(identifier);
                    }
                }

                if (!loadedConfigs.equals(this.map.keySet())) save();
            } else {
                save();
            }
        } catch (NoSuchFileException | FileNotFoundException e) {
            save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static JsonWriter createJsonWriter(StringWriter stringWriter) {
        JsonWriter jsonWriter = new JsonWriter(stringWriter);
        jsonWriter.setLenient(true);
        jsonWriter.setIndent("  ");
        return jsonWriter;
    }

    public static File createFile(String name) {
        Path configDir = FabricLoader.getInstance().getConfigDir();
        return new File(new File(String.valueOf(configDir)), "%s.json".formatted(name));
    }
}
