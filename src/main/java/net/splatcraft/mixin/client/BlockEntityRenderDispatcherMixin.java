package net.splatcraft.mixin.client;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.splatcraft.block.entity.InkedBlockEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntityRenderDispatcher.class)
public abstract class BlockEntityRenderDispatcherMixin {
    @Shadow public abstract @Nullable <E extends BlockEntity> BlockEntityRenderer<E> get(E blockEntity);
    @Shadow public abstract <E extends BlockEntity> void render(E blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertices);

    @Shadow
    private static <T extends BlockEntity> void render(BlockEntityRenderer<T> renderer, T blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertices) {
        throw new AssertionError();
    }

    @Shadow
    private static void runReported(BlockEntity blockEntity, Runnable runnable) {
        throw new AssertionError();
    }

    @Inject(method = "render(Lnet/minecraft/block/entity/BlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;)V", at = @At("HEAD"), cancellable = true)
    private <E extends BlockEntity> void onRender(E blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertices, CallbackInfo ci) {
        // couldn't work out why this wasn't rendering, was rushed and lazy when implementing this, so yeah
        if (blockEntity instanceof InkedBlockEntity) {
            BlockEntityRenderer<E> blockEntityRenderer = this.get(blockEntity);
            runReported(blockEntity, () -> render(blockEntityRenderer, blockEntity, tickDelta, matrices, vertices));
            ci.cancel();
        }
    }
}
