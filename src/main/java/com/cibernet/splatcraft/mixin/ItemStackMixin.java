package com.cibernet.splatcraft.mixin;

import com.cibernet.splatcraft.item.InkTankArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    @Shadow @Final @Deprecated
    private Item item;

    @Inject(method = "isDamaged", at = @At("HEAD"), cancellable = true)
    private void isDamaged(CallbackInfoReturnable<Boolean> cir) {
        if (this.item instanceof InkTankArmorItem) {
            cir.setReturnValue(InkTankArmorItem.getInkAmount(ItemStack.class.cast(this)) != ((InkTankArmorItem) this.item).capacity);
        }
    }
    @Inject(method = "getDamage", at = @At("HEAD"), cancellable = true)
    private void getDamage(CallbackInfoReturnable<Integer> cir) {
        if (this.item instanceof InkTankArmorItem) {
            cir.setReturnValue((int) (((InkTankArmorItem) this.item).capacity - InkTankArmorItem.getInkAmount(ItemStack.class.cast(this))));
        }
    }
}
