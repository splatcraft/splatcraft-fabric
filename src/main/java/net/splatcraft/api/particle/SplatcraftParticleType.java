package net.splatcraft.api.particle;

import com.mojang.serialization.Codec;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.splatcraft.api.Splatcraft;
import net.splatcraft.api.inkcolor.InkColor;
import net.splatcraft.api.inkcolor.Inkable;

import java.util.function.Function;

import static net.splatcraft.api.client.util.ClientUtil.*;

public interface SplatcraftParticleType {
    ParticleType<InkSplashParticleEffect> INK_SPLASH = register("ink_splash", InkSplashParticleEffect.PARAMETERS, p -> InkSplashParticleEffect.CODEC);
    ParticleType<InkSquidSoulParticleEffect> INK_SQUID_SOUL = register("ink_squid_soul", InkSquidSoulParticleEffect.PARAMETERS, p -> InkSquidSoulParticleEffect.CODEC);

    @SuppressWarnings("deprecation")
    private static <T extends ParticleEffect> ParticleType<T> register(String id, ParticleEffect.Factory<T> factory, final Function<ParticleType<T>, Codec<T>> function) {
        return Registry.register(Registry.PARTICLE_TYPE, new Identifier(Splatcraft.MOD_ID, id), new ParticleType<T>(false, factory) {
            @Override
            public Codec<T> getCodec() {
                return function.apply(this);
            }
        });
    }

    static void inkSplash(World world, Inkable inkable, Vec3d pos, float scale) {
        InkColor inkColor = inkable.getInkColor();
        ParticleEffect effect = new InkSplashParticleEffect(getColor(inkColor), scale);
        world.addParticle(effect, pos.x, pos.y, pos.z, 0.0d, 0.0d, 0.0d);
    }

    static <T extends Entity & Inkable> void inkSquidSoul(T inkable, Vec3d pos, float scale) {
        InkColor inkColor = inkable.getInkColor();
        ParticleEffect effect = new InkSquidSoulParticleEffect(getColor(inkColor), scale);
        inkable.world.addParticle(effect, pos.x, pos.y, pos.z, 0.0d, 0.0d, 0.0d);
    }
}
