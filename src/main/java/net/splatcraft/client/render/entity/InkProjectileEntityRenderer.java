package net.splatcraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3f;
import net.splatcraft.client.model.SplatcraftEntityModelLayers;
import net.splatcraft.client.model.entity.InkProjectileEntityModel;
import net.splatcraft.entity.InkProjectileEntity;
import net.splatcraft.inkcolor.InkType;
import net.splatcraft.item.weapon.settings.InkProjectileSettings;

import static net.splatcraft.client.util.ClientUtil.*;

@Environment(EnvType.CLIENT)
public class InkProjectileEntityRenderer<T extends InkProjectileEntity> extends EntityRenderer<T> {
    public static final Identifier TEXTURE = entityTexture("ink_projectile/ink_projectile");

    private final InkProjectileEntityModel model;

    @SuppressWarnings("unused")
    public InkProjectileEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        this.model = new InkProjectileEntityModel(ctx.getPart(SplatcraftEntityModelLayers.INK_PROJECTILE));
    }

    @Override
    public void render(T entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        if (entity.age < 3 && this.dispatcher.camera.getFocusedEntity().squaredDistanceTo(entity) < 12.25d) return;

        matrices.push();

        InkProjectileSettings settings = entity.getProjectileSettings();

        float size = settings.getSize();
        matrices.scale(size, size, size);

        Vec3f color = getVectorColor(entity.getInkColor());
        this.model.render(matrices, vertexConsumers.getBuffer(this.model.getLayer(TEXTURE)), light, OverlayTexture.DEFAULT_UV, color.getX(), color.getY(), color.getZ(), 1);

        matrices.pop();

        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    @Override
    public Identifier getTexture(T entity) {
        return TEXTURE;
    }

    @Override
    protected int getBlockLight(T entity, BlockPos blockPos) {
        return entity.getInkType() == InkType.GLOWING ? 15 : super.getBlockLight(entity, blockPos);
    }
}
