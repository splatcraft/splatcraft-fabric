package net.splatcraft.inkcolor;

import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.Vec3f;
import net.splatcraft.registry.SplatcraftRegistries;
import net.splatcraft.util.Color;
import net.splatcraft.util.Identifiable;

import java.util.function.Function;

import static net.splatcraft.util.SplatcraftConstants.*;

public class InkColor implements Identifiable {
    public static final Function<InkColor, String> TO_TRANSLATION_KEY = Util.memoize(inkColor -> {
        Identifier id = inkColor.getId();
        return "%s.%s.%s".formatted(SplatcraftRegistries.INK_COLOR.getKey().getValue(), id.getNamespace(), id.getPath());
    });

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
        return new Vec3f(this.getRed() / 255.0f, this.getGreen() / 255.0f, this.getBlue() / 255.0f);
    }

    public int getDecimalColor() {
        return this.color.getColor();
    }

    public int getRed() {
        return this.color.getRed();
    }

    public int getGreen() {
        return this.color.getGreen();
    }

    public int getBlue() {
        return this.color.getBlue();
    }

    public String getTranslationKey() {
        return TO_TRANSLATION_KEY.apply(this);
    }

    public Text getDisplayText(Style style) {
        return new TranslatableText(T_INK_COLOR_TEXT_DISPLAY, new TranslatableText(this.getTranslationKey())).setStyle(style.withColor(this.getDecimalColor()));
    }

    public Text getDisplayText() {
        return getDisplayText(Style.EMPTY);
    }

    /**
     * @return the {@link Identifier} of this {@link InkColor}.
     *         If not registered, it will return the
     *         default.
     */
    @Override
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
        return (inkColor == null ? InkColors.getDefault() : inkColor).toString();
    }

    @Override
    public String toString() {
        return this.getId().toString();
    }
}
