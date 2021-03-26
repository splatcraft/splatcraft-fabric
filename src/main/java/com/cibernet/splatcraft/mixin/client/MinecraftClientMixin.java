package com.cibernet.splatcraft.mixin.client;

import com.cibernet.splatcraft.entity.SquidBumperEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Inject(method = "doItemPick", at = @At("HEAD"))
    private void doItemPick(CallbackInfo ci) {
        MinecraftClient $this = MinecraftClient.class.cast(this);
        if ($this.crosshairTarget != null && $this.crosshairTarget.getType() != HitResult.Type.MISS) {
            assert $this.player != null;
            if ($this.crosshairTarget.getType() == HitResult.Type.ENTITY) {
                Entity entity = ((EntityHitResult)$this.crosshairTarget).getEntity();
                if (entity instanceof SquidBumperEntity) {
                    ItemStack pickedStack = ((SquidBumperEntity) entity).asItem();
                    PlayerInventory playerInventory = $this.player.inventory;

                    int slotIndexWithStack = playerInventory.getSlotWithStack(pickedStack);
                    if ($this.player.abilities.creativeMode) {
                        playerInventory.addPickBlock(pickedStack);
                        assert $this.interactionManager != null;
                        $this.interactionManager.clickCreativeStack($this.player.getStackInHand(Hand.MAIN_HAND), 36 + playerInventory.selectedSlot);
                    } else if (slotIndexWithStack != -1) {
                        if (PlayerInventory.isValidHotbarIndex(slotIndexWithStack)) {
                            playerInventory.selectedSlot = slotIndexWithStack;
                        } else {
                            assert $this.interactionManager != null;
                            $this.interactionManager.pickFromInventory(slotIndexWithStack);
                        }
                    }
                }
            }
        }
    }
}
