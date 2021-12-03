package net.splatcraft.network;

import net.minecraft.entity.player.PlayerEntity;
import net.splatcraft.component.PlayerDataComponent;

public class CommonNetworking {
    public static void setSquidForm(PlayerEntity player, boolean squid) {
        PlayerDataComponent data = PlayerDataComponent.get(player);
        data.setSquid(squid);
        if (squid) player.setSprinting(false);
    }
}
