package net.splatcraft.client.keybind;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Style;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.splatcraft.Splatcraft;
import net.splatcraft.client.config.SplatcraftConfigScreenFactory;
import net.splatcraft.entity.access.InkEntityAccess;
import org.lwjgl.glfw.GLFW;

import static net.splatcraft.client.network.NetworkingClient.*;
import static net.splatcraft.util.SplatcraftConstants.*;

@Environment(EnvType.CLIENT)
public class SplatcraftKeyBindings {
    public static final String KEY_CATEGORY = "key.categories.%s".formatted(Splatcraft.MOD_ID);

    public static final ChangeSquidFormKeyBinding CHANGE_SQUID_FORM = register("change_squid_form", GLFW.GLFW_KEY_Z, ChangeSquidFormKeyBinding::new);
    public static final KeyBinding OPEN_CONFIG = register("open_config", GLFW.GLFW_KEY_UNKNOWN);

    static {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.world != null) {
                ClientPlayerEntity player = client.player;
                if (player != null) {
                    // config menu
                    if (OPEN_CONFIG.wasPressed()) {
                        client.setScreen(new SplatcraftConfigScreenFactory(client.currentScreen).create());
                        return;
                    }

                    if (isSplatcraftPresentOnServer()) {
                        // squid form
                        boolean squid = ((InkEntityAccess) player).isInSquidForm();
                        boolean nowSquid = CHANGE_SQUID_FORM.getSquidForm(squid);
                        if (squid != nowSquid) keyChangeSquidForm(nowSquid);
                    } else {
                        if (CHANGE_SQUID_FORM.wasPressed()) {
                            SystemToast.add(
                                client.getToastManager(), SystemToast.Type.TUTORIAL_HINT,
                                new TranslatableText(T_NOT_INSTALLED_ON_SERVER_1),
                                new TranslatableText(T_NOT_INSTALLED_ON_SERVER_2).setStyle(Style.EMPTY.withColor(0xFCFC00))
                            );
                        }
                    }
                }
            }
        });
    }

    public static <K extends KeyBinding> K register(String id, int code, KeyBindingFactory<K> factory) {
        Identifier identifier = new Identifier(Splatcraft.MOD_ID, id);
        String dottedIdentifier = "%s.%s".formatted(identifier.getNamespace(), identifier.getPath());
        K key = factory.create("key.%s".formatted(dottedIdentifier), InputUtil.Type.KEYSYM, code, KEY_CATEGORY);
        KeyBindingHelper.registerKeyBinding(key);
        return key;
    }

    @SuppressWarnings("unchecked")
    public static <K extends KeyBinding> K register(String id, int code) {
        return (K) register(id, code, KeyBinding::new);
    }

    @FunctionalInterface
    public interface KeyBindingFactory<K extends KeyBinding> {
        K create(String translationKey, InputUtil.Type type, int code, String category);
    }
}
