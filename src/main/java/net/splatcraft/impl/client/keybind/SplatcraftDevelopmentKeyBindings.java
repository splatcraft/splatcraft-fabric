package net.splatcraft.impl.client.keybind;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;
import net.splatcraft.api.client.keybind.SinglePressKeyBinding;
import net.splatcraft.impl.client.config.ClientCompatConfig;
import net.splatcraft.impl.client.config.ClientConfig;

import static net.splatcraft.api.util.SplatcraftConstants.*;
import static net.splatcraft.impl.client.keybind.SplatcraftKeyBindingsClientImpl.*;

@Environment(EnvType.CLIENT)
public class SplatcraftDevelopmentKeyBindings {
    public static final SinglePressKeyBinding RELOAD_CONFIG_CLIENT        = register("reload_config_client", SinglePressKeyBinding::new);
    public static final SinglePressKeyBinding RELOAD_CONFIG_CLIENT_COMPAT = register("reload_config_client_compat", SinglePressKeyBinding::new);

    static {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null) {
                if (RELOAD_CONFIG_CLIENT.wasToggled()) {
                    ClientConfig.INSTANCE.load();
                    client.player.sendMessage(reloadConfigText(RELOAD_CONFIG_CLIENT), true);
                }
                if (RELOAD_CONFIG_CLIENT_COMPAT.wasToggled()) {
                    ClientCompatConfig.INSTANCE.load();
                    client.player.sendMessage(reloadConfigText(RELOAD_CONFIG_CLIENT_COMPAT), true);
                }
            }
        });
    }

    private static Text reloadConfigText(KeyBinding keyBinding) {
        String key = keyBinding.getTranslationKey();
        return Text.translatable(T_RELOADED_CONFIG, key.substring(key.lastIndexOf("_") + 1));
    }
}
