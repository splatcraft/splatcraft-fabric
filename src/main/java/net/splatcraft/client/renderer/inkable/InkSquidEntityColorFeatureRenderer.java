package net.splatcraft.client.renderer.inkable;

import me.shedaniel.math.Color;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.splatcraft.client.model.inkable.InkSquidEntityModel;
import net.splatcraft.inkcolor.Inkable;

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
            Color color = inkable.getInkColor().getColor();

            float red = color.getRed() / 255.0f;
            float green = color.getGreen() / 255.0f;
            float blue = color.getBlue() / 255.0f;

            FeatureRenderer.render(this.getContextModel(), this.model, this.texture, matrices, vertices, light, entity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch, red, green, blue);
        } else {
            throw new IllegalArgumentException("Trying to render non-inkable entity with %s".formatted(this.getClass().getCanonicalName()));
        }
    }
}
