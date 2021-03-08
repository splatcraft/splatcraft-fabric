package com.cibernet.splatcraft.client.config;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.client.config.enums.InkAmountIndicator;
import com.cibernet.splatcraft.client.config.enums.PreventBobView;
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
        jsonObject.addProperty(UI.preventBobViewWhenSquid.getId(), UI.preventBobViewWhenSquid.getString());
        jsonObject.addProperty(UI.invisibleHotbarWhenSquid.getId(), UI.invisibleHotbarWhenSquid.getBoolean());
        jsonObject.addProperty(UI.renderHeldItemWhenHotbarInvisible.getId(), UI.renderHeldItemWhenHotbarInvisible.getBoolean());
        jsonObject.addProperty(UI.invisibleHotbarStatusBarsShift.getId(), UI.invisibleHotbarStatusBarsShift.getInt());
        jsonObject.addProperty(UI.invisibleCrosshairWhenSquid.getId(), UI.invisibleCrosshairWhenSquid.getBoolean());
        jsonObject.addProperty(UI.inkColoredCrosshairWhenSquid.getId(), UI.inkColoredCrosshairWhenSquid.getBoolean());
        jsonObject.addProperty(UI.inkAmountIndicator.getId(), UI.inkAmountIndicator.getString());
        jsonObject.addProperty(UI.inkAmountIndicatorAlwaysVisible.getId(), UI.inkAmountIndicatorAlwaysVisible.getBoolean());
        jsonObject.addProperty(UI.inkAmountIndicatorExclamations.getId(), UI.inkAmountIndicatorExclamations.getBoolean());
        jsonObject.addProperty(UI.inkAmountIndicatorExclamationsMin.getId(), UI.inkAmountIndicatorExclamationsMin.getInt());
        jsonObject.addProperty(UI.inkAmountIndicatorExclamationsMax.getId(), UI.inkAmountIndicatorExclamationsMax.getInt());
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
                UI.preventBobViewWhenSquid.value = PreventBobView.valueOf(SplatcraftConfigManager.load(jsonObject, UI.preventBobViewWhenSquid).getAsString());
                UI.invisibleHotbarWhenSquid.value = SplatcraftConfigManager.load(jsonObject, UI.invisibleHotbarWhenSquid).getAsBoolean();
                UI.renderHeldItemWhenHotbarInvisible.value = SplatcraftConfigManager.load(jsonObject, UI.renderHeldItemWhenHotbarInvisible).getAsBoolean();
                UI.invisibleHotbarStatusBarsShift.value = SplatcraftConfigManager.load(jsonObject, UI.invisibleHotbarStatusBarsShift).getAsInt();
                UI.invisibleCrosshairWhenSquid.value = SplatcraftConfigManager.load(jsonObject, UI.invisibleCrosshairWhenSquid).getAsBoolean();
                UI.inkColoredCrosshairWhenSquid.value = SplatcraftConfigManager.load(jsonObject, UI.inkColoredCrosshairWhenSquid).getAsBoolean();
                UI.inkAmountIndicator.value = InkAmountIndicator.valueOf(SplatcraftConfigManager.load(jsonObject, UI.inkAmountIndicator).getAsString());
                UI.inkAmountIndicatorAlwaysVisible.value = SplatcraftConfigManager.load(jsonObject, UI.inkAmountIndicatorAlwaysVisible).getAsBoolean();
                UI.inkAmountIndicatorExclamations.value = SplatcraftConfigManager.load(jsonObject, UI.inkAmountIndicatorExclamations).getAsBoolean();
                UI.inkAmountIndicatorExclamationsMin.value = SplatcraftConfigManager.load(jsonObject, UI.inkAmountIndicatorExclamationsMin).getAsInt();
                UI.inkAmountIndicatorExclamationsMax.value = SplatcraftConfigManager.load(jsonObject, UI.inkAmountIndicatorExclamationsMax).getAsInt();
                SplatcraftConfig.ColorsGroup COLORS = SplatcraftConfig.COLORS;
                COLORS.colorLock.value = SplatcraftConfigManager.load(jsonObject, COLORS.colorLock).getAsBoolean();
            }
        } catch (IOException ignored) {
            Splatcraft.log(Level.WARN, "Could not load configuration file! Saving and loading default values.");
            SplatcraftConfigManager.save();
        } catch (NullPointerException e) {
            Splatcraft.log(Level.WARN, "Configuration failed to load fully from file due to " + e.toString() + ". This is probably just a configuration update.");
        } catch (IllegalArgumentException e) {
            Splatcraft.log(Level.ERROR, "Configuration option failed to load: " + e.toString());
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
