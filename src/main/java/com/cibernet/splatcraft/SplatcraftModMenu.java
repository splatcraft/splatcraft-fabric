package com.cibernet.splatcraft;

import com.cibernet.splatcraft.client.config.SplatcraftConfig;
import com.cibernet.splatcraft.client.config.SplatcraftConfigManager;
import com.cibernet.splatcraft.client.config.enums.InkAmountIndicator;
import com.cibernet.splatcraft.client.config.enums.PreventBobView;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class SplatcraftModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parentScreen -> {
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
            SplatcraftConfig.Option holdStageBarrierToRenderOption = SplatcraftConfig.RENDER.holdStageBarrierToRender;
            SplatcraftConfig.RangedOption barrierRenderDistanceOption = SplatcraftConfig.RENDER.barrierRenderDistance;
            SplatcraftConfig.Option inkedBlocksColorLayerIsTransparentOption = SplatcraftConfig.RENDER.inkedBlocksColorLayerIsTransparent;
            RENDER.addEntry(
                entryBuilder.startBooleanToggle(holdStageBarrierToRender, holdStageBarrierToRenderOption.getBoolean())
                    .setDefaultValue(holdStageBarrierToRenderOption.getDefaultBoolean())
                    .setSaveConsumer(value -> holdStageBarrierToRenderOption.value = value)
                    .setTooltip(createTooltip(holdStageBarrierToRender))
                    .build()
            ).addEntry(
                entryBuilder.startIntSlider(barrierRenderDistance, barrierRenderDistanceOption.getInt(), barrierRenderDistanceOption.getMinInt(), barrierRenderDistanceOption.getMaxInt())
                    .setDefaultValue(barrierRenderDistanceOption.getDefaultInt())
                    .setSaveConsumer(value -> barrierRenderDistanceOption.value = value)
                    .setTooltip(createTooltip(barrierRenderDistance))
                    .build()
            ).addEntry(
                entryBuilder.startBooleanToggle(inkedBlocksColorLayerIsTransparent, inkedBlocksColorLayerIsTransparentOption.getBoolean())
                    .setDefaultValue(inkedBlocksColorLayerIsTransparentOption.getDefaultBoolean())
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
            SplatcraftConfig.EnumOption<PreventBobView> preventBobViewWhenSquidOption = SplatcraftConfig.UI.preventBobViewWhenSquid;
            TranslatableText modifyFovForSquidForm = createUIText(SplatcraftConfig.UI.modifyFovForSquidForm.getId());
            SplatcraftConfig.Option modifyFovForSquidFormOption = SplatcraftConfig.UI.modifyFovForSquidForm;
            TranslatableText fovForSquidForm = createUIText(SplatcraftConfig.UI.fovForSquidForm.getId());
            SplatcraftConfig.RangedOption fovForSquidFormOption = SplatcraftConfig.UI.fovForSquidForm;
            TranslatableText invisibleHotbarWhenSquid = createUIText(SplatcraftConfig.UI.invisibleHotbarWhenSquid.getId());
            SplatcraftConfig.Option invisibleHotbarWhenSquidOption = SplatcraftConfig.UI.invisibleHotbarWhenSquid;
            TranslatableText renderHeldItemWhenHotbarInvisible = createUIText(SplatcraftConfig.UI.renderHeldItemWhenHotbarInvisible.getId());
            SplatcraftConfig.Option renderHeldItemWhenHotbarInvisibleOption = SplatcraftConfig.UI.renderHeldItemWhenHotbarInvisible;
            TranslatableText invisibleHotbarStatusBarsShift = createUIText(SplatcraftConfig.UI.invisibleHotbarStatusBarsShift.getId());
            SplatcraftConfig.Option invisibleHotbarStatusBarsShiftOption = SplatcraftConfig.UI.invisibleHotbarStatusBarsShift;
            TranslatableText invisibleCrosshairWhenSquid = createUIText(SplatcraftConfig.UI.invisibleCrosshairWhenSquid.getId());
            SplatcraftConfig.Option invisibleCrosshairWhenSquidOption = SplatcraftConfig.UI.invisibleCrosshairWhenSquid;
            UI.addEntry(
                entryBuilder.startEnumSelector(preventBobViewWhenSquid, preventBobViewWhenSquidOption.getClazz(), preventBobViewWhenSquidOption.getEnum())
                    .setDefaultValue(preventBobViewWhenSquidOption.getDefaultEnum())
                    .setSaveConsumer(value -> preventBobViewWhenSquidOption.value = value)
                    .setTooltip(createTooltip(preventBobViewWhenSquid))
                    .build()
            ).addEntry(
                entryBuilder.startBooleanToggle(modifyFovForSquidForm, modifyFovForSquidFormOption.getBoolean())
                    .setDefaultValue(modifyFovForSquidFormOption.getDefaultBoolean())
                    .setSaveConsumer(value -> modifyFovForSquidFormOption.value = value)
                    .setTooltip(createTooltip(modifyFovForSquidForm))
                    .build()
            ).addEntry(
                entryBuilder.startIntSlider(fovForSquidForm, fovForSquidFormOption.getInt(), fovForSquidFormOption.getMinInt(), fovForSquidFormOption.getMaxInt())
                    .setDefaultValue(fovForSquidFormOption.getDefaultInt())
                    .setSaveConsumer(value -> fovForSquidFormOption.value = value)
                    .setTooltip(createTooltip(fovForSquidForm))
                    .build()
            ).addEntry(
                entryBuilder.startBooleanToggle(invisibleHotbarWhenSquid, invisibleHotbarWhenSquidOption.getBoolean())
                    .setDefaultValue(invisibleHotbarWhenSquidOption.getDefaultBoolean())
                    .setSaveConsumer(value -> invisibleHotbarWhenSquidOption.value = value)
                    .setTooltip(createTooltip(invisibleHotbarWhenSquid))
                    .build()
            ).addEntry(
                entryBuilder.startBooleanToggle(renderHeldItemWhenHotbarInvisible, renderHeldItemWhenHotbarInvisibleOption.getBoolean())
                    .setDefaultValue(renderHeldItemWhenHotbarInvisibleOption.getDefaultBoolean())
                    .setSaveConsumer(value -> renderHeldItemWhenHotbarInvisibleOption.value = value)
                    .setTooltip(createTooltip(renderHeldItemWhenHotbarInvisible))
                    .build()
            ).addEntry(
                entryBuilder.startIntField(invisibleHotbarStatusBarsShift, invisibleHotbarStatusBarsShiftOption.getInt())
                    .setDefaultValue(invisibleHotbarStatusBarsShiftOption.getDefaultInt())
                    .setSaveConsumer(value -> invisibleHotbarStatusBarsShiftOption.value = value)
                    .setTooltip(createTooltip(invisibleHotbarStatusBarsShift))
                    .build()
            ).addEntry(
                entryBuilder.startBooleanToggle(invisibleCrosshairWhenSquid, invisibleCrosshairWhenSquidOption.getBoolean())
                    .setDefaultValue(invisibleCrosshairWhenSquidOption.getDefaultBoolean())
                    .setSaveConsumer(value -> invisibleCrosshairWhenSquidOption.value = value)
                    .setTooltip(createTooltip(invisibleCrosshairWhenSquid))
                    .build()
            );

            //
            // COLORS CATEGORY
            //

            ConfigCategory INK = builder.getOrCreateCategory(createInkText());
            TranslatableText colorLock = createInkText(SplatcraftConfig.INK.colorLock.getId());
            SplatcraftConfig.Option colorLockOption = SplatcraftConfig.INK.colorLock;
            SplatcraftConfig.EnumOption<InkAmountIndicator> inkAmountIndicatorOption = SplatcraftConfig.INK.inkAmountIndicator;
            TranslatableText inkColoredCrosshairWhenSquid = createInkText(SplatcraftConfig.INK.inkColoredCrosshairWhenSquid.getId());
            SplatcraftConfig.Option inkColoredCrosshairWhenSquidOption = SplatcraftConfig.INK.inkColoredCrosshairWhenSquid;
            TranslatableText inkAmountIndicator = createInkText(SplatcraftConfig.INK.inkAmountIndicator.getId());
            TranslatableText inkAmountIndicatorAlwaysVisible = createInkText(SplatcraftConfig.INK.inkAmountIndicatorAlwaysVisible.getId());
            SplatcraftConfig.Option inkAmountIndicatorAlwaysVisibleOption = SplatcraftConfig.INK.inkAmountIndicatorAlwaysVisible;
            TranslatableText inkAmountIndicatorExclamations = createInkText(SplatcraftConfig.INK.inkAmountIndicatorExclamations.getId());
            SplatcraftConfig.Option inkAmountIndicatorExclamationsOption = SplatcraftConfig.INK.inkAmountIndicatorExclamations;
            TranslatableText inkAmountIndicatorExclamationsMin = createInkText(SplatcraftConfig.INK.inkAmountIndicatorExclamationsMin.getId());
            SplatcraftConfig.RangedOption inkAmountIndicatorExclamationsMinOption = SplatcraftConfig.INK.inkAmountIndicatorExclamationsMin;
            TranslatableText inkAmountIndicatorExclamationsMax = createInkText(SplatcraftConfig.INK.inkAmountIndicatorExclamationsMax.getId());
            SplatcraftConfig.RangedOption inkAmountIndicatorExclamationsMaxOption = SplatcraftConfig.INK.inkAmountIndicatorExclamationsMax;
            INK.addEntry(
                entryBuilder.startBooleanToggle(colorLock, colorLockOption.getBoolean())
                    .setDefaultValue(colorLockOption.getDefaultBoolean())
                    .setSaveConsumer(value -> colorLockOption.value = value)
                    .setTooltip(createTooltip(colorLock))
                    .build()
            ).addEntry(
                entryBuilder.startBooleanToggle(inkColoredCrosshairWhenSquid, inkColoredCrosshairWhenSquidOption.getBoolean())
                    .setDefaultValue(inkColoredCrosshairWhenSquidOption.getDefaultBoolean())
                    .setSaveConsumer(value -> inkColoredCrosshairWhenSquidOption.value = value)
                    .setTooltip(createTooltip(inkColoredCrosshairWhenSquid))
                    .build()
            ).addEntry(
                entryBuilder.startEnumSelector(inkAmountIndicator, inkAmountIndicatorOption.getClazz(), inkAmountIndicatorOption.getEnum())
                    .setDefaultValue(inkAmountIndicatorOption.getDefaultEnum())
                    .setSaveConsumer(value -> inkAmountIndicatorOption.value = value)
                    .setTooltip(createTooltip(inkAmountIndicator))
                    .build()
            ).addEntry(
                entryBuilder.startBooleanToggle(inkAmountIndicatorAlwaysVisible, inkAmountIndicatorAlwaysVisibleOption.getBoolean())
                    .setDefaultValue(inkAmountIndicatorAlwaysVisibleOption.getDefaultBoolean())
                    .setSaveConsumer(value -> inkAmountIndicatorAlwaysVisibleOption.value = value)
                    .setTooltip(createTooltip(inkAmountIndicatorAlwaysVisible))
                    .build()
            ).addEntry(
                entryBuilder.startBooleanToggle(inkAmountIndicatorExclamations, inkAmountIndicatorExclamationsOption.getBoolean())
                    .setDefaultValue(inkAmountIndicatorExclamationsOption.getDefaultBoolean())
                    .setSaveConsumer(value -> inkAmountIndicatorExclamationsOption.value = value)
                    .setTooltip(createTooltip(inkAmountIndicatorExclamations))
                    .build()
            ).addEntry(
                entryBuilder.startIntSlider(inkAmountIndicatorExclamationsMin, inkAmountIndicatorExclamationsMinOption.getInt(), inkAmountIndicatorExclamationsMinOption.getMinInt(), inkAmountIndicatorExclamationsMinOption.getMaxInt())
                    .setDefaultValue(inkAmountIndicatorExclamationsMinOption.getDefaultInt())
                    .setSaveConsumer(value -> inkAmountIndicatorExclamationsMinOption.value = value)
                    .setTooltip(createTooltip(inkAmountIndicatorExclamationsMin))
                    .build()
            ).addEntry(
                entryBuilder.startIntSlider(inkAmountIndicatorExclamationsMax, inkAmountIndicatorExclamationsMaxOption.getInt(), inkAmountIndicatorExclamationsMaxOption.getMinInt(), inkAmountIndicatorExclamationsMaxOption.getMaxInt())
                    .setDefaultValue(inkAmountIndicatorExclamationsMaxOption.getDefaultInt())
                    .setSaveConsumer(value -> inkAmountIndicatorExclamationsMaxOption.value = value)
                    .setTooltip(createTooltip(inkAmountIndicatorExclamationsMax))
                    .build()
            );

            return builder.build();
        };
    }

    private TranslatableText createTooltip(TranslatableText text) {
        return new TranslatableText(text.getKey() + ".tooltip");
    }
    private TranslatableText createRenderText(String label) {
        return createCatText("render" + (label.isEmpty() ? "" : "." + label));
    }
    private TranslatableText createRenderText() {
        return createRenderText("");
    }
    private TranslatableText createUIText(String label) {
        return createCatText("ui" + (label.isEmpty() ? "" : "." + label));
    }
    private TranslatableText createUIText() {
        return createUIText("");
    }
    private TranslatableText createInkText(String label) {
        return createCatText("ink" + (label.isEmpty() ? "" : "." + label));
    }
    private TranslatableText createInkText() {
        return createInkText("");
    }
    private TranslatableText createCatText(String group) {
        return createConfigText("category." + group);
    }
    private TranslatableText createConfigText(String label) {
        return new TranslatableText("config." + Splatcraft.MOD_ID + "." + label);
    }
}
