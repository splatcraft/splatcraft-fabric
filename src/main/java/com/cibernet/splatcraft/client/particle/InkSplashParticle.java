package com.cibernet.splatcraft.client.particle;

import com.cibernet.splatcraft.particle.InkSplashParticleEffect;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.RainSplashParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;

@Environment(EnvType.CLIENT)
public class InkSplashParticle extends RainSplashParticle {
    private final SpriteProvider spriteProvider;

    private InkSplashParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, InkSplashParticleEffect particleEffect, SpriteProvider spriteProvider) {
        super(world, x, y, z);
        this.gravityStrength = 0.04F;

        if (velocityY == 0.0D && (velocityX != 0.0D || velocityZ != 0.0D)) {
            this.velocityX = velocityX;
            this.velocityY = 0.4D;
            this.velocityZ = velocityZ;
        }

        float rand = (float)Math.random() * 0.4F + 0.6F;
        this.colorRed = ((float)(Math.random() * 0.20000000298023224D) + 0.8F) * particleEffect.getRed() * rand;
        this.colorGreen = ((float)(Math.random() * 0.20000000298023224D) + 0.8F) * particleEffect.getGreen() * rand;
        this.colorBlue = ((float)(Math.random() * 0.20000000298023224D) + 0.8F) * particleEffect.getBlue() * rand;
        this.scale = 0.33F * (this.random.nextFloat() * 0.5F + 0.5F) * 2.0F;

        this.maxAge = 20 + this.random.nextInt(4);
        this.spriteProvider = spriteProvider;
        this.setSpriteForAge(spriteProvider);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.age++ >= this.maxAge) {
            this.markDead();
        } else {
            this.setSpriteForAge(this.spriteProvider);
        }
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<InkSplashParticleEffect> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(InkSplashParticleEffect particleEffect, ClientWorld clientWorld, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return new InkSplashParticle(clientWorld, x, y, z, velocityX, velocityY, velocityZ, particleEffect, this.spriteProvider);
        }
    }
}
