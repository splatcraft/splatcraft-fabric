package net.splatcraft.network;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Vec3d;
import net.splatcraft.component.PlayerDataComponent;
import net.splatcraft.entity.InputPlayerEntityAccess;

import static net.splatcraft.network.PacketIdentifiers.*;

public class NetworkingCommon {
    static {
        ServerPlayNetworking.registerGlobalReceiver(KEY_CHANGE_SQUID_FORM, (server, player, handler, buf, responseSender) -> {
            boolean squid = buf.readBoolean();

            PlayerDataComponent data = PlayerDataComponent.get(player);
            if (data.setSquid(squid)) {
                PacketByteBuf returnBuf = PacketByteBufs.create();
                returnBuf.writeBoolean(squid);
                ServerPlayNetworking.send(player, KEY_CHANGE_SQUID_FORM_RESPONSE, returnBuf);
            }
        });

        ServerPlayNetworking.registerGlobalReceiver(CLIENT_INPUT, (server, player, handler, buf, responseSender) -> {
            double sidewaysSpeed = buf.readDouble();
            double upwardSpeed = buf.readDouble();
            double forwardSpeed = buf.readDouble();
            ((InputPlayerEntityAccess) player).setInputSpeeds(new Vec3d(sidewaysSpeed, upwardSpeed, forwardSpeed));
        });
    }
}
