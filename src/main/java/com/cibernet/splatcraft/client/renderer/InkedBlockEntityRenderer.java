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
        if (blockEntity.getCachedState().getRenderType().equals(BlockRenderType.INVISIBLE)) {
            BakedModel model = blockRendererDispatcher.getModel(state);

            int i = ColorUtils.getInkColor(blockEntity).getColor();
            float r = (float) (i >> 16 & 255) / 255.0F;
            float g = (float) (i >> 8 & 255) / 255.0F;
            float b = (float) (i & 255) / 255.0F;

            renderModel(matrices.peek(), vertices.getBuffer(RenderLayers.getEntityBlockLayer(state, false)), state, model, r, g, b, light, overlay);
        }
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
            float r = MathHelper.clamp(red, 0.0F, 1.0F);
            float g = MathHelper.clamp(green, 0.0F, 1.0F);
            float b = MathHelper.clamp(blue, 0.0F, 1.0F);

            buffer.quad(matrixEntry, bakedquad, r, g, b, light, overlay);
        }
    }
}
