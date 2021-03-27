package com.cibernet.splatcraft.client.renderer.entity.squid_bumper;

import com.cibernet.splatcraft.client.model.entity.SquidBumperEntityModel;
import com.cibernet.splatcraft.entity.SquidBumperEntity;
import com.cibernet.splatcraft.init.SplatcraftEntities;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class SquidBumperEntityRenderer<T extends SquidBumperEntity> extends LivingEntityRenderer<T, SquidBumperEntityModel<T>> {
    public static final Identifier TEXTURE = SplatcraftEntities.texture(SquidBumperEntity.id + "/" + SquidBumperEntity.id);

    @SuppressWarnings("unused")
    public SquidBumperEntityRenderer(EntityRenderDispatcher dispatcher, @Nullable EntityRendererRegistry.Context ctx) {
        super(dispatcher, new SquidBumperEntityModel<>(), 0.5f);
        this.addFeature(new SquidBumperEntityColorFeatureRenderer<>(this));
        this.addFeature(new SquidBumperEntityEmissiveFeatureRenderer<>(this));
    }

    @Override
    protected void renderLabelIfPresent(T entity, Text text, MatrixStack matrices, VertexConsumerProvider vertices, int light) {
        if (entity == this.dispatcher.targetedEntity || entity.isFlattened()) {
            super.renderLabelIfPresent(
                entity,
                entity.hasCustomName() && entity.isCustomNameVisible()
                    ? text
                    : entity.isFlattened()
                        ? new TranslatableText(SplatcraftEntities.SQUID_BUMPER.getTranslationKey() + ".name", entity.getPreFlattenDamage()).formatted(Formatting.RED)
                        : text,
                matrices, vertices, light
            );
        }
    }

    @Override
    public Identifier getTexture(T entity) {
        return TEXTURE;
    }
}
