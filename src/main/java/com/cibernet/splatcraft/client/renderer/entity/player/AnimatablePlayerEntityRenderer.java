package com.cibernet.splatcraft.client.renderer.entity.player;

import com.cibernet.splatcraft.client.model.entity.player.SignalPlayerModel;
import com.cibernet.splatcraft.client.signal.Signal;
import com.cibernet.splatcraft.entity.player.signal.AnimatablePlayerEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import software.bernie.geckolib3.renderers.geo.GeoReplacedEntityRenderer;

/**
 * One-time animatable player entity renderer.
 */
@Environment(EnvType.CLIENT)
public class AnimatablePlayerEntityRenderer extends GeoReplacedEntityRenderer<AnimatablePlayerEntity> {
    protected final AnimatablePlayerEntity animatable;
    protected boolean dead = false;

    protected static final int COLOR = Integer.parseInt(Formatting.WHITE.getColorValue() + "");
    protected static final float MAX_LABEL_ANIMATION_TICKS = (60) - 10;
    protected static final int BASE_LABEL_ANIMATION_TICKS = (int) (MAX_LABEL_ANIMATION_TICKS * 0.8f);
    protected int labelAnimationTicks = BASE_LABEL_ANIMATION_TICKS;

    public AnimatablePlayerEntityRenderer(EntityRendererFactory.Context ctx, AnimatablePlayerEntity animatable, boolean thin) {
        super(ctx, new SignalPlayerModel(thin), animatable);
        this.animatable = animatable;
        this.shadowRadius = 0.5f;
        GeoReplacedEntityRenderer.registerReplacedEntity(AnimatablePlayerEntity.class, this);
    }
    public AnimatablePlayerEntityRenderer(EntityRendererFactory.Context ctx, PlayerEntity animatable, Signal signal, boolean thin) {
        this(ctx, new AnimatablePlayerEntity(animatable, signal), thin);
    }

    @Override
    public void render(Entity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertices, int light) {
        if (animatable.isRunning() && AnimatablePlayerEntity.shouldContinue(animatable.player)) {
            // label rendering
            if (labelAnimationTicks <= MAX_LABEL_ANIMATION_TICKS) {
                labelAnimationTicks++;
            }

            matrices.push();

            float y = entity.getHeight() + 0.6f;
            matrices.translate(0.0d, y, 0.0d);
            matrices.multiply(this.dispatcher.getRotation());

            float textScale = -0.023f * MathHelper.sin(
                (float) Math.pow(
                    (1 / ((MAX_LABEL_ANIMATION_TICKS) / labelAnimationTicks)),
                    20.0f // do I really want to be raising something to the power of 20 every tick?
                )
            );
            matrices.scale(textScale, textScale, textScale);

            Matrix4f model = matrices.peek().getModel();
            float backgroundOpacity = MinecraftClient.getInstance().options.getTextBackgroundOpacity(0.27f);
            int backgroundColor = (int) (backgroundOpacity * 255.0f) << 24;
            TextRenderer textRenderer = this.getTextRenderer();
            Text displayText = animatable.signal.text;
            float x = (float) (-textRenderer.getWidth(displayText) / 2);
            textRenderer.draw(displayText, x, 0, COLOR, false, model, vertices, false, backgroundColor, light);

            matrices.pop();

            // global modifications
            float scale = 0.92f;
            matrices.scale(scale, scale, scale); // the model is slightly larger than the default player

            // render player
            super.render(entity, yaw, tickDelta, matrices, vertices, light);
        } else {
            this.dead = true;
        }
    }

    public AnimatablePlayerEntity getAnimatable() {
        return this.animatable;
    }
    public boolean isDead() {
        return this.dead;
    }
}
