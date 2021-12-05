package net.splatcraft.mixin.client;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractClientPlayerEntity.class)
public interface AbstractClientPlayerEntityInvoker {
    @Invoker("getPlayerListEntry")
    PlayerListEntry invoke_getPlayerListEntry();
}
