package net.splatcraft.client.renderer.inkable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.splatcraft.client.model.SplatcraftEntityModelLayers;
import net.splatcraft.client.model.inkable.InkSquidEntityModel;
import net.splatcraft.util.SplatcraftUtil;

@Environment(EnvType.CLIENT)
public class InkSquidEntityModelRenderer<T extends LivingEntity> extends LivingEntityRenderer<T, InkSquidEntityModel<T>> {
    public InkSquidEntityModelRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new InkSquidEntityModel<>(ctx.getPart(SplatcraftEntityModelLayers.INK_SQUID)), 0.2f);
        this.addFeature(new InkSquidEntityColorFeatureRenderer<>(this, new InkSquidEntityModel<>(ctx.getPart(SplatcraftEntityModelLayers.INK_SQUID)), SplatcraftUtil.entityTexture("ink_squid/ink_squid")));
    }

    @Override
    protected boolean hasLabel(T entity) {
        return super.hasLabel(entity) && (entity.shouldRenderName() || entity.hasCustomName() && entity == this.dispatcher.targetedEntity);
    }

    @Override
    public Identifier getTexture(T entity) {
        return SplatcraftUtil.entityTexture("ink_squid/ink_squid_overlay");
    }
}
