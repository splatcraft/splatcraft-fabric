package net.splatcraft.particle;

import com.mojang.serialization.Codec;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.splatcraft.util.Color;

@SuppressWarnings("deprecation")
public class InkSplashParticleEffect extends ColorableScalableParticleEffect {
    public static final Codec<InkSplashParticleEffect> CODEC = createCodec(InkSplashParticleEffect::new);
    public static final ParticleEffect.Factory<InkSplashParticleEffect> PARAMETERS = createParameters(InkSplashParticleEffect::new);

    public InkSplashParticleEffect(Color color, float scale) {
        super(color, scale);
    }

    @Override
    public ParticleType<?> getType() {
        return SplatcraftParticles.INK_SPLASH;
    }
}
