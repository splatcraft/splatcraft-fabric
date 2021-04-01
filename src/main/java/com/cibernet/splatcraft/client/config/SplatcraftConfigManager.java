package com.cibernet.splatcraft.client.config;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.client.config.enums.InkAmountIndicator;
import com.cibernet.splatcraft.client.config.enums.PreventBobView;
import com.cibernet.splatcraft.client.config.enums.SquidFormKeyBehavior;
import com.cibernet.splatcraft.client.network.SplatcraftClientNetworking;
import com.cibernet.splatcraft.component.PlayerDataComponent;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import me.andante.chord.client.config.EnumOption;
import me.andante.chord.client.config.Option;
import me.andante.chord.client.config.RangedOption;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class SplatcraftConfigManager {
    private static final File FILE = FabricLoader.getInstance().getConfigDir().toFile().toPath().resolve(Splatcraft.MOD_ID + ".json").toFile();
    public static final List<Option<?>> OPTIONS = new LinkedList<>();

    public static void save() {
        JsonObject jsonObject = new JsonObject();
        OPTIONS.forEach(option -> jsonObject.addProperty(option.getId(), option.getValueForSave()));

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
            if (!json.isEmpty()) {
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
                UI.renderPaperDollWhenSignalling.value = SplatcraftConfigManager.load(jsonObject, UI.renderPaperDollWhenSignalling).getAsBoolean();
                SplatcraftConfig.InkGroup INK = SplatcraftConfig.INK;
                // INK.dynamicInkDurabilityColor.value = SplatcraftConfigManager.load(jsonObject, INK.dynamicInkDurabilityColor).getAsBoolean();
                INK.inkColoredCrosshairWhenSquid.value = SplatcraftConfigManager.load(jsonObject, INK.inkColoredCrosshairWhenSquid).getAsBoolean();
                INK.inkAmountIndicator.value = InkAmountIndicator.valueOf(SplatcraftConfigManager.load(jsonObject, INK.inkAmountIndicator).getAsString());
                INK.inkAmountIndicatorAlwaysVisible.value = SplatcraftConfigManager.load(jsonObject, INK.inkAmountIndicatorAlwaysVisible).getAsBoolean();
                INK.inkAmountIndicatorExclamations.value = SplatcraftConfigManager.load(jsonObject, INK.inkAmountIndicatorExclamations).getAsBoolean();
                INK.inkAmountIndicatorExclamationsMin.value = SplatcraftConfigManager.load(jsonObject, INK.inkAmountIndicatorExclamationsMin).getAsInt();
                INK.inkAmountIndicatorExclamationsMax.value = SplatcraftConfigManager.load(jsonObject, INK.inkAmountIndicatorExclamationsMax).getAsInt();
                SplatcraftConfig.AccessibilityGroup ACCESSIBILITY = SplatcraftConfig.ACCESSIBILITY;
                ACCESSIBILITY.squidFormKeyBehavior.value = SquidFormKeyBehavior.valueOf(SplatcraftConfigManager.load(jsonObject, ACCESSIBILITY.squidFormKeyBehavior).getAsString());
                ACCESSIBILITY.colorLock.value = SplatcraftConfigManager.load(jsonObject, ACCESSIBILITY.colorLock).getAsBoolean();
                ACCESSIBILITY.colorLockFriendly.value = SplatcraftConfigManager.load(jsonObject, ACCESSIBILITY.colorLockFriendly).getAsInt();
                ACCESSIBILITY.colorLockHostile.value = SplatcraftConfigManager.load(jsonObject, ACCESSIBILITY.colorLockHostile).getAsInt();
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
    private static JsonPrimitive load(JsonObject jsonObject, Option<?> option) {
        try {
            return jsonObject.getAsJsonPrimitive(option.getId());
        } catch (RuntimeException e) {
            Object optionDefault = option.getDefault();
            Splatcraft.log(Level.WARN, option.getId() + " is not present! Defaulting to " + optionDefault);
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

    public static Screen createScreen(Screen parentScreen) {
        ConfigBuilder builder = ConfigBuilder.create()
            .setParentScreen(parentScreen)
            .setDefaultBackgroundTexture(new Identifier(Splatcraft.MOD_ID, "textures/block/inked_block.png"))
            .setTitle(createConfigText("title"))
            .setSavingRunnable(SplatcraftConfigManager::save);

        builder.setGlobalized(true);
        builder.setGlobalizedExpanded(false);

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        //
        // RENDER CATEGORY
        //

        ConfigCategory RENDER = builder.getOrCreateCategory(createRenderText());
        TranslatableText holdStageBarrierToRender = createRenderText(SplatcraftConfig.RENDER.holdStageBarrierToRender.getId());
        TranslatableText barrierRenderDistance = createRenderText(SplatcraftConfig.RENDER.barrierRenderDistance.getId());
        TranslatableText inkedBlocksColorLayerIsTransparent = createRenderText(SplatcraftConfig.RENDER.inkedBlocksColorLayerIsTransparent.getId());
        Option<Boolean> holdStageBarrierToRenderOption = SplatcraftConfig.RENDER.holdStageBarrierToRender;
        RangedOption<Integer> barrierRenderDistanceOption = SplatcraftConfig.RENDER.barrierRenderDistance;
        Option<Boolean> inkedBlocksColorLayerIsTransparentOption = SplatcraftConfig.RENDER.inkedBlocksColorLayerIsTransparent;
        RENDER.addEntry(
            entryBuilder.startBooleanToggle(holdStageBarrierToRender, holdStageBarrierToRenderOption.value)
                .setDefaultValue(holdStageBarrierToRenderOption.getDefault())
                .setSaveConsumer(value -> holdStageBarrierToRenderOption.value = value)
                .setTooltip(createTooltip(holdStageBarrierToRender))
                .build()
        ).addEntry(
            entryBuilder.startIntSlider(barrierRenderDistance, barrierRenderDistanceOption.value, barrierRenderDistanceOption.getMin(), barrierRenderDistanceOption.getMax())
                .setDefaultValue(barrierRenderDistanceOption.getDefault())
                .setSaveConsumer(value -> barrierRenderDistanceOption.value = value)
                .setTooltip(createTooltip(barrierRenderDistance))
                .build()
        ).addEntry(
            entryBuilder.startBooleanToggle(inkedBlocksColorLayerIsTransparent, inkedBlocksColorLayerIsTransparentOption.value)
                .setDefaultValue(inkedBlocksColorLayerIsTransparentOption.getDefault())
                .setSaveConsumer(value -> {
                    if (inkedBlocksColorLayerIsTransparentOption.value != value) {
                        MinecraftClient.getInstance().worldRenderer.reload();
                    }
                    inkedBlocksColorLayerIsTransparentOption.value = value;
                })
                .setTooltip(createTooltip(inkedBlocksColorLayerIsTransparent))
                .build()
        );

        //
        // UI CATEGORY
        //

        ConfigCategory UI = builder.getOrCreateCategory(createUIText());
        TranslatableText preventBobViewWhenSquid = createUIText(SplatcraftConfig.UI.preventBobViewWhenSquid.getId());
        EnumOption<PreventBobView> preventBobViewWhenSquidOption = SplatcraftConfig.UI.preventBobViewWhenSquid;
        TranslatableText modifyFovForSquidForm = createUIText(SplatcraftConfig.UI.modifyFovForSquidForm.getId());
        Option<Boolean> modifyFovForSquidFormOption = SplatcraftConfig.UI.modifyFovForSquidForm;
        TranslatableText fovForSquidForm = createUIText(SplatcraftConfig.UI.fovForSquidForm.getId());
        RangedOption<Integer> fovForSquidFormOption = SplatcraftConfig.UI.fovForSquidForm;
        TranslatableText invisibleHotbarWhenSquid = createUIText(SplatcraftConfig.UI.invisibleHotbarWhenSquid.getId());
        Option<Boolean> invisibleHotbarWhenSquidOption = SplatcraftConfig.UI.invisibleHotbarWhenSquid;
        TranslatableText renderHeldItemWhenHotbarInvisible = createUIText(SplatcraftConfig.UI.renderHeldItemWhenHotbarInvisible.getId());
        Option<Boolean> renderHeldItemWhenHotbarInvisibleOption = SplatcraftConfig.UI.renderHeldItemWhenHotbarInvisible;
        TranslatableText invisibleHotbarStatusBarsShift = createUIText(SplatcraftConfig.UI.invisibleHotbarStatusBarsShift.getId());
        Option<Integer> invisibleHotbarStatusBarsShiftOption = SplatcraftConfig.UI.invisibleHotbarStatusBarsShift;
        TranslatableText invisibleCrosshairWhenSquid = createUIText(SplatcraftConfig.UI.invisibleCrosshairWhenSquid.getId());
        Option<Boolean> invisibleCrosshairWhenSquidOption = SplatcraftConfig.UI.invisibleCrosshairWhenSquid;
        TranslatableText renderPaperDollWhenSignalling = createUIText(SplatcraftConfig.UI.renderPaperDollWhenSignalling.getId());
        Option<Boolean> renderPaperDollWhenSignallingOption = SplatcraftConfig.UI.renderPaperDollWhenSignalling;
        UI.addEntry(
            entryBuilder.startEnumSelector(preventBobViewWhenSquid, preventBobViewWhenSquidOption.getClazz(), preventBobViewWhenSquidOption.value)
                .setDefaultValue(preventBobViewWhenSquidOption.getDefault())
                .setSaveConsumer(value -> preventBobViewWhenSquidOption.value = value)
                .setTooltip(createTooltip(preventBobViewWhenSquid))
                .build()
        ).addEntry(
            entryBuilder.startBooleanToggle(modifyFovForSquidForm, modifyFovForSquidFormOption.value)
                .setDefaultValue(modifyFovForSquidFormOption.getDefault())
                .setSaveConsumer(value -> modifyFovForSquidFormOption.value = value)
                .setTooltip(createTooltip(modifyFovForSquidForm))
                .build()
        ).addEntry(
            entryBuilder.startIntSlider(fovForSquidForm, fovForSquidFormOption.value, fovForSquidFormOption.getMin(), fovForSquidFormOption.getMax())
                .setDefaultValue(fovForSquidFormOption.getDefault())
                .setSaveConsumer(value -> fovForSquidFormOption.value = value)
                .setTooltip(createTooltip(fovForSquidForm))
                .build()
        ).addEntry(
            entryBuilder.startBooleanToggle(invisibleHotbarWhenSquid, invisibleHotbarWhenSquidOption.value)
                .setDefaultValue(invisibleHotbarWhenSquidOption.getDefault())
                .setSaveConsumer(value -> invisibleHotbarWhenSquidOption.value = value)
                .setTooltip(createTooltip(invisibleHotbarWhenSquid))
                .build()
        ).addEntry(
            entryBuilder.startBooleanToggle(renderHeldItemWhenHotbarInvisible, renderHeldItemWhenHotbarInvisibleOption.value)
                .setDefaultValue(renderHeldItemWhenHotbarInvisibleOption.getDefault())
                .setSaveConsumer(value -> renderHeldItemWhenHotbarInvisibleOption.value = value)
                .setTooltip(createTooltip(renderHeldItemWhenHotbarInvisible))
                .build()
        ).addEntry(
            entryBuilder.startIntField(invisibleHotbarStatusBarsShift, invisibleHotbarStatusBarsShiftOption.value)
                .setDefaultValue(invisibleHotbarStatusBarsShiftOption.getDefault())
                .setSaveConsumer(value -> invisibleHotbarStatusBarsShiftOption.value = value)
                .setTooltip(createTooltip(invisibleHotbarStatusBarsShift))
                .build()
        ).addEntry(
            entryBuilder.startBooleanToggle(invisibleCrosshairWhenSquid, invisibleCrosshairWhenSquidOption.value)
                .setDefaultValue(invisibleCrosshairWhenSquidOption.getDefault())
                .setSaveConsumer(value -> invisibleCrosshairWhenSquidOption.value = value)
                .setTooltip(createTooltip(invisibleCrosshairWhenSquid))
                .build()
        ).addEntry(
            entryBuilder.startBooleanToggle(renderPaperDollWhenSignalling, renderPaperDollWhenSignallingOption.value)
                .setDefaultValue(renderPaperDollWhenSignallingOption.getDefault())
                .setSaveConsumer(value -> renderPaperDollWhenSignallingOption.value = value)
                .setTooltip(createTooltip(renderPaperDollWhenSignalling))
                .build()
        );

        //
        // COLORS CATEGORY
        //

        ConfigCategory INK = builder.getOrCreateCategory(createInkText());
        EnumOption<InkAmountIndicator> inkAmountIndicatorOption = SplatcraftConfig.INK.inkAmountIndicator;
        TranslatableText inkColoredCrosshairWhenSquid = createInkText(SplatcraftConfig.INK.inkColoredCrosshairWhenSquid.getId());
        Option<Boolean> inkColoredCrosshairWhenSquidOption = SplatcraftConfig.INK.inkColoredCrosshairWhenSquid;
        TranslatableText inkAmountIndicator = createInkText(SplatcraftConfig.INK.inkAmountIndicator.getId());
        TranslatableText inkAmountIndicatorAlwaysVisible = createInkText(SplatcraftConfig.INK.inkAmountIndicatorAlwaysVisible.getId());
        Option<Boolean> inkAmountIndicatorAlwaysVisibleOption = SplatcraftConfig.INK.inkAmountIndicatorAlwaysVisible;
        TranslatableText inkAmountIndicatorExclamations = createInkText(SplatcraftConfig.INK.inkAmountIndicatorExclamations.getId());
        Option<Boolean> inkAmountIndicatorExclamationsOption = SplatcraftConfig.INK.inkAmountIndicatorExclamations;
        TranslatableText inkAmountIndicatorExclamationsMin = createInkText(SplatcraftConfig.INK.inkAmountIndicatorExclamationsMin.getId());
        RangedOption<Integer> inkAmountIndicatorExclamationsMinOption = SplatcraftConfig.INK.inkAmountIndicatorExclamationsMin;
        TranslatableText inkAmountIndicatorExclamationsMax = createInkText(SplatcraftConfig.INK.inkAmountIndicatorExclamationsMax.getId());
        RangedOption<Integer> inkAmountIndicatorExclamationsMaxOption = SplatcraftConfig.INK.inkAmountIndicatorExclamationsMax;
        INK.addEntry(
            entryBuilder.startBooleanToggle(inkColoredCrosshairWhenSquid, inkColoredCrosshairWhenSquidOption.value)
                .setDefaultValue(inkColoredCrosshairWhenSquidOption.getDefault())
                .setSaveConsumer(value -> inkColoredCrosshairWhenSquidOption.value = value)
                .setTooltip(createTooltip(inkColoredCrosshairWhenSquid))
                .build()
        ).addEntry(
            entryBuilder.startEnumSelector(inkAmountIndicator, inkAmountIndicatorOption.getClazz(), inkAmountIndicatorOption.value)
                .setDefaultValue(inkAmountIndicatorOption.getDefault())
                .setSaveConsumer(value -> inkAmountIndicatorOption.value = value)
                .setTooltip(createTooltip(inkAmountIndicator))
                .build()
        ).addEntry(
            entryBuilder.startBooleanToggle(inkAmountIndicatorAlwaysVisible, inkAmountIndicatorAlwaysVisibleOption.value)
                .setDefaultValue(inkAmountIndicatorAlwaysVisibleOption.getDefault())
                .setSaveConsumer(value -> inkAmountIndicatorAlwaysVisibleOption.value = value)
                .setTooltip(createTooltip(inkAmountIndicatorAlwaysVisible))
                .build()
        ).addEntry(
            entryBuilder.startBooleanToggle(inkAmountIndicatorExclamations, inkAmountIndicatorExclamationsOption.value)
                .setDefaultValue(inkAmountIndicatorExclamationsOption.getDefault())
                .setSaveConsumer(value -> inkAmountIndicatorExclamationsOption.value = value)
                .setTooltip(createTooltip(inkAmountIndicatorExclamations))
                .build()
        ).addEntry(
            entryBuilder.startIntSlider(inkAmountIndicatorExclamationsMin, inkAmountIndicatorExclamationsMinOption.value, inkAmountIndicatorExclamationsMinOption.getMin(), inkAmountIndicatorExclamationsMinOption.getMax())
                .setDefaultValue(inkAmountIndicatorExclamationsMinOption.getDefault())
                .setSaveConsumer(value -> inkAmountIndicatorExclamationsMinOption.value = value)
                .setTooltip(createTooltip(inkAmountIndicatorExclamationsMin))
                .build()
        ).addEntry(
            entryBuilder.startIntSlider(inkAmountIndicatorExclamationsMax, inkAmountIndicatorExclamationsMaxOption.value, inkAmountIndicatorExclamationsMaxOption.getMin(), inkAmountIndicatorExclamationsMaxOption.getMax())
                .setDefaultValue(inkAmountIndicatorExclamationsMaxOption.getDefault())
                .setSaveConsumer(value -> inkAmountIndicatorExclamationsMaxOption.value = value)
                .setTooltip(createTooltip(inkAmountIndicatorExclamationsMax))
                .build()
        );

        //
        // ACCESSIBILITY CATEGORY
        //

        ConfigCategory ACCESSIBILITY = builder.getOrCreateCategory(createAccessibilityText());
        TranslatableText squidFormKeyBehavior = createAccessibilityText(SplatcraftConfig.ACCESSIBILITY.squidFormKeyBehavior.getId());
        EnumOption<SquidFormKeyBehavior> squidFormKeyBehaviorOption = SplatcraftConfig.ACCESSIBILITY.squidFormKeyBehavior;
        TranslatableText colorLock = createAccessibilityText(SplatcraftConfig.ACCESSIBILITY.colorLock.getId());
        Option<Boolean> colorLockOption = SplatcraftConfig.ACCESSIBILITY.colorLock;
        TranslatableText colorLockFriendly = createAccessibilityText(SplatcraftConfig.ACCESSIBILITY.colorLockFriendly.getId());
        Option<Integer> colorLockFriendlyOption = SplatcraftConfig.ACCESSIBILITY.colorLockFriendly;
        TranslatableText colorLockHostile = createAccessibilityText(SplatcraftConfig.ACCESSIBILITY.colorLockHostile.getId());
        Option<Integer> colorLockHostileOption = SplatcraftConfig.ACCESSIBILITY.colorLockHostile;
        ACCESSIBILITY.addEntry(
            entryBuilder.startEnumSelector(squidFormKeyBehavior, squidFormKeyBehaviorOption.getClazz(), squidFormKeyBehaviorOption.value)
                .setDefaultValue(squidFormKeyBehaviorOption.getDefault())
                .setSaveConsumer(value -> {
                    if (squidFormKeyBehaviorOption.value != value && value == SquidFormKeyBehavior.HOLD) {
                        ClientPlayerEntity player = MinecraftClient.getInstance().player;
                        if (PlayerDataComponent.isSquid(player)) {
                            SplatcraftClientNetworking.toggleSquidForm(player);
                        }
                    }
                    squidFormKeyBehaviorOption.value = value;
                })
                .setTooltip(createTooltip(squidFormKeyBehavior))
                .build()
        ).addEntry(
            entryBuilder.startBooleanToggle(colorLock, colorLockOption.value)
                .setDefaultValue(colorLockOption.getDefault())
                .setSaveConsumer(value -> {
                    if (colorLockOption.value != value) {
                        MinecraftClient.getInstance().worldRenderer.reload();
                    }
                    colorLockOption.value = value;
                })
                .setTooltip(createTooltip(colorLock))
                .build()
        ).addEntry(
            entryBuilder.startColorField(colorLockFriendly, colorLockFriendlyOption.value)
                .setDefaultValue(colorLockFriendlyOption.getDefault())
                .setSaveConsumer(value -> {
                    if (!colorLockFriendlyOption.value.equals(value)) {
                        MinecraftClient.getInstance().worldRenderer.reload();
                    }
                    colorLockFriendlyOption.value = value;
                })
                .setTooltip(createTooltip(colorLockFriendly))
                .build()
        ).addEntry(
            entryBuilder.startColorField(colorLockHostile, colorLockHostileOption.value)
                .setDefaultValue(colorLockHostileOption.getDefault())
                .setSaveConsumer(value -> {
                    if (!colorLockHostileOption.value.equals(value)) {
                        MinecraftClient.getInstance().worldRenderer.reload();
                    }
                    colorLockHostileOption.value = value;
                })
                .setTooltip(createTooltip(colorLockHostile))
                .build()
        );

        return builder.build();
    }

    private static TranslatableText createTooltip(TranslatableText text) {
        return new TranslatableText(text.getKey() + ".tooltip");
    }
    private static TranslatableText createRenderText(String label) {
        return createCatText("render" + (label.isEmpty() ? "" : "." + label));
    }
    private static TranslatableText createRenderText() {
        return createRenderText("");
    }
    private static TranslatableText createUIText(String label) {
        return createCatText("ui" + (label.isEmpty() ? "" : "." + label));
    }
    private static TranslatableText createUIText() {
        return createUIText("");
    }
    private static TranslatableText createInkText(String label) {
        return createCatText("ink" + (label.isEmpty() ? "" : "." + label));
    }
    private static TranslatableText createInkText() {
        return createInkText("");
    }
    private static TranslatableText createAccessibilityText(String label) {
        return createCatText("accessibility" + (label.isEmpty() ? "" : "." + label));
    }
    private static TranslatableText createAccessibilityText() {
        return createAccessibilityText("");
    }
    private static TranslatableText createCatText(String group) {
        return createConfigText("category." + group);
    }
    private static TranslatableText createConfigText(String label) {
        return new TranslatableText("config." + Splatcraft.MOD_ID + "." + label);
    }
}
