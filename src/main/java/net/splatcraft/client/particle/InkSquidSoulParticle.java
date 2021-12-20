package net.splatcraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.splatcraft.particle.InkSquidSoulParticleEffect;

@Environment(EnvType.CLIENT)
public class InkSquidSoulParticle extends SpriteBillboardParticle {
    private final SpriteProvider spriteProvider;

    private InkSquidSoulParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, InkSquidSoulParticleEffect effect, SpriteProvider spriteProvider) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);

        Vec3f color = effect.getColor().getVector();
        this.colorRed = Math.max(0.018f, color.getX() - 0.018f);
        this.colorGreen = Math.max(0.018f, color.getY() - 0.018f);
        this.colorBlue = Math.max(0.018f, color.getZ() - 0.018f);

        this.gravityStrength = 0.15f;
        this.maxAge = 20;
        this.scale = 0.3f;
        this.collidesWithWorld = false;

        this.spriteProvider = spriteProvider;
    }

    @Override
    public void tick() {
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;

        if (this.age++ >= this.maxAge) {
            this.markDead();
        } else {
            this.velocityY += 0.04D * (double) this.gravityStrength;
            this.move(0, this.velocityY, 0);
            this.velocityY *= 0.98F;
        }
    }

    @Override
    public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
        Vec3d cameraPos = camera.getPos();
        float x = (float) (MathHelper.lerp(tickDelta, this.prevPosX, this.x) - cameraPos.getX());
        float y = (float) (MathHelper.lerp(tickDelta, this.prevPosY, this.y) - cameraPos.getY());
        float z = (float) (MathHelper.lerp(tickDelta, this.prevPosZ, this.z) - cameraPos.getZ());

        Quaternion rotation;
        if (this.angle == 0.0F) {
            rotation = camera.getRotation();
        } else {
            rotation = new Quaternion(camera.getRotation());
            float angle = MathHelper.lerp(tickDelta, this.prevAngle, this.angle);
            rotation.hamiltonProduct(Vec3f.POSITIVE_Z.getDegreesQuaternion(angle));
        }

        Vec3f[] vector3fs = new Vec3f[]{
            new Vec3f(-1.0F, -1.0F, 0.0F),
            new Vec3f(-1.0F, 1.0F, 0.0F),
            new Vec3f(1.0F, 1.0F, 0.0F),
            new Vec3f(1.0F, -1.0F, 0.0F)
        };
        float size = this.getSize(tickDelta);

        for (int i = 0; i < 4; ++i) {
            Vec3f vec3f = vector3fs[i];
            vec3f.rotate(rotation);
            vec3f.scale(size);
            vec3f.add(x, y, z);
        }


        for (int i = 0; i < 3; i++) {
            float r = i == 1 ? this.colorRed : 1;
            float g = i == 1 ? this.colorGreen : 1;
            float b = i == 1 ? this.colorBlue : 1;
            float a = this.colorAlpha;
            if (age > maxAge - 5) a = (1f - Math.max(0, age - maxAge + 5) - tickDelta) * 0.2f;

            setSprite(spriteProvider.getSprite(i + 1, 3));

            float minU = this.getMinU();
            float maxU = this.getMaxU();
            float minV = this.getMinV();
            float maxV = this.getMaxV();
            int light = 15728880;

            vertexConsumer.vertex(vector3fs[0].getX(), vector3fs[0].getY(), vector3fs[0].getZ()).texture(maxU, maxV).color(r, g, b, a).light(light).next();
            vertexConsumer.vertex(vector3fs[1].getX(), vector3fs[1].getY(), vector3fs[1].getZ()).texture(maxU, minV).color(r, g, b, a).light(light).next();
            vertexConsumer.vertex(vector3fs[2].getX(), vector3fs[2].getY(), vector3fs[2].getZ()).texture(minU, minV).color(r, g, b, a).light(light).next();
            vertexConsumer.vertex(vector3fs[3].getX(), vector3fs[3].getY(), vector3fs[3].getZ()).texture(minU, maxV).color(r, g, b, a).light(light).next();
        }
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Environment(EnvType.CLIENT)
    public record Factory(SpriteProvider spriteProvider) implements ParticleFactory<InkSquidSoulParticleEffect> {
        @Override
        public Particle createParticle(InkSquidSoulParticleEffect particleEffect, ClientWorld clientWorld, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return new InkSquidSoulParticle(clientWorld, x, y, z, velocityX, velocityY, velocityZ, particleEffect, this.spriteProvider);
        }
    }
}
