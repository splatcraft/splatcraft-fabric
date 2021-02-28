package com.cibernet.splatcraft.init;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.particle.InkSplashParticleEffect;
import com.mojang.serialization.Codec;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.function.Function;

public class SplatcraftParticles {
    public static final ParticleType<InkSplashParticleEffect> INK_SPLASH = register("ink_splash", InkSplashParticleEffect.PARAMETERS_FACTORY, particleType -> InkSplashParticleEffect.CODEC);

    public SplatcraftParticles() {}

    @SuppressWarnings("deprecation")
    private static <T extends ParticleEffect> ParticleType<T> register(String id, ParticleEffect.Factory<T> factory, final Function<ParticleType<T>, Codec<T>> function) {
        return Registry.register(Registry.PARTICLE_TYPE, new Identifier(Splatcraft.MOD_ID, id), new ParticleType<T>(false, factory) {
            @Override
            public Codec<T> getCodec() {
                return function.apply(this);
            }
        });
    }
}
