package com.cibernet.splatcraft.mixin;

import com.cibernet.splatcraft.component.PlayerDataComponent;
import com.cibernet.splatcraft.handler.PlayerHandler;
import com.cibernet.splatcraft.handler.WeaponHandler;
import com.cibernet.splatcraft.init.SplatcraftAttributes;
import com.cibernet.splatcraft.init.SplatcraftComponents;
import com.cibernet.splatcraft.inkcolor.ColorUtils;
import com.cibernet.splatcraft.inkcolor.InkBlockUtils;
import com.cibernet.splatcraft.network.SplatcraftNetworkingConstants;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
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
    @Shadow @Final public PlayerAbilities abilities;

    private Vec3d splatcraft_posLastTick = Vec3d.ZERO;

    @Inject(method = "createPlayerAttributes", at = @At("RETURN"), cancellable = true)
    private static void createPlayerAttributes(CallbackInfoReturnable<DefaultAttributeContainer.Builder> cir) {
       cir.setReturnValue(cir.getReturnValue().add(SplatcraftAttributes.INK_SWIM_SPEED, SplatcraftAttributes.INK_SWIM_SPEED.getDefaultValue()));
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void tick(CallbackInfo ci) {
        PlayerEntity player = PlayerEntity.class.cast(this);
        PlayerHandler.onPlayerTick(player);
        WeaponHandler.onPlayerTick(player);
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
    @Inject(method = "travel", at = @At("TAIL"))
    private void travel(Vec3d movementInput, CallbackInfo ci) {
        PlayerEntity $this = PlayerEntity.class.cast(this);

        if ($this.world instanceof ServerWorld) {
            double threshold = 0.13D;
            if (InkBlockUtils.shouldBeSubmerged($this) && (Math.abs(splatcraft_posLastTick.getX() - $this.getX()) >= threshold || Math.abs(splatcraft_posLastTick.getY() - $this.getY()) >= threshold || Math.abs(splatcraft_posLastTick.getZ() - $this.getZ()) >= threshold)) {
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeUuid($this.getUuid());

                buf.writeDouble($this.getX());
                buf.writeDouble($this.getY());
                buf.writeDouble($this.getZ());

                buf.writeString(ColorUtils.getInkColor($this).toString());
                buf.writeFloat(1.0F);

                for (ServerPlayerEntity serverPlayer : PlayerLookup.tracking((ServerWorld) $this.world, $this.getBlockPos())) {
                    ServerPlayNetworking.send(serverPlayer, SplatcraftNetworkingConstants.PLAY_SQUID_TRAVEL_EFFECTS_PACKET_ID, buf);
                }
            }

            splatcraft_posLastTick = $this.getPos();
        }
    }

    @Inject(method = "getDimensions", at = @At("HEAD"), cancellable = true)
    private void getDimensions(EntityPose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        PlayerDataComponent data = SplatcraftComponents.PLAYER_DATA.get(PlayerEntity.class.cast(this));
        if (data.isSquid()) {
            cir.setReturnValue(pose == EntityPose.FALL_FLYING ? PlayerHandler.SQUID_FORM_DIMENSIONS : PlayerHandler.SQUID_FORM_AIRBORNE_DIMENSIONS);
        }
    }
    @Inject(method = "getActiveEyeHeight", at = @At("RETURN"), cancellable = true)
    private void getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions, CallbackInfoReturnable<Float> cir) {
        if (pose != EntityPose.FALL_FLYING) {
            try {
                PlayerEntity $this = PlayerEntity.class.cast(this);
                PlayerDataComponent data = SplatcraftComponents.PLAYER_DATA.get($this);
                if (data.isSquid()) {
                    cir.setReturnValue(cir.getReturnValueF() / 2);
                }
            } catch (NullPointerException ignored) {}
        }
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

    @Inject(method = "getDeathSound", at = @At("HEAD"), cancellable = true)
    private void getDeathSound(CallbackInfoReturnable<SoundEvent> cir) {
        PlayerEntity $this = PlayerEntity.class.cast(this);
        PlayerDataComponent data = SplatcraftComponents.PLAYER_DATA.get($this);
        if (data.isSquid()) {
            cir.setReturnValue(SoundEvents.ENTITY_COD_DEATH);
        }
    }
    @Inject(method = "getHurtSound", at = @At("HEAD"), cancellable = true)
    private void getHurtSound(CallbackInfoReturnable<SoundEvent> cir) {
        PlayerEntity $this = PlayerEntity.class.cast(this);
        PlayerDataComponent data = SplatcraftComponents.PLAYER_DATA.get($this);
        if (data.isSquid()) {
            cir.setReturnValue(SoundEvents.ENTITY_COD_HURT);
        }
    }
}
