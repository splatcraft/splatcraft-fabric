package com.cibernet.splatcraft;

import com.cibernet.splatcraft.config.SplatcraftConfig;
import com.cibernet.splatcraft.config.SplatcraftConfigManager;
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
            SplatcraftConfig.EnumOption<SplatcraftConfig.UIGroup.PreventBobView> preventBobViewWhenSquidOption = SplatcraftConfig.UI.preventBobViewWhenSquid;
            TranslatableText invisibleHotbarWhenSquid = createUIText(SplatcraftConfig.UI.invisibleHotbarWhenSquid.getId());
            SplatcraftConfig.Option invisibleHotbarWhenSquidOption = SplatcraftConfig.UI.invisibleHotbarWhenSquid;
            TranslatableText invisibleHotbarStatusBarsShift = createUIText(SplatcraftConfig.UI.invisibleHotbarStatusBarsShift.getId());
            SplatcraftConfig.Option invisibleHotbarStatusBarsShiftOption = SplatcraftConfig.UI.invisibleHotbarStatusBarsShift;
            TranslatableText invisibleCrosshairWhenSquid = createUIText(SplatcraftConfig.UI.invisibleCrosshairWhenSquid.getId());
            SplatcraftConfig.Option invisibleCrosshairWhenSquidOption = SplatcraftConfig.UI.invisibleCrosshairWhenSquid;
            TranslatableText inkColoredCrosshairWhenSquid = createUIText(SplatcraftConfig.UI.inkColoredCrosshairWhenSquid.getId());
            SplatcraftConfig.Option inkColoredCrosshairWhenSquidOption = SplatcraftConfig.UI.inkColoredCrosshairWhenSquid;
            TranslatableText inkAmountIndicator = createUIText(SplatcraftConfig.UI.inkAmountIndicator.getId());
            SplatcraftConfig.EnumOption<SplatcraftConfig.UIGroup.InkAmountIndicator> inkAmountIndicatorOption = SplatcraftConfig.UI.inkAmountIndicator;
            UI.addEntry(
                entryBuilder.startEnumSelector(preventBobViewWhenSquid, preventBobViewWhenSquidOption.getClazz(), preventBobViewWhenSquidOption.getEnum())
                    .setDefaultValue(preventBobViewWhenSquidOption.getDefaultEnum())
                    .setSaveConsumer(value -> preventBobViewWhenSquidOption.value = value)
                    .setTooltip(createTooltip(preventBobViewWhenSquid))
                    .build()
            ).addEntry(
                entryBuilder.startBooleanToggle(invisibleHotbarWhenSquid, invisibleHotbarWhenSquidOption.getBoolean())
                    .setDefaultValue(invisibleHotbarWhenSquidOption.getDefaultBoolean())
                    .setSaveConsumer(value -> invisibleHotbarWhenSquidOption.value = value)
                    .setTooltip(createTooltip(invisibleHotbarWhenSquid))
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
            );

            //
            // COLORS CATEGORY
            //

            ConfigCategory COLORS = builder.getOrCreateCategory(createColorsText());
            TranslatableText colorLock = createColorsText(SplatcraftConfig.COLORS.colorLock.getId());
            SplatcraftConfig.Option colorLockOption = SplatcraftConfig.COLORS.colorLock;
            COLORS.addEntry(
                entryBuilder.startBooleanToggle(colorLock, colorLockOption.getBoolean())
                    .setDefaultValue(colorLockOption.getDefaultBoolean())
                    .setSaveConsumer(value -> colorLockOption.value = value)
                    .setTooltip(createTooltip(colorLock))
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
    private TranslatableText createColorsText(String label) {
        return createCatText("colors" + (label.isEmpty() ? "" : "." + label));
    }
    private TranslatableText createColorsText() {
        return createColorsText("");
    }
    private TranslatableText createCatText(String group) {
        return createConfigText("category." + group);
    }
    private TranslatableText createConfigText(String label) {
        return new TranslatableText("config." + Splatcraft.MOD_ID + "." + label);
    }
}
