package net.splatcraft.datagen;

import net.splatcraft.Splatcraft;
import net.splatcraft.datagen.impl.generator.tag.AbstractTagGenerator;
import net.splatcraft.inkcolor.InkColor;
import net.splatcraft.inkcolor.InkColors;
import net.splatcraft.registry.SplatcraftRegistries;
import net.splatcraft.tag.InkColorTags;

public class InkColorTagGenerator extends AbstractTagGenerator<InkColor> {
    public InkColorTagGenerator() {
        super(Splatcraft.MOD_ID, SplatcraftRegistries.INK_COLOR);
    }

    @Override
    public void generate() {
        this.add(InkColorTags.STARTER_COLORS,
            InkColors.ORANGE,
            InkColors.BLUE,
            InkColors.GREEN,
            InkColors.PINK
        );
    }
}
