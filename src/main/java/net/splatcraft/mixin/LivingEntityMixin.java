package net.splatcraft.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.splatcraft.component.PlayerDataComponent;
import net.splatcraft.config.CommonConfig;
import net.splatcraft.tag.SplatcraftEntityTypeTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    private LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    // damage entity if in water or is player and is squid
    @Inject(method = "hurtByWater", at = @At("HEAD"), cancellable = true)
    private void onHurtByWater(CallbackInfoReturnable<Boolean> cir) {
        if (CommonConfig.INSTANCE.hurtInkSquidsInWater.getValue()) {
            LivingEntity that = LivingEntity.class.cast(this);
            if (that instanceof PlayerEntity player) {
                PlayerDataComponent data = PlayerDataComponent.get(player);
                if (data.isSquid()) cir.setReturnValue(true);
            } else if (this.getType().isIn(SplatcraftEntityTypeTags.HURT_BY_WATER)) cir.setReturnValue(true);
        }
    }
}
