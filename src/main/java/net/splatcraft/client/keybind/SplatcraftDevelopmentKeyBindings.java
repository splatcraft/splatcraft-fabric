package net.splatcraft.client.keybind;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.splatcraft.Splatcraft;
import net.splatcraft.client.config.ClientConfig;
import net.splatcraft.config.CommonConfig;
import org.lwjgl.glfw.GLFW;

import static net.splatcraft.client.keybind.SplatcraftKeyBindings.*;
import static net.splatcraft.util.SplatcraftConstants.*;

@Environment(EnvType.CLIENT)
public class SplatcraftDevelopmentKeyBindings {
    private static final String KEY_CATEGORY = "key.categories.%s-dev".formatted(Splatcraft.MOD_ID);

    public static final SinglePressKeyBinding RELOAD_CONFIG_COMMON = register("reload_config_common", GLFW.GLFW_KEY_UNKNOWN, SinglePressKeyBinding::new);
    public static final SinglePressKeyBinding RELOAD_CONFIG_CLIENT = register("reload_config_client", GLFW.GLFW_KEY_UNKNOWN, SinglePressKeyBinding::new);

    static {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null) {
                if (RELOAD_CONFIG_COMMON.wasToggled()) {
                    if (CommonConfig.INSTANCE.canDisplayInMenu()) {
                        CommonConfig.INSTANCE.load();
                        client.player.sendMessage(reloadConfigText(RELOAD_CONFIG_COMMON), true);
                    } else {
                        client.player.sendMessage(new TranslatableText(T_RELOADED_CONFIG_FAILED).formatted(Formatting.RED), true);
                    }
                }
                if (RELOAD_CONFIG_CLIENT.wasToggled()) {
                    ClientConfig.INSTANCE.load();
                    client.player.sendMessage(reloadConfigText(RELOAD_CONFIG_CLIENT), true);
                }
            }
        });
    }

    private static TranslatableText reloadConfigText(KeyBinding keyBinding) {
        String key = keyBinding.getTranslationKey();
        return new TranslatableText(T_RELOADED_CONFIG, key.substring(key.lastIndexOf("_") + 1));
    }
}
