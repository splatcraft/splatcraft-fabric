package com.cibernet.splatcraft.particle;

import com.cibernet.splatcraft.init.SplatcraftParticles;
import com.cibernet.splatcraft.inkcolor.InkColor;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.registry.Registry;

import java.util.Locale;

public class InkSplashParticleEffect implements ParticleEffect {
    public static final Codec<InkSplashParticleEffect> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.FLOAT.fieldOf("r").forGetter(dustParticleEffect -> dustParticleEffect.red),
        Codec.FLOAT.fieldOf("g").forGetter(dustParticleEffect -> dustParticleEffect.green),
        Codec.FLOAT.fieldOf("b").forGetter(dustParticleEffect -> dustParticleEffect.blue)
    ).apply(instance, InkSplashParticleEffect::new));
    @SuppressWarnings("deprecation")
    public static final ParticleEffect.Factory<InkSplashParticleEffect> PARAMETERS_FACTORY = new ParticleEffect.Factory<InkSplashParticleEffect>() {
        @Override
        public InkSplashParticleEffect read(ParticleType<InkSplashParticleEffect> particleType, StringReader stringReader) throws CommandSyntaxException {
            stringReader.expect(' ');
            float r = (float)stringReader.readDouble();
            stringReader.expect(' ');
            float g = (float)stringReader.readDouble();
            stringReader.expect(' ');
            float b = (float)stringReader.readDouble();
            return new InkSplashParticleEffect(r, g, b);
        }

        @Override
        public InkSplashParticleEffect read(ParticleType<InkSplashParticleEffect> particleType, PacketByteBuf packetByteBuf) {
            return new InkSplashParticleEffect(packetByteBuf.readFloat(), packetByteBuf.readFloat(), packetByteBuf.readFloat());
        }
    };

    private final float red;
    private final float green;
    private final float blue;

    public InkSplashParticleEffect(float red, float green, float blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }
    public InkSplashParticleEffect(InkColor inkColor) {
        int colorInt = inkColor.getColor();

        this.red = ((colorInt & 16711680) >> 16) / 255.0f;
        this.green = ((colorInt & '\uff00') >> 8) / 255.0f;
        this.blue = (colorInt & 255) / 255.0f;
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeFloat(this.red);
        buf.writeFloat(this.green);
        buf.writeFloat(this.blue);
    }

    @Override
    public String asString() {
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f", Registry.PARTICLE_TYPE.getId(this.getType()), this.red, this.green, this.blue);
    }

    @Override
    public ParticleType<InkSplashParticleEffect> getType() {
        return SplatcraftParticles.INK_SPLASH;
    }

    @Environment(EnvType.CLIENT)
    public float getRed() {
        return this.red;
    }

    @Environment(EnvType.CLIENT)
    public float getGreen() {
        return this.green;
    }

    @Environment(EnvType.CLIENT)
    public float getBlue() {
        return this.blue;
    }
}
