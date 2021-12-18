package net.splatcraft.particle;

import com.mojang.serialization.Codec;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.splatcraft.Splatcraft;

import java.util.function.Function;

public class SplatcraftParticles {
    public static final ParticleType<InkSplashParticleEffect> INK_SPLASH = register("ink_splash", InkSplashParticleEffect.PARAMETERS, p -> InkSplashParticleEffect.CODEC);
    public static final ParticleType<InkSquidSoulParticleEffect> INK_SQUID_SOUL = register("ink_squid_soul", InkSquidSoulParticleEffect.PARAMETERS, p -> InkSquidSoulParticleEffect.CODEC);

    @SuppressWarnings("deprecation")
    private static <T extends ParticleEffect> ParticleType<T> register(String id, ParticleEffect.Factory<T> factory, final Function<ParticleType<T>, Codec<T>> function) {
        return Registry.register(Registry.PARTICLE_TYPE, new Identifier(Splatcraft.MOD_ID, id), new ParticleType<T>(false, factory) {
            @Override
            public Codec<T> getCodec() {
                return function.apply(this);
            }
        });
    }
    private static DefaultParticleType register(String id, boolean alwaysShow) {
        return Registry.register(Registry.PARTICLE_TYPE, new Identifier(Splatcraft.MOD_ID, id), new PublicDefaultParticleType(alwaysShow));
    }
}
