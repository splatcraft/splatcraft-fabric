package com.cibernet.splatcraft.mixin;

import com.cibernet.splatcraft.component.LazyPlayerDataComponent;
import com.cibernet.splatcraft.item.AttackInputDetectable;
import com.cibernet.splatcraft.network.SplatcraftNetworking;
import com.cibernet.splatcraft.network.SplatcraftNetworkingConstants;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {
    @Inject(method = "onDeath", at = @At("HEAD"))
    private void onDeath(DamageSource source, CallbackInfo ci) {
        ServerPlayerEntity $this = ServerPlayerEntity.class.cast(this);
        SplatcraftNetworking.sendPlayInkDeathEffects($this);
    }

    @Inject(method = "swingHand", at = @At("TAIL"))
    private void swingHand(Hand hand, CallbackInfo ci) {
        ServerPlayerEntity player = ServerPlayerEntity.class.cast(this);
        if (!player.isSpectator() && !LazyPlayerDataComponent.isSquid(player)) {
            ItemStack stack = player.getStackInHand(hand);
            Item item = stack.getItem();
            if (item instanceof AttackInputDetectable) {
                ((AttackInputDetectable) item).onAttack(player, stack);
            }

            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeUuid(player.getUuid());
            ServerPlayNetworking.send(player, SplatcraftNetworkingConstants.RESET_PLAYER_SIGNAL_PACKET_ID, buf);
        }
    }
}
