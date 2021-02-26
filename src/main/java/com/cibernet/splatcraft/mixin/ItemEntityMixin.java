package com.cibernet.splatcraft.mixin;

import com.cibernet.splatcraft.item.EntityTickable;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public class ItemEntityMixin {
    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        ItemEntity $this = ItemEntity.class.cast(this);
        ItemStack stack = $this.getStack();
        Item item = stack.getItem();
        if (item instanceof EntityTickable) {
            ((EntityTickable) item).entityTick(stack, $this);
        }
    }
}
