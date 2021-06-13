package com.cibernet.splatcraft.particle;

import com.cibernet.splatcraft.inkcolor.InkColor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.registry.Registry;

import java.util.Locale;

public abstract class InkedParticleEffect implements ParticleEffect {
    private final float red;
    private final float green;
    private final float blue;
    private final float scale;

    public InkedParticleEffect(float red, float green, float blue, float scale) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.scale = scale;
    }
    public InkedParticleEffect(float[] color, float scale) {
        this(color[0], color[1], color[2], scale);
    }
    public InkedParticleEffect(InkColor inkColor, float scale) {
        this(inkColor.getColorOrLockedComponents(), scale);
    }

    public InkedParticleEffect(float[] color) {
        this(color, 1.0f);
    }
    public InkedParticleEffect(InkColor inkColor) {
        this(inkColor, 1.0f);
    }

    @Override
    public void write(PacketByteBuf buf) {
        for (float f : new float[]{ this.red, this.green, this.blue, this.scale }) {
            buf.writeFloat(f);
        }
    }

    @Override
    public String asString() {
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f", Registry.PARTICLE_TYPE.getId(this.getType()), this.red, this.green, this.blue, this.scale);
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

    @Environment(EnvType.CLIENT)
    public float getScale() {
        return this.scale;
    }
}
