package net.splatcraft.client.render.entity;

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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;
import net.splatcraft.client.model.SplatcraftEntityModelLayers;
import net.splatcraft.client.model.entity.InkTankModel;
import net.splatcraft.component.PlayerDataComponent;
import net.splatcraft.inkcolor.InkColor;
import net.splatcraft.inkcolor.InkColors;

import static net.splatcraft.client.util.ClientUtil.entityRendererFactoryContext;
import static net.splatcraft.client.util.ClientUtil.texture;

@Environment(EnvType.CLIENT)
public class InkTankRenderer {
    public static final InkTankRenderer INSTANCE = new InkTankRenderer();

    public static final Identifier TEXTURE = texture("models/ink_tank/ink_tank");
    public static final Identifier TEXTURE_OVERLAY = texture("models/ink_tank/ink_tank_overlay");

    protected InkTankModel<LivingEntity> model = null;

    protected InkTankRenderer() {}

    @SuppressWarnings("unused")
    public void render(MatrixStack matrices, VertexConsumerProvider vertices, ItemStack stack, LivingEntity entity, EquipmentSlot slot, int light, BipedEntityModel<LivingEntity> contextModel) {
        if (this.model == null) this.model = new InkTankModel<>(entityRendererFactoryContext().getPart(SplatcraftEntityModelLayers.INK_TANK));
        this.model.copyFrom(contextModel);

        PlayerDataComponent data = entity instanceof PlayerEntity player ? PlayerDataComponent.get(player) : null;
        InkColor inkColor = data == null ? InkColors.getDefault() : data.getInkColor();

        Vec3f color = inkColor.getVectorColor();
        this.render(TEXTURE, entity, stack, matrices, vertices, light, color.getX(), color.getY(), color.getZ());
        this.render(TEXTURE_OVERLAY, entity, stack, matrices, vertices, light);
    }

    public void render(Identifier texture, LivingEntity entity, ItemStack stack, MatrixStack matrices, VertexConsumerProvider vertices, int light, float red, float green, float blue) {
        this.model.render(entity, matrices, ItemRenderer.getArmorGlintConsumer(vertices, RenderLayer.getArmorCutoutNoCull(texture), false, stack.hasGlint()), light, OverlayTexture.DEFAULT_UV, red, green, blue, 1.0f);
    }

    public void render(Identifier texture, LivingEntity entity, ItemStack stack, MatrixStack matrices, VertexConsumerProvider vertices, int light) {
        this.render(texture, entity, stack, matrices, vertices, light, 1.0f, 1.0f, 1.0f);
    }
}
