package net.splatcraft.mixin;

import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import net.splatcraft.component.PlayerDataComponent;
import net.splatcraft.inkcolor.Inkable;
import net.splatcraft.inkcolor.InkColor;
import net.splatcraft.util.SplatcraftConstants;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.splatcraft.util.SplatcraftConstants.*;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements Inkable {
    @Shadow public abstract Text getDisplayName();

    private PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public InkColor getInkColor() {
        PlayerEntity that = PlayerEntity.class.cast(this);
        PlayerDataComponent data = PlayerDataComponent.get(that);
        return data.getInkColor();
    }

    @Override
    public boolean setInkColor(InkColor inkColor) {
        PlayerEntity that = PlayerEntity.class.cast(this);
        PlayerDataComponent data = PlayerDataComponent.get(that);
        return data.setInkColor(inkColor);
    }

    @Override
    public Text getTextForCommand() {
        return this.getDisplayName();
    }

    // change pose for squid form
    @Inject(method = "updatePose", at = @At("HEAD"), cancellable = true)
    private void onUpdatePose(CallbackInfo ci) {
        PlayerEntity that = PlayerEntity.class.cast(this);
        PlayerDataComponent data = PlayerDataComponent.get(that);
        if (data.isSquid()) {
            this.setPose(EntityPose.SWIMMING);
            ci.cancel();
        }
    }

    // replace dimensions for squid form
    @Inject(method = "getDimensions", at = @At("HEAD"), cancellable = true)
    private void onGetDimensions(EntityPose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        PlayerEntity that = PlayerEntity.class.cast(this);
        PlayerDataComponent data = PlayerDataComponent.get(that);
        if (data.isSquid()) {
            cir.setReturnValue(data.isSubmerged() ? SQUID_FORM_SUBMERGED_DIMENSIONS : SQUID_FORM_DIMENSIONS);
        }
    }

    // replace eye height for squid form
    @Inject(method = "getActiveEyeHeight", at = @At("HEAD"), cancellable = true)
    private void getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions, CallbackInfoReturnable<Float> cir) {
        PlayerEntity that = PlayerEntity.class.cast(this);
        try {
            PlayerDataComponent data = PlayerDataComponent.get(that);
            if (data.isSquid()) {
                cir.setReturnValue(SplatcraftConstants.getEyeHeight(data.isSubmerged()));
            }
        } catch (NullPointerException ignored) {}
    }
}
