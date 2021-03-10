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
        jsonObject.addProperty(UI.modifyFovForSquidForm.getId(), UI.modifyFovForSquidForm.getBoolean());
        jsonObject.addProperty(UI.fovForSquidForm.getId(), UI.fovForSquidForm.getInt());
        jsonObject.addProperty(UI.invisibleHotbarWhenSquid.getId(), UI.invisibleHotbarWhenSquid.getBoolean());
        jsonObject.addProperty(UI.renderHeldItemWhenHotbarInvisible.getId(), UI.renderHeldItemWhenHotbarInvisible.getBoolean());
        jsonObject.addProperty(UI.invisibleHotbarStatusBarsShift.getId(), UI.invisibleHotbarStatusBarsShift.getInt());
        jsonObject.addProperty(UI.invisibleCrosshairWhenSquid.getId(), UI.invisibleCrosshairWhenSquid.getBoolean());
        SplatcraftConfig.InkGroup INK = SplatcraftConfig.INK;
        jsonObject.addProperty(INK.colorLock.getId(), INK.colorLock.getBoolean());
        // jsonObject.addProperty(INK.dynamicInkDurabilityColor.getId(), INK.dynamicInkDurabilityColor.getBoolean());
        jsonObject.addProperty(INK.inkColoredCrosshairWhenSquid.getId(), INK.inkColoredCrosshairWhenSquid.getBoolean());
        jsonObject.addProperty(INK.inkAmountIndicator.getId(), INK.inkAmountIndicator.getString());
        jsonObject.addProperty(INK.inkAmountIndicatorAlwaysVisible.getId(), INK.inkAmountIndicatorAlwaysVisible.getBoolean());
        jsonObject.addProperty(INK.inkAmountIndicatorExclamations.getId(), INK.inkAmountIndicatorExclamations.getBoolean());
        jsonObject.addProperty(INK.inkAmountIndicatorExclamationsMin.getId(), INK.inkAmountIndicatorExclamationsMin.getInt());
        jsonObject.addProperty(INK.inkAmountIndicatorExclamationsMax.getId(), INK.inkAmountIndicatorExclamationsMax.getInt());

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
                UI.modifyFovForSquidForm.value = SplatcraftConfigManager.load(jsonObject, UI.modifyFovForSquidForm).getAsBoolean();
                UI.fovForSquidForm.value = SplatcraftConfigManager.load(jsonObject, UI.fovForSquidForm).getAsInt();
                UI.invisibleHotbarWhenSquid.value = SplatcraftConfigManager.load(jsonObject, UI.invisibleHotbarWhenSquid).getAsBoolean();
                UI.renderHeldItemWhenHotbarInvisible.value = SplatcraftConfigManager.load(jsonObject, UI.renderHeldItemWhenHotbarInvisible).getAsBoolean();
                UI.invisibleHotbarStatusBarsShift.value = SplatcraftConfigManager.load(jsonObject, UI.invisibleHotbarStatusBarsShift).getAsInt();
                UI.invisibleCrosshairWhenSquid.value = SplatcraftConfigManager.load(jsonObject, UI.invisibleCrosshairWhenSquid).getAsBoolean();
                SplatcraftConfig.InkGroup INK = SplatcraftConfig.INK;
                INK.colorLock.value = SplatcraftConfigManager.load(jsonObject, INK.colorLock).getAsBoolean();
                // INK.dynamicInkDurabilityColor.value = SplatcraftConfigManager.load(jsonObject, INK.dynamicInkDurabilityColor).getAsBoolean();
                INK.inkColoredCrosshairWhenSquid.value = SplatcraftConfigManager.load(jsonObject, INK.inkColoredCrosshairWhenSquid).getAsBoolean();
                INK.inkAmountIndicator.value = InkAmountIndicator.valueOf(SplatcraftConfigManager.load(jsonObject, INK.inkAmountIndicator).getAsString());
                INK.inkAmountIndicatorAlwaysVisible.value = SplatcraftConfigManager.load(jsonObject, INK.inkAmountIndicatorAlwaysVisible).getAsBoolean();
                INK.inkAmountIndicatorExclamations.value = SplatcraftConfigManager.load(jsonObject, INK.inkAmountIndicatorExclamations).getAsBoolean();
                INK.inkAmountIndicatorExclamationsMin.value = SplatcraftConfigManager.load(jsonObject, INK.inkAmountIndicatorExclamationsMin).getAsInt();
                INK.inkAmountIndicatorExclamationsMax.value = SplatcraftConfigManager.load(jsonObject, INK.inkAmountIndicatorExclamationsMax).getAsInt();
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
