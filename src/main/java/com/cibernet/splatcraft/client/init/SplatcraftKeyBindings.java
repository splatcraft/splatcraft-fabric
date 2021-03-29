package com.cibernet.splatcraft.client.init;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.client.config.SplatcraftConfig;
import com.cibernet.splatcraft.client.config.SplatcraftConfigManager;
import com.cibernet.splatcraft.client.network.SplatcraftClientNetworking;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class SplatcraftKeyBindings {
    private static final KeyBinding CHANGE_SQUID_FORM = register("change_squid_form", GLFW.GLFW_KEY_Z);
    private static final KeyBinding OPEN_CONFIG_MENU = register("open_config_menu", GLFW.GLFW_KEY_UNKNOWN);

    public SplatcraftKeyBindings() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.world != null && SplatcraftConfig.ACCESSIBILITY.squidFormKeyBehavior.value.keyIsPressed(CHANGE_SQUID_FORM.wasPressed(), CHANGE_SQUID_FORM.isPressed())) {
                SplatcraftClientNetworking.toggleSquidForm(client.player);
            }

            if (OPEN_CONFIG_MENU.wasPressed()) {
                client.openScreen(SplatcraftConfigManager.createScreen(client.currentScreen));
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
