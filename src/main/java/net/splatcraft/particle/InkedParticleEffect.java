package net.splatcraft.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.splatcraft.command.argument.InkColorStringReader;
import net.splatcraft.inkcolor.InkColor;

import java.util.function.BiFunction;

@SuppressWarnings("deprecation")
public abstract class InkedParticleEffect implements ParticleEffect {
    private final InkColor inkColor;
    private final float scale;

    protected InkedParticleEffect(InkColor inkColor, float scale) {
        this.inkColor = inkColor;
        this.scale = scale;
    }

    protected InkedParticleEffect(String inkColor, float scale) {
        this(InkColor.fromString(inkColor), scale);
    }

    protected InkedParticleEffect(Identifier inkColor, float scale) {
        this(InkColor.fromId(inkColor), scale);
    }

    public InkColor getInkColor() {
        return this.inkColor;
    }

    public float getScale() {
        return this.scale;
    }

    public static <T extends InkedParticleEffect> Codec<T> createCodec(BiFunction<String, Float, T> factory) {
        return RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("ink_color").forGetter(e -> e.getInkColor().toString()),
            Codec.FLOAT.fieldOf("scale").forGetter(InkedParticleEffect::getScale)
        ).apply(instance, factory));
    }

    public static <T extends InkedParticleEffect> ParticleEffect.Factory<T> createParameters(BiFunction<Identifier, Float, T> factory) {
        return new ParticleEffect.Factory<>() {
            @Override
            public T read(ParticleType<T> particleType, StringReader reader) throws CommandSyntaxException {
                reader.expect(' ');
                InkColorStringReader inkColorReader = new InkColorStringReader(reader, false).consume();
                InkColor inkColor = inkColorReader.getInkColor();

                reader.expect(' ');
                float scale = (float) reader.readDouble();

                return factory.apply(inkColor.getId(), scale);
            }

            @Override
            public T read(ParticleType<T> particleType, PacketByteBuf buf) {
                return factory.apply(buf.readIdentifier(), buf.readFloat());
            }
        };
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeIdentifier(this.inkColor.getId());
        buf.writeFloat(this.scale);
    }

    @Override
    public String asString() {
        return "%s %s %.2f".formatted(Registry.PARTICLE_TYPE.getId(this.getType()), this.inkColor, this.scale);
    }
}
