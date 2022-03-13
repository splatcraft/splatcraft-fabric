package net.splatcraft.impl.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;
import net.splatcraft.api.client.model.SplatcraftEntityModelLayers;
import net.splatcraft.api.client.model.entity.InkTankModel;
import net.splatcraft.api.client.render.InkTankRenderer;
import net.splatcraft.api.inkcolor.InkColor;
import net.splatcraft.api.inkcolor.InkType;
import net.splatcraft.api.inkcolor.Inkable;

import static net.splatcraft.api.client.util.ClientUtil.*;

@Environment(EnvType.CLIENT)
public final class InkTankRendererImpl implements InkTankRenderer {
    public static final Identifier TEXTURE = texture("models/ink_tank/ink_tank");
    public static final Identifier TEXTURE_OVERLAY = texture("models/ink_tank/ink_tank_overlay");

    private InkTankModel<LivingEntity> model = null;

    public InkTankRendererImpl() {}

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertices, ItemStack stack, LivingEntity entity, EquipmentSlot slot, int light, BipedEntityModel<LivingEntity> model) {
        if (!(entity instanceof Inkable inkable)) throw new IllegalArgumentException("Entity rendering ink tank is not inkable");

        if (this.model == null) this.model = new InkTankModel<>(entityRendererFactoryContext().getPart(SplatcraftEntityModelLayers.INK_TANK));
        this.model.copyFrom(model);

        InkColor inkColor = inkable.getInkColor();
        InkType inkType = inkable.getInkType();

        Vec3f color = getVectorColor(inkColor);
        this.render(TEXTURE, entity, stack, matrices, vertices, inkType == InkType.GLOWING ? 0xFF00FF : light, color.getX(), color.getY(), color.getZ());
        this.render(TEXTURE_OVERLAY, entity, stack, matrices, vertices, light);
    }

    public void render(Identifier texture, LivingEntity entity, ItemStack stack, MatrixStack matrices, VertexConsumerProvider vertices, int light, float red, float green, float blue) {
        this.model.render(entity, matrices, ItemRenderer.getArmorGlintConsumer(vertices, RenderLayer.getArmorCutoutNoCull(texture), false, stack.hasGlint()), light, OverlayTexture.DEFAULT_UV, red, green, blue, 1.0f);
    }

    public void render(Identifier texture, LivingEntity entity, ItemStack stack, MatrixStack matrices, VertexConsumerProvider vertices, int light) {
        this.render(texture, entity, stack, matrices, vertices, light, 1.0f, 1.0f, 1.0f);
    }
}
