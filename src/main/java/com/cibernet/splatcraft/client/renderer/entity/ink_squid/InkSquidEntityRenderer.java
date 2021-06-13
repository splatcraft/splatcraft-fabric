package com.cibernet.splatcraft.client.renderer.entity.ink_squid;

import com.cibernet.splatcraft.client.init.SplatcraftEntityModelLayers;
import com.cibernet.splatcraft.client.model.entity.InkSquidEntityModel;
import com.cibernet.splatcraft.entity.InkSquidEntity;
import com.cibernet.splatcraft.init.SplatcraftEntities;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class InkSquidEntityRenderer extends LivingEntityRenderer<LivingEntity, InkSquidEntityModel> {
    @SuppressWarnings("unused")
    public InkSquidEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new InkSquidEntityModel(ctx.getPart(SplatcraftEntityModelLayers.INK_SQUID)), 0.2f);
        this.addFeature(new InkSquidEntityColorFeatureRenderer(this, ctx.getModelLoader()));
    }

    @Override
    protected boolean hasLabel(LivingEntity entity) {
        return super.hasLabel(entity) && (entity.shouldRenderName() || entity.hasCustomName() && entity == this.dispatcher.targetedEntity);
    }

    @Override
    public Identifier getTexture(LivingEntity entity) {
        return SplatcraftEntities.texture(InkSquidEntity.id + "/" + InkSquidEntity.id + "_overlay");
    }
}
