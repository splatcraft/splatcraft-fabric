package com.cibernet.splatcraft.client.renderer.entity.squid_bumper;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.client.model.entity.SquidBumperEntityModel;
import com.cibernet.splatcraft.entity.SquidBumperEntity;
import com.cibernet.splatcraft.init.SplatcraftEntities;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.entity.feature.EyesFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class SquidBumperEntityEmissiveFeatureRenderer<T extends SquidBumperEntity> extends EyesFeatureRenderer<T, SquidBumperEntityModel<T>> {
    public static final Identifier TEXTURE = SplatcraftEntities.texture(SquidBumperEntity.id + "/" + SquidBumperEntity.id + "_overlay");

    private static final RenderLayer RENDER_LAYER = RenderLayer.of(
        new Identifier(Splatcraft.MOD_ID, SquidBumperEntity.id + "_emissive").toString(),
        VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL,
        VertexFormat.DrawMode.QUADS, 256, false, true,
        RenderLayer.MultiPhaseParameters.builder()
            .shader(RenderLayer.EYES_SHADER)
            .texture(new RenderPhase.Texture(TEXTURE, false, false))
            .transparency(RenderLayer.ADDITIVE_TRANSPARENCY)
            .writeMaskState(RenderLayer.COLOR_MASK)
            .build(false)
    );

    public SquidBumperEntityEmissiveFeatureRenderer(FeatureRendererContext<T, SquidBumperEntityModel<T>> ctx) {
        super(ctx);
    }

    @Override
    public RenderLayer getEyesTexture() {
        return RENDER_LAYER;
    }
}
