package net.splatcraft.inkcolor;

import me.shedaniel.math.Color;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.Vec3f;
import net.splatcraft.registry.SplatcraftRegistries;
import net.splatcraft.util.Identifiable;

import java.util.function.Function;

import static net.splatcraft.util.SplatcraftConstants.*;

public class InkColor implements Identifiable {
    public static final Function<InkColor, Identifier> TO_IDENTIFIER = Util.memoize(SplatcraftRegistries.INK_COLOR::getId);
    public static final Function<String, InkColor> FROM_STRING = Util.memoize(s -> SplatcraftRegistries.INK_COLOR.get(Identifier.tryParse(s)));

    private final Color color;

    public InkColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return this.color;
    }

    public Vec3f getVectorColor() {
        return new Vec3f(this.getRed(), this.getGreen(), this.getBlue());
    }

    public int getDecimalColor() {
        return this.color.getColor();
    }

    public float getRed() {
        return this.color.getRed() / 255.0f;
    }

    public float getGreen() {
        return this.color.getGreen() / 255.0f;
    }

    public float getBlue() {
        return this.color.getBlue() / 255.0f;
    }

    public Text getDisplayText() {
        Text text = new TranslatableText(T_INK_COLOR_TEXT_DISPLAY_ICON).setStyle(Style.EMPTY.withColor(this.getDecimalColor()));
        return new TranslatableText(T_INK_COLOR_TEXT_DISPLAY, text, this);
    }

    /**
     * @return the {@link Identifier} of this {@link InkColor}.
     *         If not registered, it will return the
     *         default.
     */
    public Identifier getId() {
        return TO_IDENTIFIER.apply(this);
    }

    public static InkColor fromId(Identifier id) {
        return fromString(id.toString());
    }

    public static InkColor fromString(String id) {
        return FROM_STRING.apply(id);
    }

    public boolean equals(InkColor inkColor) {
        return inkColor != null && inkColor.getId().equals(this.getId());
    }

    public static String toString(InkColor inkColor) {
        return (inkColor == null ? InkColors._DEFAULT : inkColor).toString();
    }

    @Override
    public String toString() {
        return this.getId().toString();
    }
}
