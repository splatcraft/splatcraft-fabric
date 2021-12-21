package net.splatcraft.client.particle;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.RainSplashParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.splatcraft.particle.InkSplashParticleEffect;

@Environment(EnvType.CLIENT)
public class InkSplashParticle extends RainSplashParticle {
    public static final int MAX_SPRITE_AGE = 15;

    protected final SpriteProvider spriteProvider;
    protected final float baseScale;

    protected int spriteAge = 0;

    private InkSplashParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, InkSplashParticleEffect effect, SpriteProvider spriteProvider) {
        super(world, x, y, z);
        this.gravityStrength = 0.04f;

        if (velocityY == 0.0d && (velocityX != 0.0d || velocityZ != 0.0d)) {
            this.velocityX = velocityX;
            this.velocityY = 0.4d;
            this.velocityZ = velocityZ;
        }

        float rand = randCol(world);
        Vec3f color = effect.getColor().getVector();
        this.colorRed = randCol(world) * color.getX() * rand;
        this.colorGreen = randCol(world) * color.getY() * rand;
        this.colorBlue = randCol(world) * color.getZ() * rand;
        this.baseScale = effect.getScale() * (0.33f * (this.random.nextFloat() * 0.5f + 0.5f) * 2.0f);

        this.collidesWithWorld = false;
        this.maxAge = 20 + (this.random.nextInt(3) * 20) + (20 * 3);

        this.spriteProvider = spriteProvider;
        this.setSpriteForAge(spriteProvider);
    }

    public float randCol(ClientWorld world) {
        return world.random.nextFloat() * 0.075f + 0.925f;
    }

    @Override
    public void tick() {
        if (this.collidesWithWorld || this.age++ >= this.maxAge) {
            this.markDead();
        } else {
            this.spriteAge++;
            this.setSpriteForAge(this.spriteProvider);
        }

        super.tick();
    }

    @Override
    public float getSize(float tickDelta) {
        return this.isInvisible() ? 0.0f : this.baseScale;
    }

    public void setSpriteForAge(SpriteProvider spriteProvider) {
        if (!this.dead) this.setSprite(spriteProvider.getSprite(Math.min(this.spriteAge, MAX_SPRITE_AGE), MAX_SPRITE_AGE));
    }

    public boolean isInvisible() {
        MinecraftClient client = MinecraftClient.getInstance();
        Vec3d pos = new Vec3d(this.x, this.y, this.z);
        return this.age < 2 && client.gameRenderer.getCamera().getFocusedEntity().squaredDistanceTo(pos) < 12.25f;
    }

    @Environment(EnvType.CLIENT)
    public record Factory(SpriteProvider spriteProvider) implements ParticleFactory<InkSplashParticleEffect> {
        @Override
        public Particle createParticle(InkSplashParticleEffect particleEffect, ClientWorld clientWorld, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return new InkSplashParticle(clientWorld, x, y, z, velocityX, velocityY, velocityZ, particleEffect, this.spriteProvider);
        }
    }
}
