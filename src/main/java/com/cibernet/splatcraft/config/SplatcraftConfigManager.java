package com.cibernet.splatcraft.config;

import com.cibernet.splatcraft.Splatcraft;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;

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

        try (PrintWriter out = new PrintWriter(FILE)) {
            out.println(jsonObject.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    public static void load() {
        try {
            String json = new String(Files.readAllBytes(FILE.toPath()));
            if (!json.equals("")) {
                JsonObject jsonObject = (JsonObject) new JsonParser().parse(json);

                SplatcraftConfig.RenderGroup RENDER = SplatcraftConfig.RENDER;
                RENDER.holdStageBarrierToRender.value = jsonObject.getAsJsonPrimitive(RENDER.holdStageBarrierToRender.getId()).getAsBoolean();
                RENDER.barrierRenderDistance.value = jsonObject.getAsJsonPrimitive(RENDER.barrierRenderDistance.getId()).getAsInt();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
