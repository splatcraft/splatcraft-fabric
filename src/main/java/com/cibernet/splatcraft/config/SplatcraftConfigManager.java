package com.cibernet.splatcraft.config;

import com.cibernet.splatcraft.Splatcraft;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;

@Environment(EnvType.CLIENT)
public class SplatcraftConfigManager {
    private static final File FILE = FabricLoader.getInstance().getConfigDir().toFile().toPath().resolve(Splatcraft.MOD_ID + ".json").toFile();

    public static void save() {
        JsonObject jsonObject = new JsonObject();

        SplatcraftConfig.RenderGroup RENDER = SplatcraftConfig.RENDER;
        jsonObject.addProperty(RENDER.holdStageBarrierToRender.getId(), RENDER.holdStageBarrierToRender.getBoolean());
        jsonObject.addProperty(RENDER.barrierRenderDistance.getId(), RENDER.barrierRenderDistance.getInt());
        jsonObject.addProperty(RENDER.inkedBlocksColorLayerIsTransparent.getId(), RENDER.inkedBlocksColorLayerIsTransparent.getBoolean());
        SplatcraftConfig.ColorsGroup COLORS = SplatcraftConfig.COLORS;
        jsonObject.addProperty(COLORS.colorLock.getId(), COLORS.colorLock.getBoolean());

        try (PrintWriter out = new PrintWriter(FILE)) {
            out.println(jsonObject.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("ConstantConditions")
    public static void load() {
        try {
            String json = new String(Files.readAllBytes(FILE.toPath()));
            if (!json.equals("")) {
                JsonObject jsonObject = (JsonObject) new JsonParser().parse(json);

                SplatcraftConfig.RenderGroup RENDER = SplatcraftConfig.RENDER;
                RENDER.holdStageBarrierToRender.value = SplatcraftConfigManager.load(jsonObject, RENDER.holdStageBarrierToRender).getAsBoolean();
                RENDER.barrierRenderDistance.value = SplatcraftConfigManager.load(jsonObject, RENDER.barrierRenderDistance).getAsInt();
                RENDER.inkedBlocksColorLayerIsTransparent.value = SplatcraftConfigManager.load(jsonObject, RENDER.inkedBlocksColorLayerIsTransparent).getAsBoolean();
                SplatcraftConfig.ColorsGroup COLORS = SplatcraftConfig.COLORS;
                COLORS.colorLock.value = SplatcraftConfigManager.load(jsonObject, COLORS.colorLock).getAsBoolean();
            }
        } catch (IOException e) {
            Splatcraft.log(Level.ERROR, "Configuration failed to load due to " + e.toString());
            e.printStackTrace();
        } catch (NullPointerException e) {
            Splatcraft.log(Level.WARN, "Configuration failed to load fully from file due to " + e.toString() + ". This is probably just a configuration update.");
        }
    }

    private static JsonPrimitive load(JsonObject jsonObject, SplatcraftConfig.Option id) {
        try {
            return jsonObject.getAsJsonPrimitive(id.getId());
        } catch (RuntimeException e) {
            Object optionDefault = id.getDefault();
            if (optionDefault instanceof Boolean) {
                return new JsonPrimitive((Boolean) optionDefault);
            } else if (optionDefault instanceof Integer) {
                return new JsonPrimitive((Integer) optionDefault);
            }

            return null;
        }
    }
}
