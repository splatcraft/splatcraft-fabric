package com.cibernet.splatcraft.client.renderer.entity.squid_bumper;

import com.cibernet.splatcraft.client.model.SquidBumperEntityModel;
import com.cibernet.splatcraft.entity.SquidBumperEntity;
import com.cibernet.splatcraft.init.SplatcraftEntities;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.entity.feature.EyesFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class SquidBumperEntityEmissiveFeatureRenderer<T extends SquidBumperEntity> extends EyesFeatureRenderer<T, SquidBumperEntityModel<T>> {
    public static final Identifier TEXTURE = SplatcraftEntities.texture(SquidBumperEntity.id + "/" + SquidBumperEntity.id + "_overlay");

    private static final RenderLayer RENDER_LAYER = RenderLayer.of(
        SquidBumperEntity.id + "_emissive", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL,
        7, 256, false, true,
        RenderLayer.MultiPhaseParameters.builder()
            .texture(new RenderPhase.Texture(TEXTURE, false, false))
            .alpha(new RenderPhase.Alpha(0.0001F))
            .build(false)
    );

    public SquidBumperEntityEmissiveFeatureRenderer(FeatureRendererContext<T, SquidBumperEntityModel<T>> featureRendererContext) {
        super(featureRendererContext);
    }

    @Override
    public RenderLayer getEyesTexture() {
        return RENDER_LAYER;
    }
}
