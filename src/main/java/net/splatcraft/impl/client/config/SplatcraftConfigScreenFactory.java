package net.splatcraft.impl.client.config;

import com.google.common.collect.ImmutableMap;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.TranslatableText;
import net.moddingplayground.frame.api.config.v0.Config;
import net.splatcraft.api.Splatcraft;

import static net.splatcraft.api.client.util.ClientUtil.*;

@Environment(EnvType.CLIENT)
public class SplatcraftConfigScreenFactory {
    private final Screen parent;

    public SplatcraftConfigScreenFactory(Screen parent) {
        this.parent = parent;
    }

    public Screen create() {
        ConfigBuilder configBuilder = ConfigBuilder
            .create()
            .setParentScreen(this.parent)
            .setDefaultBackgroundTexture(texture("block/inked_block"))
            .setTitle(txt("title"))
            .setSavingRunnable(() -> {
                ClientConfig.INSTANCE.save();
                ClientCompatConfig.INSTANCE.save();
            });
        configBuilder.setGlobalized(true);
        configBuilder.setGlobalizedExpanded(false);

        ConfigEntryBuilder entryBuilder = configBuilder.entryBuilder();
        new ImmutableMap.Builder<String, Config>()
            .put("client", ClientConfig.INSTANCE)
            .put("client_compat", ClientCompatConfig.INSTANCE)
        .build().forEach((title, config) -> config.addConfigListEntries(entryBuilder, () -> configBuilder.getOrCreateCategory(catTxt(title))));

        return configBuilder.build();
    }

    public TranslatableText txt(String label) {
        return new TranslatableText("config.%s.%s".formatted(Splatcraft.MOD_ID, label));
    }

    public TranslatableText catTxt(String category) {
        return txt("category.%s".formatted(category));
    }
}
