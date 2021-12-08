package net.splatcraft.inkcolor;

import me.shedaniel.math.Color;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.splatcraft.registry.SplatcraftRegistries;

import java.util.function.Function;

import static net.splatcraft.util.SplatcraftConstants.T_INK_COLOR_TEXT_DISPLAY;
import static net.splatcraft.util.SplatcraftConstants.T_INK_COLOR_TEXT_DISPLAY_ICON;

public class InkColor {
    public static final Function<InkColor, Identifier> TO_IDENTIFIER = Util.memoize(SplatcraftRegistries.INK_COLOR::getId);
    public static final Function<String, InkColor> FROM_STRING = Util.memoize(s -> SplatcraftRegistries.INK_COLOR.get(Identifier.tryParse(s)));

    private final Color color;

    public InkColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return this.color;
    }

    public int getDecimalColor() {
        return this.color.getColor();
    }

    /**
     * @return the {@link Identifier} of this {@link InkColor}.
     *         If not registered, it will return the
     *         default.
     */
    public Identifier getId() {
        return TO_IDENTIFIER.apply(this);
    }

    public Text getDisplayText() {
        Text text = new TranslatableText(T_INK_COLOR_TEXT_DISPLAY_ICON).setStyle(Style.EMPTY.withColor(this.getDecimalColor()));
        return new TranslatableText(T_INK_COLOR_TEXT_DISPLAY, text, this);
    }

    public static String toString(InkColor inkColor) {
        return (inkColor == null ? InkColors._DEFAULT : inkColor).toString();
    }

    public static InkColor fromString(String id) {
        return FROM_STRING.apply(id);
    }

    public boolean equals(InkColor inkColor) {
        return inkColor != null && inkColor.getId().equals(this.getId());
    }

    @Override
    public String toString() {
        return this.getId().toString();
    }
}
