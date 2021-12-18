package net.splatcraft.particle;

import com.mojang.serialization.Codec;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.Identifier;
import net.splatcraft.inkcolor.InkColor;

@SuppressWarnings("deprecation")
public class InkSquidSoulParticleEffect extends InkedParticleEffect {
    public static final Codec<InkSquidSoulParticleEffect> CODEC = createCodec(InkSquidSoulParticleEffect::new);
    public static final Factory<InkSquidSoulParticleEffect> PARAMETERS = createParameters(InkSquidSoulParticleEffect::new);

    public InkSquidSoulParticleEffect(InkColor inkColor, float scale) {
        super(inkColor, scale);
    }

    public InkSquidSoulParticleEffect(String inkColor, float scale) {
        super(inkColor, scale);
    }

    public InkSquidSoulParticleEffect(Identifier inkColor, float scale) {
        super(inkColor, scale);
    }

    @Override
    public ParticleType<?> getType() {
        return SplatcraftParticles.INK_SQUID_SOUL;
    }
}
