package com.cibernet.splatcraft.particle;

import com.cibernet.splatcraft.init.SplatcraftParticles;
import com.cibernet.splatcraft.inkcolor.InkColor;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;

public class InkSplashParticleEffect extends InkedParticleEffect {
    public static final Codec<InkSplashParticleEffect> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.FLOAT.fieldOf("r").forGetter(InkedParticleEffect::getRed),
        Codec.FLOAT.fieldOf("g").forGetter(InkedParticleEffect::getGreen),
        Codec.FLOAT.fieldOf("b").forGetter(InkedParticleEffect::getBlue),
        Codec.FLOAT.fieldOf("scale").forGetter(InkedParticleEffect::getScale)
    ).apply(instance, InkSplashParticleEffect::new));

    @SuppressWarnings("deprecation")
    public static final ParticleEffect.Factory<InkSplashParticleEffect> PARAMETERS_FACTORY = new ParticleEffect.Factory<>() {
        @Override
        public InkSplashParticleEffect read(ParticleType<InkSplashParticleEffect> particleType, StringReader stringReader) throws CommandSyntaxException {
            stringReader.expect(' ');
            float r = (float) stringReader.readDouble();
            stringReader.expect(' ');
            float g = (float) stringReader.readDouble();
            stringReader.expect(' ');
            float b = (float) stringReader.readDouble();
            stringReader.expect(' ');
            float scale = (float) stringReader.readDouble();
            return new InkSplashParticleEffect(r, g, b, scale);
        }

        @Override
        public InkSplashParticleEffect read(ParticleType<InkSplashParticleEffect> particleType, PacketByteBuf buf) {
            return new InkSplashParticleEffect(buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat());
        }
    };

    public InkSplashParticleEffect(float red, float green, float blue, float scale) {
        super(red, green, blue, scale);
    }
    public InkSplashParticleEffect(float[] color, float scale) {
        super(color, scale);
    }
    public InkSplashParticleEffect(float[] color) {
        super(color);
    }
    public InkSplashParticleEffect(InkColor inkColor) {
        super(inkColor);
    }

    @Override
    public ParticleType<?> getType() {
        return SplatcraftParticles.INK_SPLASH;
    }
}
