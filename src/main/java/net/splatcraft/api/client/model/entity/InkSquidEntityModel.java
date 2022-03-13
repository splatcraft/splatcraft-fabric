package net.splatcraft.api.client.model.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class InkSquidEntityModel<T extends LivingEntity> extends SinglePartEntityModel<T> {
    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart leftTentacle;
    private final ModelPart rightTentacle;

    public InkSquidEntityModel(ModelPart root) {
        this.root = root;
        this.body = root.getChild("body");
        this.leftTentacle = root.getChild("left_tentacle");
        this.rightTentacle = root.getChild("right_tentacle");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData data = new ModelData();

        ModelPartData root = data.getRoot();

        root.addChild(
            "body",
            ModelPartBuilder.create()
                            .uv(0, 16)
                            .cuboid(-4.0f, -1.5f, -12.0f, 8.0f, 3.0f, 3.0f)
                            .uv(0, 0)
                            .cuboid(-5.5f, -2.0f, -9.0f, 11.0f, 4.0f, 5.0f)
                            .uv(0, 9)
                            .cuboid(-3.5f, -1.5f, -4.0f, 7.0f, 3.0f, 4.0f)
                            .uv(20, 20)
                            .cuboid(-3.0f, -1.5f, 0.0f, 6.0f, 3.0f, 2.0f),
            ModelTransform.pivot(0.0f, 22.5f, 3.0f)
        );

        root.addChild(
            "left_tentacle",
            ModelPartBuilder.create()
                            .mirrored()
                            .uv(10, 24)
                            .cuboid(-1.0f, -1.0f, 1.0f, 2.0f, 2.0f, 4.0f)
                            .mirrored()
                            .uv(0, 22)
                            .cuboid(-2.0f, -1.0f, 5.0f, 3.0f, 2.0f, 4.0f),
            ModelTransform.pivot(2.5f, 22.5f, 2.0f)
        );
        root.addChild(
            "right_tentacle",
            ModelPartBuilder.create()
                            .uv(10, 24)
                            .cuboid(-1.0f, -1.0f, 1.0f, 2.0f, 2.0f, 4.0f)
                            .uv(0, 22)
                            .cuboid(-1.0f, -1.0f, 5.0f, 3.0f, 2.0f, 4.0f),
            ModelTransform.pivot(-2.5f, 22.5f, 2.0f)
        );

        return TexturedModelData.of(data, 48, 32);
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        boolean isSwimming = entity.isSwimming();

        if (!entity.hasVehicle()) {
            float angle = isSwimming
                ? (float) -((entity.getPitch() * Math.PI) / 180F)
                : (float) (entity.getY() - entity.prevY) * 1.1f;
            this.body.pitch = (float) -Math.min(Math.PI / 2, Math.max(-Math.PI / 2, angle));

            this.leftTentacle.pitch = this.body.pitch / 2;
            this.rightTentacle.pitch = this.body.pitch / 2;
        }

        this.leftTentacle.yaw = MathHelper.cos(limbAngle * 0.6662f + (float) Math.PI) * 1.4f * limbDistance / (isSwimming ? 2.2f : 1.5f);
        this.rightTentacle.yaw = MathHelper.cos(limbAngle * 0.6662f) * 1.4f * limbDistance / (isSwimming ? 2.2f : 1.5f);

        boolean tentacleVisible = !entity.isSpectator();
        this.leftTentacle.visible = tentacleVisible;
        this.rightTentacle.visible = tentacleVisible;
    }

    @Override
    public ModelPart getPart() {
        return this.root;
    }
}
