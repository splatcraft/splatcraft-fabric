package com.cibernet.splatcraft.client.renderer.block.entity;

import com.cibernet.splatcraft.block.entity.InkedBlockEntity;
import com.cibernet.splatcraft.inkcolor.ColorUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
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

@Environment(EnvType.CLIENT)
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

            float[] color = ColorUtils.getColorsFromInt(ColorUtils.getInkColor(blockEntity).getColorOrLocked());
            renderModel(matrices.peek(), vertices.getBuffer(RenderLayers.getEntityBlockLayer(state, true)), state, model, color[0], color[1], color[2], light, overlay, blockEntity);
        }
    }

    private static void renderModel(MatrixStack.Entry matrix, VertexConsumer buffer, BlockState state, BakedModel model, float red, float green, float blue, int light, int overlay, BlockEntity blockEntity) {
        Random random = new Random();

        for (Direction direction : Direction.values()) {
            /*World world = MinecraftClient.getInstance().world;
            if (world != null && Block.shouldDrawSide(state, world, blockEntity.getPos(), direction)) {*/
                random.setSeed(42L);
                renderModelBrightnessColorQuads(matrix, buffer, red, green, blue, model.getQuads(state, direction, random), light, overlay);
            /*}*/
        }

        random.setSeed(42L);
        renderModelBrightnessColorQuads(matrix, buffer, red, green, blue, model.getQuads(state, null, random), light, overlay);
    }

    private static void renderModelBrightnessColorQuads(MatrixStack.Entry matrix, VertexConsumer buffer, float red, float green, float blue, List<BakedQuad> quads, int light, int overlay) {
        for (BakedQuad bakedquad : quads) {
            float r = MathHelper.clamp(red, 0.0f, 1.0f);
            float g = MathHelper.clamp(green, 0.0f, 1.0f);
            float b = MathHelper.clamp(blue, 0.0f, 1.0f);

            buffer.quad(matrix, bakedquad, r, g, b, light, overlay);
        }
    }
}
