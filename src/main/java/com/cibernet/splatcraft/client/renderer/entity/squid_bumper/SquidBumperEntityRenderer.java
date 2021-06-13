package com.cibernet.splatcraft.client.renderer.entity.squid_bumper;

import com.cibernet.splatcraft.client.init.SplatcraftEntityModelLayers;
import com.cibernet.splatcraft.client.model.entity.SquidBumperEntityModel;
import com.cibernet.splatcraft.entity.SquidBumperEntity;
import com.cibernet.splatcraft.entity.enums.SquidBumperDisplayType;
import com.cibernet.splatcraft.init.SplatcraftEntities;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class SquidBumperEntityRenderer<T extends SquidBumperEntity> extends LivingEntityRenderer<T, SquidBumperEntityModel<T>> {
    public static final Identifier TEXTURE = SplatcraftEntities.texture(SquidBumperEntity.id + "/" + SquidBumperEntity.id);

    @SuppressWarnings("unused")
    public SquidBumperEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new SquidBumperEntityModel<>(ctx.getPart(SplatcraftEntityModelLayers.SQUID_BUMPER)), 0.5f);
        this.addFeature(new SquidBumperEntityColorFeatureRenderer<>(this));
        this.addFeature(new SquidBumperEntityEmissiveFeatureRenderer<>(this));
    }

    @Override
    protected void renderLabelIfPresent(T entity, Text text, MatrixStack matrices, VertexConsumerProvider vertices, int light) {
        SquidBumperDisplayType displayType = entity.getDisplayType();
        boolean shouldDisplay = entity.isFlattened() || entity.getHurtDelay() > 0 || displayType.alwaysDisplay();
        if (entity.isCustomNameVisible() || shouldDisplay) {
            super.renderLabelIfPresent(
                entity,
                entity.hasCustomName() && entity.isCustomNameVisible()
                    ? text
                    : shouldDisplay
                        ? new TranslatableText(
                                SplatcraftEntities.SQUID_BUMPER.getTranslationKey() + ".name." + displayType.getSnakeCase(),
                                displayType.getObjectToDisplay(entity), displayType.getAltObjectToDisplay(entity)
                            ).formatted(Formatting.RED)
                        : text,
                matrices, vertices, light
            );
        }
    }

    @Override
    protected boolean isShaking(T entity) {
        return entity.getLastHitTime() > 0 || entity.getHurtDelay() >= entity.maxHurtTime - (entity.maxHurtTime / 10);
    }

    @Override
    public Identifier getTexture(T entity) {
        return TEXTURE;
    }
}
