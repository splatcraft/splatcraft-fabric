package com.cibernet.splatcraft.mixin.client;

import com.cibernet.splatcraft.entity.SquidBumperEntity;
import com.cibernet.splatcraft.item.DisablesAttack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Shadow @Nullable public HitResult crosshairTarget;
    @Shadow @Nullable public ClientPlayerEntity player;
    @Shadow @Nullable public ClientPlayerInteractionManager interactionManager;

    @Inject(method = "doItemPick", at = @At("HEAD"))
    private void doItemPick(CallbackInfo ci) {
        if (this.crosshairTarget != null && this.crosshairTarget.getType() != HitResult.Type.MISS) {
            assert this.player != null;
            if (this.crosshairTarget.getType() == HitResult.Type.ENTITY) {
                Entity entity = ((EntityHitResult)this.crosshairTarget).getEntity();
                if (entity instanceof SquidBumperEntity) {
                    ItemStack pickedStack = ((SquidBumperEntity) entity).asItem();
                    PlayerInventory playerInventory = this.player.inventory;

                    int slotIndexWithStack = playerInventory.getSlotWithStack(pickedStack);
                    if (this.player.abilities.creativeMode) {
                        playerInventory.addPickBlock(pickedStack);
                        assert this.interactionManager != null;
                        this.interactionManager.clickCreativeStack(this.player.getStackInHand(Hand.MAIN_HAND), 36 + playerInventory.selectedSlot);
                    } else if (slotIndexWithStack != -1) {
                        if (PlayerInventory.isValidHotbarIndex(slotIndexWithStack)) {
                            playerInventory.selectedSlot = slotIndexWithStack;
                        } else {
                            assert this.interactionManager != null;
                            this.interactionManager.pickFromInventory(slotIndexWithStack);
                        }
                    }
                }
            }
        }
    }

    @Inject(method = "doAttack", at = @At("HEAD"), cancellable = true)
    private void doAttack(CallbackInfo ci) {
        if (this.player != null) {
            Item item = this.player.getStackInHand(Hand.MAIN_HAND).getItem();
            if (item instanceof DisablesAttack) {
                ci.cancel();
            }
        }
    }
}
