package net.splatcraft.api.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.registry.Registry;
import net.splatcraft.api.util.Color;

import java.util.function.BiFunction;

@SuppressWarnings("deprecation")
public abstract class ColorableScalableParticleEffect implements ParticleEffect {
    private final Color color;
    private final float scale;

    protected ColorableScalableParticleEffect(Color color, float scale) {
        this.color = color;
        this.scale = scale;
    }

    public Color getColor() {
        return this.color;
    }

    public float getScale() {
        return this.scale;
    }

    public static <T extends ColorableScalableParticleEffect> Codec<T> createCodec(BiFunction<Color, Float, T> factory) {
        return RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("color").forGetter(e -> e.getColor().getColor()),
            Codec.FLOAT.fieldOf("scale").forGetter(ColorableScalableParticleEffect::getScale)
        ).apply(instance, (color, scale) -> factory.apply(Color.of(color), scale)));
    }

    public static <T extends ColorableScalableParticleEffect> ParticleEffect.Factory<T> createParameters(BiFunction<Color, Float, T> factory) {
        return new ParticleEffect.Factory<>() {
            @Override
            public T read(ParticleType<T> particleType, StringReader reader) throws CommandSyntaxException {
                reader.expect(' ');
                int color = reader.readInt();
                reader.expect(' ');
                float scale = reader.readFloat();

                return factory.apply(Color.of(color), scale);
            }

            @Override
            public T read(ParticleType<T> particleType, PacketByteBuf buf) {
                return factory.apply(Color.of(buf.readInt()), buf.readFloat());
            }
        };
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeInt(this.color.getColor());
        buf.writeFloat(this.scale);
    }

    @Override
    public String asString() {
        return "%s %s %.2f".formatted(Registry.PARTICLE_TYPE.getId(this.getType()), this.color, this.scale);
    }
}
