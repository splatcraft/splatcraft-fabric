package net.splatcraft.inkcolor;

import me.shedaniel.math.Color;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.splatcraft.registry.SplatcraftRegistries;

import java.util.function.Function;

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

    public static InkColor fromString(String id) {
        return FROM_STRING.apply(id);
    }

    public boolean equals(InkColor inkColor) {
        return inkColor.getId().equals(this.getId());
    }

    @Override
    public String toString() {
        return this.getId().toString();
    }
}
