package net.splatcraft.client.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.splatcraft.client.config.ClientConfig;
import net.splatcraft.component.PlayerDataComponent;
import net.splatcraft.inkcolor.InkColor;
import net.splatcraft.particle.InkSplashParticleEffect;
import net.splatcraft.particle.InkSquidSoulParticleEffect;

import static net.splatcraft.client.util.ClientUtil.getColor;
import static net.splatcraft.network.PacketIdentifiers.*;

@Environment(EnvType.CLIENT)
public class NetworkingClient {
    static {
        ClientPlayNetworking.registerGlobalReceiver(KEY_CHANGE_SQUID_FORM_RESPONSE, (client, handler, buf, responseSender) -> {
            boolean squid = buf.readBoolean();

            PlayerDataComponent data = PlayerDataComponent.get(client.player);
            data.setSquid(squid);
        });

        ClientPlayNetworking.registerGlobalReceiver(INK_SPLASH_PARTICLE_AT_POS, (client, handler, buf, responseSender) -> {
            if (!ClientConfig.INSTANCE.inkSplashParticleOnTravel.getValue()) return;

            Identifier id = buf.readIdentifier();
            double x = buf.readDouble();
            double y = buf.readDouble();
            double z = buf.readDouble();
            float scale = buf.readFloat();

            if (client.world != null) {
                InkColor inkColor = InkColor.fromId(id);
                client.world.addParticle(new InkSplashParticleEffect(getColor(inkColor), scale), x, y, z, 0.0d, 0.0d, 0.0d);
            }
        });

        ClientPlayNetworking.registerGlobalReceiver(INK_SQUID_SOUL_PARTICLE_AT_POS, (client, handler, buf, responseSender) -> {
            if (!ClientConfig.INSTANCE.inkSquidSoulParticleOnDeath.getValue()) return;

            Identifier id = buf.readIdentifier();
            double x = buf.readDouble();
            double y = buf.readDouble();
            double z = buf.readDouble();
            float scale = buf.readFloat();

            if (client.world != null) {
                InkColor inkColor = InkColor.fromId(id);
                client.world.addParticle(new InkSquidSoulParticleEffect(getColor(inkColor), scale), x, y, z, 0.0d, 0.0d, 0.0d);
            }
        });
    }

    public static void keyChangeSquidForm(boolean squid) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBoolean(squid);
        ClientPlayNetworking.send(KEY_CHANGE_SQUID_FORM, buf);

        // if configured, instantly change squid form client-side without response
        boolean instantSquidFormChange = ClientConfig.INSTANCE.instantSquidFormChangeClient.getValue();
        if (instantSquidFormChange) {
            MinecraftClient client = MinecraftClient.getInstance();
            PlayerDataComponent data = PlayerDataComponent.get(client.player);
            data.setSquid(squid);
        }
    }

    public static void clientInput(float sidewaysSpeed, float upwardSpeed, float forwardSpeed) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeDouble(sidewaysSpeed);
        buf.writeDouble(upwardSpeed);
        buf.writeDouble(forwardSpeed);
        ClientPlayNetworking.send(CLIENT_INPUT, buf);
    }
}
