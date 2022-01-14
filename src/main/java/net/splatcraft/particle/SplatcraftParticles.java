package net.splatcraft.particle;

import com.mojang.serialization.Codec;
import net.minecraft.entity.Entity;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.splatcraft.Splatcraft;
import net.splatcraft.inkcolor.InkColor;
import net.splatcraft.inkcolor.Inkable;

import java.util.function.Function;

import static net.splatcraft.client.util.ClientUtil.*;

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

    public static void inkSplash(World world, Inkable inkable, Vec3d pos, float scale) {
        InkColor inkColor = inkable.getInkColor();
        ParticleEffect effect = new InkSplashParticleEffect(getColor(inkColor), scale);
        world.addParticle(effect, pos.x, pos.y, pos.z, 0.0d, 0.0d, 0.0d);
    }

    public static <T extends Entity & Inkable> void inkSquidSoul(T inkable, Vec3d pos, float scale) {
        InkColor inkColor = inkable.getInkColor();
        ParticleEffect effect = new InkSquidSoulParticleEffect(getColor(inkColor), scale);
        inkable.world.addParticle(effect, pos.x, pos.y, pos.z, 0.0d, 0.0d, 0.0d);
    }
}
