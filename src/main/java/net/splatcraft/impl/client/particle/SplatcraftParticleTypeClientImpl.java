package net.splatcraft.impl.client.particle;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.splatcraft.api.client.particle.InkSplashParticle;
import net.splatcraft.api.client.particle.InkSquidSoulParticle;
import net.splatcraft.api.particle.SplatcraftParticleType;

@Environment(EnvType.CLIENT)
public final class SplatcraftParticleTypeClientImpl implements SplatcraftParticleType, ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ParticleFactoryRegistry pfrInstance = ParticleFactoryRegistry.getInstance();
        pfrInstance.register(INK_SPLASH, InkSplashParticle.Factory::new);
        pfrInstance.register(INK_SQUID_SOUL, InkSquidSoulParticle.Factory::new);
    }
}
