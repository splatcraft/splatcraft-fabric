package net.splatcraft.client.render.entity.inkable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;
import net.splatcraft.client.model.inkable.InkSquidEntityModel;
import net.splatcraft.inkcolor.Inkable;

import static net.splatcraft.client.util.ClientUtil.getVectorColor;

@Environment(EnvType.CLIENT)
public class InkSquidEntityColorFeatureRenderer<T extends LivingEntity> extends FeatureRenderer<T, InkSquidEntityModel<T>> {
    private final EntityModel<T> model;
    private final Identifier texture;

    public InkSquidEntityColorFeatureRenderer(FeatureRendererContext<T, InkSquidEntityModel<T>> ctx, InkSquidEntityModel<T> model, Identifier texture) {
        super(ctx);
        this.model = model;
        this.texture = texture;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertices, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        if (entity instanceof Inkable inkable) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (!entity.isInvisibleTo(client.player)) {
                this.getContextModel().copyStateTo(this.model);
                this.model.animateModel(entity, limbAngle, limbDistance, tickDelta);
                this.model.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);

                boolean invisible = entity.isInvisible();
                RenderLayer renderLayer = invisible
                    ? RenderLayer.getItemEntityTranslucentCull(this.texture)
                    : RenderLayer.getEntityCutoutNoCull(this.texture);
                Vec3f color = getVectorColor(inkable.getInkColor());

                this.model.render(
                    matrices, vertices.getBuffer(renderLayer),
                    light, LivingEntityRenderer.getOverlay(entity, 0.0f),
                    color.getX(), color.getY(), color.getZ(), invisible ? 0.15f : 1.0f
                );
            }
        } else throw new IllegalArgumentException("Trying to render non-inkable entity with %s".formatted(this.getClass().getSimpleName()));
    }
}
