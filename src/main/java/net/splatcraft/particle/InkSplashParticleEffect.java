package net.splatcraft.particle;

import com.mojang.serialization.Codec;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.Identifier;
import net.splatcraft.inkcolor.InkColor;

@SuppressWarnings("deprecation")
public class InkSplashParticleEffect extends InkedParticleEffect {
    public static final Codec<InkSplashParticleEffect> CODEC = createCodec(InkSplashParticleEffect::new);
    public static final ParticleEffect.Factory<InkSplashParticleEffect> PARAMETERS = createParameters(InkSplashParticleEffect::new);

    public InkSplashParticleEffect(InkColor inkColor, float scale) {
        super(inkColor, scale);
    }

    public InkSplashParticleEffect(String inkColor, float scale) {
        super(inkColor, scale);
    }

    public InkSplashParticleEffect(Identifier inkColor, float scale) {
        super(inkColor, scale);
    }

    @Override
    public ParticleType<?> getType() {
        return SplatcraftParticles.INK_SPLASH;
    }
}
