package com.cibernet.splatcraft.client.renderer;

import com.cibernet.splatcraft.block.entity.InkedBlockEntity;
import com.cibernet.splatcraft.inkcolor.ColorUtils;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

import java.util.List;
import java.util.Random;

public class InkedBlockEntityRenderer extends BlockEntityRenderer<InkedBlockEntity> {
    public InkedBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(InkedBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        renderBlock(blockEntity, MinecraftClient.getInstance().getBlockRenderManager(), matrices, vertexConsumers, light, overlay);
    }

    private static void renderBlock(InkedBlockEntity blockEntity, BlockRenderManager blockRendererDispatcher, MatrixStack matrices, VertexConsumerProvider vertices, int light, int overlay) {
        BlockState state = blockEntity.getSavedState();
        BlockRenderType renderType = blockEntity.getSavedState().getRenderType();
        if (renderType.equals(BlockRenderType.MODEL)) {
            state = blockEntity.getSavedState();
        }

        BakedModel model = blockRendererDispatcher.getModel(state);
        int i = ColorUtils.getInkColor(blockEntity).getColor();
        float f = (float)(i >> 16 & 255) / 255.0F;
        float f1 = (float)(i >> 8 & 255) / 255.0F;
        float f2 = (float)(i & 255) / 255.0F;

        //f = 0;
        //f1 = 1;
        //f2 = 1;

        renderModel(matrices.peek(), vertices.getBuffer(RenderLayers.getEntityBlockLayer(state, false)), state, model, f, f1, f2, light, overlay);

    }

    private static void renderModel(MatrixStack.Entry matrices, VertexConsumer buffer, BlockState state, BakedModel model, float red, float green, float blue, int light, int overlay) {
        Random random = new Random();

        for (Direction direction : Direction.values()) {
            random.setSeed(42L);
            renderModelBrightnessColorQuads(matrices, buffer, red, green, blue, model.getQuads(state, direction, random), light, overlay);
        }

        random.setSeed(42L);
        renderModelBrightnessColorQuads(matrices, buffer, red, green, blue, model.getQuads(state, null, random), light, overlay);
    }

    private static void renderModelBrightnessColorQuads(MatrixStack.Entry matrixEntry, VertexConsumer buffer, float red, float green, float blue, List<BakedQuad> quads, int light, int overlay) {
        for (BakedQuad bakedquad : quads) {
            float f = MathHelper.clamp(red, 0.0F, 1.0F);
            float f1 = MathHelper.clamp(green, 0.0F, 1.0F);
            float f2 = MathHelper.clamp(blue, 0.0F, 1.0F);

            buffer.quad(matrixEntry, bakedquad, f, f1, f2, light, overlay);
        }

    }
}
