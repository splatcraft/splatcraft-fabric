package net.splatcraft.impl.network;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.splatcraft.api.component.PlayerDataComponent;
import net.splatcraft.impl.entity.PackedInput;
import net.splatcraft.impl.entity.access.InputPlayerEntityAccess;

import static net.splatcraft.impl.network.PacketIdentifiers.*;

public final class NetworkingCommon implements ModInitializer {
    @Override
    public void onInitialize() {
        ServerPlayNetworking.registerGlobalReceiver(KEY_CHANGE_SQUID_FORM, (server, player, handler, buf, sender) -> {
            boolean squid = buf.readBoolean();

            PlayerDataComponent data = PlayerDataComponent.get(player);
            if (data.setSquid(squid)) {
                PacketByteBuf returnBuf = PacketByteBufs.create();
                returnBuf.writeBoolean(squid);
                ServerPlayNetworking.send(player, KEY_CHANGE_SQUID_FORM_RESPONSE, returnBuf);
            }
        });

        ServerPlayNetworking.registerGlobalReceiver(CLIENT_INPUT, (server, player, handler, buf, sender) -> {
            ((InputPlayerEntityAccess) player).setPackedInput(PackedInput.fromPacket(buf));
        });
    }

    public static void s2cInit(ServerPlayerEntity player) {
        ServerPlayNetworking.send(player, S2C_INIT, PacketByteBufs.empty());
    }
}
