package net.splatcraft.client.keybind;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.splatcraft.Splatcraft;
import net.splatcraft.client.config.ClientConfig;
import net.splatcraft.config.CommonConfig;
import org.lwjgl.glfw.GLFW;

import static net.splatcraft.util.SplatcraftConstants.*;

@Environment(EnvType.CLIENT)
public class SplatcraftDevelopmentKeyBindings {
    private static final String KEY_CATEGORY = "key.categories.%s-dev".formatted(Splatcraft.MOD_ID);

    public static final KeyBinding RELOAD_CONFIG_COMMON = register("reload_config_common", GLFW.GLFW_KEY_UNKNOWN);
    public static final KeyBinding RELOAD_CONFIG_CLIENT = register("reload_config_client", GLFW.GLFW_KEY_UNKNOWN);

    static {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null) {
                if (RELOAD_CONFIG_COMMON.wasPressed() && client.isIntegratedServerRunning()) {
                    CommonConfig.INSTANCE.load();
                    client.player.sendMessage(reloadConfigText(RELOAD_CONFIG_COMMON), true);
                }
                if (RELOAD_CONFIG_CLIENT.wasPressed()) {
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

    private static KeyBinding register(String id, int code) {
        Identifier identifier = new Identifier(Splatcraft.MOD_ID, id);
        String dottedIdentifier = "%s.%s".formatted(identifier.getNamespace(), identifier.getPath());
        return KeyBindingHelper.registerKeyBinding(new KeyBinding("key.%s".formatted(dottedIdentifier), InputUtil.Type.KEYSYM, code, KEY_CATEGORY));
    }
}
