package net.splatcraft.client.model.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.splatcraft.item.InkTankItem;
import net.splatcraft.mixin.client.ModelPartAccessor;

import java.util.List;

import static net.splatcraft.item.InkTankItem.getContainedInk;

@SuppressWarnings({ "FieldCanBeLocal", "unused" })
@Environment(EnvType.CLIENT)
public class InkTankModel<T extends LivingEntity> extends SinglePartEntityModel<T> {
    private final ModelPart root;
    private final ModelPart tank;
    private final ModelPart ink;
    private final ModelPart braces;

    public InkTankModel(ModelPart root) {
        this.root = root;
        this.tank = this.root.getChild("tank");
        this.ink = this.root.getChild("ink");
        this.braces = this.root.getChild("braces");

        this.ink.visible = false; // to render manually
    }

    public void copyFrom(BipedEntityModel<T> model) {
        model.copyStateTo(this);
        this.root.copyTransform(model.body);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData data = new ModelData();
        ModelPartData root = data.getRoot();

        ModelPartData tank = root.addChild(
            "tank",
            ModelPartBuilder.create()
                            .uv(18, 0)
                            .cuboid(-2.0F, -24.0F, 2.0F, 4.0F, 2.0F, 4.0F)
                            .uv(0, 0)
                            .cuboid(-3.0F, -22.0F, 1.0F, 6.0F, 9.0F, 6.0F),
            ModelTransform.of(0.0F, 24.0F, 0.0F, 0.0F, 0.0F, 0.0F)
        );

        ModelPartData ink = root.addChild(
            "ink",
            ModelPartBuilder.create()
                            .uv(32, 11)
                            .cuboid(-2.0F, -15.0F, 2.0F, 4.0F, 1.0F, 4.0F)
                            .uv(32, 10)
                            .cuboid(-2.0F, -16.0F, 2.0F, 4.0F, 1.0F, 4.0F)
                            .uv(32, 9)
                            .cuboid(-2.0F, -17.0F, 2.0F, 4.0F, 1.0F, 4.0F)
                            .uv(32, 8)
                            .cuboid(-2.0F, -18.0F, 2.0F, 4.0F, 1.0F, 4.0F)
                            .uv(32, 7)
                            .cuboid(-2.0F, -19.0F, 2.0F, 4.0F, 1.0F, 4.0F)
                            .uv(32, 6)
                            .cuboid(-2.0F, -20.0F, 2.0F, 4.0F, 1.0F, 4.0F)
                            .uv(32, 5)
                            .cuboid(-2.0F, -21.0F, 2.0F, 4.0F, 1.0F, 4.0F),
            ModelTransform.of(0.0F, 24.0F, 0.0F, 0.0F, 0.0F, 0.0F)
        );

        ModelPartData braces = root.addChild(
            "braces",
            ModelPartBuilder.create()
                            .uv(0, 15)
                            .cuboid(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new Dilation(0.5F)),
            ModelTransform.NONE
        );

        return TexturedModelData.of(data, 48, 32);
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {}

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        throw new AssertionError("Should call alternative render method");
    }

    public void render(T entity, MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        super.render(matrices, vertices, light, overlay, red, green, blue, alpha);

        // render ink in parts dependent on percentage of contained ink
        matrices.push();

        this.root.rotate(matrices);
        this.ink.rotate(matrices);

        List<ModelPart.Cuboid> cuboids = ModelPartAccessor.class.cast(this.ink).getCuboids();
        int total = cuboids.size();

        ItemStack stack = entity.getEquippedStack(EquipmentSlot.CHEST);
        float containedInk = getContainedInk(stack);
        float capacity = ((InkTankItem) stack.getItem()).getCapacity();

        int toRender = (int) (total * (containedInk / capacity));
        if (!(containedInk <= 1)) toRender = Math.max(1,  toRender);

        for (int i = 0; i < total && i < toRender; i++) {
            ModelPart.Cuboid cuboid = cuboids.get(i);
            cuboid.renderCuboid(matrices.peek(), vertices, light, overlay, red, green, blue, alpha);
        }

        matrices.pop();
    }

    @Override
    public ModelPart getPart() {
        return this.root;
    }
}
