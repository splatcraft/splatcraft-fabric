package net.splatcraft.api.inkcolor;

import com.google.common.base.Suppliers;
import net.minecraft.tag.TagKey;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.registry.RegistryEntry;
import net.splatcraft.api.registry.SplatcraftRegistries;
import net.splatcraft.api.util.Color;
import net.splatcraft.api.util.Identifiable;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static net.splatcraft.api.util.SplatcraftConstants.*;

public class InkColor implements Identifiable {
    public static final Function<String, InkColor> FROM_STRING = Util.memoize(s -> SplatcraftRegistries.INK_COLOR.get(Identifier.tryParse(s)));

    protected final Supplier<Identifier> id = Suppliers.memoize(this::getLatestId);
    protected final Supplier<String> translationKey = Suppliers.memoize(this::createTranslationKey);
    protected final Supplier<Text> defaultDisplayText = Suppliers.memoize(() -> this.getDisplayText(Style.EMPTY));
    protected final RegistryEntry.Reference<InkColor> registryEntry = SplatcraftRegistries.INK_COLOR.createEntry(this);

    private final Color color;

    public InkColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return this.color;
    }

    public Vec3f getVectorColor() {
        float r = this.getRed()   / 255.0f;
        float g = this.getGreen() / 255.0f;
        float b = this.getBlue()  / 255.0f;
        return new Vec3f(r, g, b);
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
        return this.translationKey.get();
    }

    protected String createTranslationKey() {
        Identifier id = this.getId();
        return "%s.%s.%s".formatted(SplatcraftRegistries.INK_COLOR.getKey().getValue(), id.getNamespace(), id.getPath());
    }

    public Text getDisplayText(Style style) {
        return new TranslatableText(T_INK_COLOR_TEXT_DISPLAY, new TranslatableText(this.getTranslationKey())).setStyle(style.withColor(this.getDecimalColor()));
    }

    public Text getDisplayText() {
        return this.defaultDisplayText.get();
    }

    public RegistryEntry.Reference<InkColor> getRegistryEntry() {
        return this.registryEntry;
    }

    public Stream<TagKey<InkColor>> streamTags() {
        return this.getRegistryEntry().streamTags();
    }

    /**
     * @return the {@link Identifier} of this {@link InkColor}.
     *         If not registered, it will return the
     *         default.
     */
    @Override
    public Identifier getId() {
        return this.id.get();
    }

    protected Identifier getLatestId() {
        return SplatcraftRegistries.INK_COLOR.getId(this);
    }

    public static InkColor fromId(Identifier id) {
        return fromString(id.toString());
    }

    public static InkColor fromString(String id) {
        return FROM_STRING.apply(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        InkColor inkColor = (InkColor) o;
        return Objects.equals(getId(), inkColor.getId());
    }

    @Override
    public String toString() {
        return this.getId().toString();
    }

    public static String toString(InkColor inkColor) {
        return (inkColor == null ? InkColors.getDefault() : inkColor).toString();
    }
}
