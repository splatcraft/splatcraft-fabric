package com.cibernet.splatcraft.mixin;

import com.cibernet.splatcraft.item.InkTankArmorItem;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(PlayerInventory.class)
public class PlayerInventoryMixin {
    @Inject(method = "damageArmor", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;damage(ILnet/minecraft/entity/LivingEntity;Ljava/util/function/Consumer;)V", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILEXCEPTION, cancellable = true)
    private void preventInkTankDamage(DamageSource damageSource, float amount, int[] slots, CallbackInfo ci, int[] slotIds, int slotsLength, int i, int slotId, ItemStack itemStack) {
        if (itemStack.getItem() instanceof InkTankArmorItem) {
            ci.cancel();
        }
    }
}
