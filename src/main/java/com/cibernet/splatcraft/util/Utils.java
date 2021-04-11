package com.cibernet.splatcraft.util;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class Utils {
    public static void announceMessageIfNonNull(Text text, @Nullable PlayerEntity player, @Nullable World world) {
        if (player != null) {
            player.sendMessage(text, false);
        } else if (world != null) {
            for (ServerPlayerEntity serverPlayer : PlayerLookup.world((ServerWorld) world)) {
                serverPlayer.sendMessage(text, false);
            }
        }
    }
}
