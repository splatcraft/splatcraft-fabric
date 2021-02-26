package com.cibernet.splatcraft.mixin;

import com.cibernet.splatcraft.component.PlayerDataComponent;
import com.cibernet.splatcraft.handler.PlayerHandler;
import com.cibernet.splatcraft.init.SplatcraftAttributes;
import com.cibernet.splatcraft.init.SplatcraftComponents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    @Shadow @Final
    public PlayerAbilities abilities;

    @Inject(method = "createPlayerAttributes", at = @At("RETURN"), cancellable = true)
    private static void createPlayerAttributes(CallbackInfoReturnable<DefaultAttributeContainer.Builder> cir) {
       cir.setReturnValue(cir.getReturnValue().add(SplatcraftAttributes.INK_SWIM_SPEED, SplatcraftAttributes.INK_SWIM_SPEED.getDefaultValue()));
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void tick(CallbackInfo ci) {
        PlayerHandler.onPlayerTick(PlayerEntity.class.cast(this));
    }

    @Inject(method = "isBlockBreakingRestricted", at = @At("HEAD"), cancellable = true)
    private void isBlockBreakingRestricted(World world, BlockPos pos, GameMode gameMode, CallbackInfoReturnable<Boolean> cir) {
        if (PlayerHandler.shouldCancelPlayerToWorldInteraction(PlayerEntity.class.cast(this))) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "attack", at = @At("HEAD"), cancellable = true)
    private void attack(Entity target, CallbackInfo ci) {
        if (PlayerHandler.shouldCancelPlayerToWorldInteraction(PlayerEntity.class.cast(this))) {
            ci.cancel();
        }
    }

    @Inject(method = "getMovementSpeed", at = @At("RETURN"), cancellable = true)
    private void getMovementSpeed(CallbackInfoReturnable<Float> cir) {
        PlayerEntity $this = PlayerEntity.class.cast(this);
        PlayerDataComponent data = SplatcraftComponents.PLAYER_DATA.get($this);

        if (data.isSquid() && !this.abilities.flying) {
            float movementSpeed = PlayerHandler.getMovementSpeed($this, cir.getReturnValueF());
            if (movementSpeed != -1.0F) {
                cir.setReturnValue(cir.getReturnValueF() * movementSpeed);
            }
        }
    }
}
