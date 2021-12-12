package net.splatcraft.client.render.entity.inkable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;
import net.splatcraft.client.model.inkable.InkSquidEntityModel;
import net.splatcraft.inkcolor.Inkable;

import static net.splatcraft.util.SplatcraftUtil.*;

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
            Vec3f color = getVectorColor(inkable.getInkColor());
            FeatureRenderer.render(this.getContextModel(), this.model, this.texture, matrices, vertices, light, entity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch, color.getX(), color.getY(), color.getZ());
        } else {
            throw new IllegalArgumentException("Trying to render non-inkable entity with %s".formatted(this.getClass().getCanonicalName()));
        }
    }
}
