package com.cibernet.splatcraft.client.renderer.block.entity;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.block.StageBarrierBlock;
import com.cibernet.splatcraft.block.entity.StageBarrierBlockEntity;
import com.cibernet.splatcraft.client.config.SplatcraftConfig;
import com.cibernet.splatcraft.handler.RendererHandler;
import com.cibernet.splatcraft.tag.SplatcraftBlockTags;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.Objects;

@Environment(EnvType.CLIENT)
@SuppressWarnings("deprecation")
public class StageBarrierBlockEntityRenderer extends BlockEntityRenderer<StageBarrierBlockEntity> {
    private static final RenderLayer BARRIER_RENDER = RenderLayer.of(new Identifier(Splatcraft.MOD_ID, StageBarrierBlock.id + "s").toString(), VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, 7, 131072, true, true, RenderLayer.MultiPhaseParameters.builder().shadeModel(new RenderPhase.ShadeModel(true)).lightmap(new RenderPhase.Lightmap(true)).texture(new RenderPhase.Texture(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, false, true)).alpha(new RenderPhase.Alpha(0.003921569f)).transparency(RendererHandler.TRANSLUCENT_TRANSPARENCY).build(true));

    public StageBarrierBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    private static void addVertex(VertexConsumer builder, MatrixStack matrixStack, float x, float y, float z, float textureX, float textureY, float r, float g, float b, float a) {
        builder.vertex(matrixStack.peek().getModel(), x, y, z)
            .color(r, g, b, a)
            .texture(textureX, textureY)
            .light(0, 240)
            .normal(1, 0, 0)
            .next();
    }

    @Override
    public void render(StageBarrierBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertices, int light, int overlay) {
        World world = blockEntity.getWorld();
        if (world != null && world.isClient) {
            PlayerEntity player = MinecraftClient.getInstance().player;
            if (player != null && player.isCreative()) {
                Block block = blockEntity.getCachedState().getBlock();
                if (!SplatcraftConfig.RENDER.holdStageBarrierToRender.value || player.getMainHandStack().getItem() == block.asItem() || player.getOffHandStack().getItem() == block.asItem()) {
                    int barrierRenderDistance = SplatcraftConfig.RENDER.barrierRenderDistance.value;
                    if (player.squaredDistanceTo(Vec3d.of(blockEntity.getPos())) <= barrierRenderDistance * barrierRenderDistance) {
                        blockEntity.incrementActiveTime();
                    }
                }
            }
        }

        float rawActiveTime = blockEntity.getActiveTime();
        float activeTime = rawActiveTime == 0 ? 0 : MathHelper.clamp(rawActiveTime + 1, 0, 20);
        Block block = Objects.requireNonNull(blockEntity.getWorld()).getBlockState(blockEntity.getPos()).getBlock();

        if (activeTime <= 0 || !(block instanceof StageBarrierBlock)) {
            return;
        }

        Sprite sprite = MinecraftClient.getInstance().getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).apply(new Identifier(Splatcraft.MOD_ID, "block/" + Registry.BLOCK.getId(block).getPath()));
        VertexConsumer builder = vertices.getBuffer(BARRIER_RENDER);

        float alpha = activeTime / blockEntity.getMaxActiveTime();

        matrices.push();

        if (canRenderSide(blockEntity, Direction.NORTH)) {
            addVertex(builder, matrices, 0, 1, 0, sprite.getMinU(), sprite.getMaxV(), 1, 1, 1, alpha);
            addVertex(builder, matrices, 1, 1, 0, sprite.getMaxU(), sprite.getMaxV(), 1, 1, 1, alpha);
            addVertex(builder, matrices, 1, 0, 0, sprite.getMaxU(), sprite.getMinV(), 1, 1, 1, alpha);
            addVertex(builder, matrices, 0, 0, 0, sprite.getMinU(), sprite.getMinV(), 1, 1, 1, alpha);
        }

        if (canRenderSide(blockEntity, Direction.SOUTH)) {
            addVertex(builder, matrices, 0, 0, 1, sprite.getMinU(), sprite.getMinV(), 1, 1, 1, alpha);
            addVertex(builder, matrices, 1, 0, 1, sprite.getMaxU(), sprite.getMinV(), 1, 1, 1, alpha);
            addVertex(builder, matrices, 1, 1, 1, sprite.getMaxU(), sprite.getMaxV(), 1, 1, 1, alpha);
            addVertex(builder, matrices, 0, 1, 1, sprite.getMinU(), sprite.getMaxV(), 1, 1, 1, alpha);
        }

        if (canRenderSide(blockEntity, Direction.WEST)) {
            addVertex(builder, matrices, 0, 0, 0, sprite.getMinU(), sprite.getMinV(), 1, 1, 1, alpha);
            addVertex(builder, matrices, 0, 0, 1, sprite.getMinU(), sprite.getMaxV(), 1, 1, 1, alpha);
            addVertex(builder, matrices, 0, 1, 1, sprite.getMaxU(), sprite.getMaxV(), 1, 1, 1, alpha);
            addVertex(builder, matrices, 0, 1, 0, sprite.getMaxU(), sprite.getMinV(), 1, 1, 1, alpha);
        }

        if (canRenderSide(blockEntity, Direction.EAST)) {
            addVertex(builder, matrices, 1, 0, 0, sprite.getMinU(), sprite.getMinV(), 1, 1, 1, alpha);
            addVertex(builder, matrices, 1, 1, 0, sprite.getMaxU(), sprite.getMinV(), 1, 1, 1, alpha);
            addVertex(builder, matrices, 1, 1, 1, sprite.getMaxU(), sprite.getMaxV(), 1, 1, 1, alpha);
            addVertex(builder, matrices, 1, 0, 1, sprite.getMinU(), sprite.getMaxV(), 1, 1, 1, alpha);
        }

        if (canRenderSide(blockEntity, Direction.DOWN)) {
            addVertex(builder, matrices, 0, 0, 0, sprite.getMinU(), sprite.getMinV(), 1, 1, 1, alpha);
            addVertex(builder, matrices, 1, 0, 0, sprite.getMaxU(), sprite.getMinV(), 1, 1, 1, alpha);
            addVertex(builder, matrices, 1, 0, 1, sprite.getMaxU(), sprite.getMaxV(), 1, 1, 1, alpha);
            addVertex(builder, matrices, 0, 0, 1, sprite.getMinU(), sprite.getMaxV(), 1, 1, 1, alpha);
        }

        if (canRenderSide(blockEntity, Direction.UP)) {
            addVertex(builder, matrices, 0, 1, 1, sprite.getMinU(), sprite.getMaxV(), 1, 1, 1, alpha);
            addVertex(builder, matrices, 1, 1, 1, sprite.getMaxU(), sprite.getMaxV(), 1, 1, 1, alpha);
            addVertex(builder, matrices, 1, 1, 0, sprite.getMaxU(), sprite.getMinV(), 1, 1, 1, alpha);
            addVertex(builder, matrices, 0, 1, 0, sprite.getMinU(), sprite.getMinV(), 1, 1, 1, alpha);
        }

        matrices.pop();
    }

    private static boolean canRenderSide(BlockEntity blockEntity, Direction side) {
        World world = blockEntity.getWorld();
        if (world != null) {
            BlockPos pos = blockEntity.getPos().offset(side);
            BlockState state = world.getBlockState(pos);
            boolean blockIsStageBarrier = state.getBlock().isIn(SplatcraftBlockTags.STAGE_BARRIERS);
            if (!blockIsStageBarrier) {
                return true;
            } else {
                BlockEntity offsetBlockEntity = world.getBlockEntity(pos);
                return offsetBlockEntity instanceof StageBarrierBlockEntity && !((StageBarrierBlockEntity) offsetBlockEntity).isActive();
            }
        }

        return false;
    }
}
