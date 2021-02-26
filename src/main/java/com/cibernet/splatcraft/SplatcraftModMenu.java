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
            SplatcraftConfig.Option holdStageBarrierToRenderOption = SplatcraftConfig.RENDER.holdStageBarrierToRender;
            SplatcraftConfig.RangedOption barrierRenderDistanceOption = SplatcraftConfig.RENDER.barrierRenderDistance;
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
