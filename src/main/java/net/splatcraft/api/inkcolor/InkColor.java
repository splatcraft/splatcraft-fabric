package net.splatcraft.api.inkcolor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.DyeColor;
import net.minecraft.util.dynamic.RegistryElementCodec;
import net.minecraft.util.registry.RegistryEntry;
import net.splatcraft.api.registry.SplatcraftRegistry;
import net.splatcraft.api.util.Color;

public final class InkColor {
    public static final Codec<InkColor> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(Color.CODEC.fieldOf("color").forGetter(InkColor::getColor))
                            .apply(instance, InkColor::new)
    );

    public static final Codec<RegistryEntry<InkColor>> REGISTRY_CODEC = RegistryElementCodec.of(SplatcraftRegistry.INK_COLOR_KEY, CODEC);

    private final Color color;
    private final RegistryEntry.Reference<InkColor> registryEntry;

    public InkColor(Color color) {
        this.color = color;
        this.registryEntry = SplatcraftRegistry.INK_COLOR.createEntry(this);
    }

    public static InkColor of(Color color) {
        return new InkColor(color);
    }

    public static InkColor of(int color) {
        return of(Color.of(color));
    }

    public static InkColor fromDye(DyeColor dye) {
        float[] rgb = dye.getColorComponents();
        return new InkColor(Color.ofRGB(rgb[0], rgb[1], rgb[2]));
    }

    public Color getColor() {
        return this.color;
    }

    public RegistryEntry.Reference<InkColor> getRegistryEntry() {
        return this.registryEntry;
    }

    @Override
    public String toString() {
        return "" + SplatcraftRegistry.INK_COLOR.getId(this);
    }
}
