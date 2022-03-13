package net.splatcraft.impl.data;

import net.moddingplayground.frame.api.toymaker.v0.generator.tag.AbstractTagGenerator;
import net.splatcraft.api.Splatcraft;
import net.splatcraft.api.inkcolor.InkColor;
import net.splatcraft.api.inkcolor.InkColors;
import net.splatcraft.api.registry.SplatcraftRegistries;
import net.splatcraft.api.tag.InkColorTags;

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
