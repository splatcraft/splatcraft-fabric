package net.splatcraft.particle;

import com.mojang.serialization.Codec;
import net.minecraft.particle.ParticleType;
import net.splatcraft.util.Color;

@SuppressWarnings("deprecation")
public class InkSquidSoulParticleEffect extends ColorableScalableParticleEffect {
    public static final Codec<InkSquidSoulParticleEffect> CODEC = createCodec(InkSquidSoulParticleEffect::new);
    public static final Factory<InkSquidSoulParticleEffect> PARAMETERS = createParameters(InkSquidSoulParticleEffect::new);

    public InkSquidSoulParticleEffect(Color color, float scale) {
        super(color, scale);
    }

    @Override
    public ParticleType<?> getType() {
        return SplatcraftParticles.INK_SQUID_SOUL;
    }
}
