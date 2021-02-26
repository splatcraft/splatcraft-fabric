package com.cibernet.splatcraft.client.init;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.component.PlayerDataComponent;
import com.cibernet.splatcraft.network.SplatcraftNetworkingConstants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class SplatcraftKeyBindings {
    private static final KeyBinding TOGGLE_SQUID = register("toggle_squid", GLFW.GLFW_KEY_Z);

    public SplatcraftKeyBindings() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (TOGGLE_SQUID.wasPressed()) {
                ClientPlayNetworking.send(SplatcraftNetworkingConstants.TOGGLE_SQUID_PACKET_ID, PacketByteBufs.empty());
                PlayerDataComponent.toggleSquidForm(client.player);
            }
        });
    }

    private static KeyBinding register(String id, int code) {
        return KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key." + Splatcraft.MOD_ID + "." + id,
                InputUtil.Type.KEYSYM,
                code,
                "key.categories." + Splatcraft.MOD_ID
            )
        );
    }
}
