package net.splatcraft.network;

import net.minecraft.entity.player.PlayerEntity;
import net.splatcraft.component.PlayerDataComponent;

public class NetworkingCommon {
    public static void setSquidForm(PlayerEntity player, boolean squid) {
        PlayerDataComponent data = PlayerDataComponent.get(player);
        data.setSquid(squid);
        player.calculateDimensions();
        if (squid) player.setSprinting(false);
    }
}
