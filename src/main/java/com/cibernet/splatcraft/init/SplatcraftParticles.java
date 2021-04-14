package com.cibernet.splatcraft.init;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.particle.InkSplashParticleEffect;
import com.cibernet.splatcraft.particle.InkSquidSoulParticleEffect;
import com.cibernet.splatcraft.particle.vanilla.PublicDefaultParticleType;
import com.mojang.serialization.Codec;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.function.Function;

public class SplatcraftParticles {
    public static final ParticleType<InkSplashParticleEffect> INK_SPLASH = register("ink_splash", InkSplashParticleEffect.PARAMETERS_FACTORY, particleType -> InkSplashParticleEffect.CODEC);
    public static final ParticleType<InkSquidSoulParticleEffect> INK_SQUID_SOUL = register("ink_squid_soul", InkSquidSoulParticleEffect.PARAMETERS_FACTORY, particleType -> InkSquidSoulParticleEffect.CODEC);

    public static final DefaultParticleType WAX_INKED_BLOCK_ON = register("wax_inked_block_on", false);
    public static final DefaultParticleType WAX_INKED_BLOCK_OFF = register("wax_inked_block_off", false);

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
