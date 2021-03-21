package com.cibernet.splatcraft.mixin;

import com.cibernet.splatcraft.component.PlayerDataComponent;
import com.cibernet.splatcraft.handler.WeaponHandler;
import com.cibernet.splatcraft.inkcolor.ColorUtils;
import com.cibernet.splatcraft.inkcolor.InkBlockUtils;
import com.cibernet.splatcraft.inkcolor.InkColor;
import com.cibernet.splatcraft.inkcolor.InkColors;
import com.cibernet.splatcraft.network.SplatcraftNetworkingConstants;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(method = "damage", at = @At("HEAD"))
    private void damage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        WeaponHandler.onLivingDamage(LivingEntity.class.cast(this), amount);
    }

    //
    // Amend momentum when a player jumps in squid form.
    //

    private InkColor splatcraft_lastLandedBlockInkColor = InkColors.NONE;

    @SuppressWarnings("ConstantConditions")
    @ModifyConstant(method = "travel", constant = @Constant(floatValue = 0.91F, ordinal = 0)) // targets the FIRST place a float is defined as 0.91F, on ground
    private float checkLastLandedBlock(float c) {
        LivingEntity $this = LivingEntity.class.cast(this);
        if ($this instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) $this;
            if (PlayerDataComponent.isSquid(player)) {
                InkColor inkColor = ColorUtils.getInkColor(player.world.getBlockEntity(player.getVelocityAffectingPos()));
                if (inkColor.equals(InkColors.NONE)) {
                    if (player.world.getBlockState(player.getVelocityAffectingPos()).isAir()) {
                        return c;
                    }
                }

                splatcraft_lastLandedBlockInkColor = inkColor;
            }
        }

        return c;
    }
    @SuppressWarnings("ConstantConditions")
    @ModifyConstant(method = "travel", constant = @Constant(floatValue = 0.91F, ordinal = 1)) // targets the SECOND place a float is defined as 0.91F, not on ground
    private float fixSquidAirborneVelocity(float c) {
        LivingEntity $this = LivingEntity.class.cast(this);
        if ($this instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) $this;
            if (PlayerDataComponent.isSquid(player) && splatcraft_lastLandedBlockInkColor != InkColors.NONE && splatcraft_lastLandedBlockInkColor.matches(ColorUtils.getInkColor(player).getColor())) {
                if (!player.world.isClient) {
                    PacketByteBuf buf = PacketByteBufs.create();
                    buf.writeUuid($this.getUuid());

                    buf.writeDouble($this.getX());
                    buf.writeDouble($this.getY());
                    buf.writeDouble($this.getZ());

                    buf.writeString(splatcraft_lastLandedBlockInkColor.toString());
                    buf.writeFloat(0.335F);

                    for (ServerPlayerEntity serverPlayer : PlayerLookup.tracking((ServerWorld) $this.world, $this.getBlockPos())) {
                        ServerPlayNetworking.send(serverPlayer, SplatcraftNetworkingConstants.PLAY_SQUID_TRAVEL_EFFECTS_PACKET_ID, buf);
                    }
                }

                return 1.0F;
            }
        }

        return c; // default to original, pre-mixin value
    }

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "getJumpVelocity", at = @At("RETURN"), cancellable = true)
    private void modifySquidJumpVelocity(CallbackInfoReturnable<Float> cir) {
        LivingEntity $this = LivingEntity.class.cast(this);
        if ($this instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) $this;
            if (PlayerDataComponent.isSquid(player) && InkBlockUtils.canSwim(player)) {
                cir.setReturnValue(cir.getReturnValueF() * 1.36F);
            }
        }
    }
}
