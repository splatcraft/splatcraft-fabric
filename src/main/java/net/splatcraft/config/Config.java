package net.splatcraft.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonWriter;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import net.splatcraft.config.option.Option;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

public class Config {
    private final File file;
    private final File oldFile;
    private final HashMap<Identifier, Option<?>> map = new HashMap<>();

    public Config(File file) {
        this.file = file;
        this.oldFile = new File(this.file.getAbsolutePath() + "_old");
    }

    public <T, O extends Option<T>> O add(Identifier id, O option) {
        this.map.put(id, option);
        return option;
    }

    public void save() {
        JsonObject jsonObject = new JsonObject();

        this.map.forEach((id, option) -> jsonObject.add(String.valueOf(id), option.toJson()));

        File folder = this.file.getParentFile();
        if (folder.exists() || folder.mkdirs()) {
            try (PrintWriter out = new PrintWriter(this.file)) {
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
            Files.copy(this.file.toPath(), this.oldFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            String json = new String(Files.readAllBytes(this.file.toPath()));
            if (!json.isEmpty()) {
                JsonObject jsonObject = (JsonObject) JsonParser.parseString(json);
                for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                    String id = entry.getKey();
                    JsonElement jsonElement = entry.getValue();

                    Identifier identifier = Identifier.tryParse(id);
                    Option<?> option = this.map.get(identifier);
                    if (option != null) option.fromJson(jsonElement);
                }
            }
        } catch (NoSuchFileException | FileNotFoundException e) {
            save();
            e.printStackTrace();
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
