package net.splatcraft.impl.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.splatcraft.api.inkcolor.InkColor;
import net.splatcraft.api.inkcolor.InkColors;
import net.splatcraft.api.registry.SplatcraftRegistries;
import net.splatcraft.api.tag.InkColorTags;

public class InkColorTagProvider extends FabricTagProvider<InkColor> {
    protected InkColorTagProvider(FabricDataGenerator gen) {
        super(gen, SplatcraftRegistries.INK_COLOR);
    }

    @Override
    protected void generateTags() {
        this.getOrCreateTagBuilder(InkColorTags.STARTER_COLORS).add(InkColors.ORANGE, InkColors.BLUE, InkColors.GREEN, InkColors.PINK);
    }
}
