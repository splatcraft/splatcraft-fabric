package net.splatcraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.splatcraft.client.model.SplatcraftEntityModelLayers;
import net.splatcraft.client.model.entity.InkSquidEntityModel;

import static net.splatcraft.client.util.ClientUtil.*;

@Environment(EnvType.CLIENT)
public class InkSquidEntityModelRenderer<T extends LivingEntity> extends LivingEntityRenderer<T, InkSquidEntityModel<T>> {
    public static final Identifier TEXTURE = entityTexture("ink_squid/ink_squid_overlay");

    public InkSquidEntityModelRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, createModel(ctx), 0.2f);
        this.addFeature(new InkSquidEntityColorFeatureRenderer<>(this, createModel(ctx), entityTexture("ink_squid/ink_squid")));
    }

    @Override
    protected boolean hasLabel(T entity) {
        return super.hasLabel(entity) && (entity.shouldRenderName() || entity.hasCustomName() && entity == this.dispatcher.targetedEntity);
    }

    @Override
    public Identifier getTexture(T entity) {
        return TEXTURE;
    }

    public static <T extends LivingEntity> InkSquidEntityModel<T> createModel(EntityRendererFactory.Context ctx) {
        return new InkSquidEntityModel<>(ctx.getPart(SplatcraftEntityModelLayers.INK_SQUID));
    }
}
