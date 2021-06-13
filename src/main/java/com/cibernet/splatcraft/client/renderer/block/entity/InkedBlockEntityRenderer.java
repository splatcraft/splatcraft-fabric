package com.cibernet.splatcraft.client.renderer.block.entity;

import com.cibernet.splatcraft.block.entity.InkedBlockEntity;
import com.cibernet.splatcraft.inkcolor.ColorUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

@Environment(EnvType.CLIENT)
public class InkedBlockEntityRenderer<T extends BlockEntity> implements BlockEntityRenderer<T> {
    public InkedBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {}

    @Override
    public int getRenderDistance() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void render(T blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (blockEntity instanceof InkedBlockEntity inkedBlockEntity) {
            renderBlock(inkedBlockEntity, MinecraftClient.getInstance().getBlockRenderManager(), matrices, vertexConsumers, light, overlay);
        }
    }

    private static void renderBlock(InkedBlockEntity blockEntity, BlockRenderManager blockRendererDispatcher, MatrixStack matrices, VertexConsumerProvider vertices, int light, int overlay) {
        BlockState state = blockEntity.getSavedState();
        if (blockEntity.getCachedState().getRenderType().equals(BlockRenderType.INVISIBLE)) {
            BakedModel model = blockRendererDispatcher.getModel(state);

            float[] color = ColorUtil.getInkColor(blockEntity).getColorOrLockedComponents();
            renderModel(blockEntity, matrices.peek(), vertices.getBuffer(RenderLayers.getEntityBlockLayer(state, true)), state, model, color[0], color[1], color[2], light, overlay);
        }
    }

    private static void renderModel(InkedBlockEntity blockEntity, MatrixStack.Entry matrix, VertexConsumer buffer, BlockState state, BakedModel model, float red, float green, float blue, int light, int overlay) {
        Random random = new Random();

        for (Direction direction : Direction.values()) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client != null) {
                World world = client.world;
                if (world != null && !Block.isShapeFullCube(world.getBlockState(blockEntity.getPos().offset(direction)).getCullingShape(world, blockEntity.getPos().offset(direction)))) {
                    random.setSeed(42L);
                    renderModelBrightnessColorQuads(matrix, buffer, red, green, blue, model.getQuads(state, direction, random), light, overlay);
                }
            }
        }

        random.setSeed(42L);
        renderModelBrightnessColorQuads(matrix, buffer, red, green, blue, model.getQuads(state, null, random), light, overlay);
    }

    private static void renderModelBrightnessColorQuads(MatrixStack.Entry matrix, VertexConsumer buffer, float red, float green, float blue, List<BakedQuad> quads, int light, int overlay) {
        for (BakedQuad quad : quads) {
            buffer.quad(matrix, quad, red, green, blue, light, overlay);
        }
    }
}
