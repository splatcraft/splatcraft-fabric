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
        SplatcraftConfig.UIGroup UI = SplatcraftConfig.UI;
        jsonObject.addProperty(UI.invisibleHotbarWhenSquid.getId(), UI.invisibleHotbarWhenSquid.getBoolean());
        jsonObject.addProperty(UI.invisibleHotbarStatusBarsShift.getId(), UI.invisibleHotbarStatusBarsShift.getInt());
        jsonObject.addProperty(UI.invisibleCrosshairWhenSquid.getId(), UI.invisibleCrosshairWhenSquid.getBoolean());
        jsonObject.addProperty(UI.inkColoredCrosshairWhenSquid.getId(), UI.inkColoredCrosshairWhenSquid.getBoolean());
        jsonObject.addProperty(UI.inkAmountIndicator.getId(), UI.inkAmountIndicator.getString());
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
                SplatcraftConfig.UIGroup UI = SplatcraftConfig.UI;
                UI.invisibleHotbarWhenSquid.value = SplatcraftConfigManager.load(jsonObject, UI.invisibleHotbarWhenSquid).getAsBoolean();
                UI.invisibleHotbarStatusBarsShift.value = SplatcraftConfigManager.load(jsonObject, UI.invisibleHotbarStatusBarsShift).getAsInt();
                UI.invisibleCrosshairWhenSquid.value = SplatcraftConfigManager.load(jsonObject, UI.invisibleCrosshairWhenSquid).getAsBoolean();
                UI.inkColoredCrosshairWhenSquid.value = SplatcraftConfigManager.load(jsonObject, UI.inkColoredCrosshairWhenSquid).getAsBoolean();
                UI.inkAmountIndicator.value = SplatcraftConfig.UIGroup.InkAmountIndicator.valueOf(SplatcraftConfigManager.load(jsonObject, UI.inkAmountIndicator).getAsString());
                SplatcraftConfig.ColorsGroup COLORS = SplatcraftConfig.COLORS;
                COLORS.colorLock.value = SplatcraftConfigManager.load(jsonObject, COLORS.colorLock).getAsBoolean();
            }
        } catch (IOException ignored) {
            Splatcraft.log(Level.ERROR, "Configuration failed to load as the configuration file is not present. Go into the configuration and change a setting!");
        } catch (NullPointerException e) {
            Splatcraft.log(Level.WARN, "Configuration failed to load fully from file due to " + e.toString() + ". This is probably just a configuration update.");
        }
    }
    private static JsonPrimitive load(JsonObject jsonObject, SplatcraftConfig.Option option) {
        try {
            return jsonObject.getAsJsonPrimitive(option.getId());
        } catch (RuntimeException e) {
            Object optionDefault = option.getDefault();
            System.out.println(option.getId() + " is not present! Defaulting to " + optionDefault);
            if (optionDefault instanceof Boolean) {
                return new JsonPrimitive((Boolean) optionDefault);
            } else if (optionDefault instanceof Integer) {
                return new JsonPrimitive((Integer) optionDefault);
            } else if (optionDefault instanceof Enum<?>) {
                return new JsonPrimitive(String.valueOf(optionDefault));
            }

            return null;
        }
    }
}
