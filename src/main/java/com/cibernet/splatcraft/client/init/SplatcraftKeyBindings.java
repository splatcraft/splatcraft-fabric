package com.cibernet.splatcraft.client.init;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.client.config.SplatcraftConfig;
import com.cibernet.splatcraft.client.config.SplatcraftConfigManager;
import com.cibernet.splatcraft.client.gui.screen.SignalSelectionScreen;
import com.cibernet.splatcraft.client.network.SplatcraftClientNetworking;
import com.cibernet.splatcraft.component.LazyPlayerDataComponent;
import com.cibernet.splatcraft.entity.player.signal.AnimatablePlayerEntity;
import com.cibernet.splatcraft.handler.PlayerHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class SplatcraftKeyBindings {
    public static final KeyBinding CHANGE_SQUID_FORM = register("change_squid_form", GLFW.GLFW_KEY_Z);
    public static final KeyBinding OPEN_SIGNAL_SELECTION_SCREEN = register("open_signal_selection_screen", GLFW.GLFW_KEY_GRAVE_ACCENT);
    public static final KeyBinding OPEN_CONFIG_MENU = register("open_config_menu", GLFW.GLFW_KEY_UNKNOWN);

    public SplatcraftKeyBindings() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.world != null && client.player != null) {
                boolean wasSquid = LazyPlayerDataComponent.isSquid(client.player);
                boolean isSquid = wasSquid;

                switch (SplatcraftConfig.ACCESSIBILITY.squidFormKeyBehavior.value) {
                    case HOLD:
                        isSquid = CHANGE_SQUID_FORM.isPressed();
                        break;
                    case TOGGLE:
                        if (CHANGE_SQUID_FORM.wasPressed()) {
                            isSquid = !isSquid;
                        }
                        break;
                }

                if (isSquid) {
                    isSquid = PlayerHandler.canEnterSquidForm(client.player);
                }

                if (wasSquid != isSquid) {
                    SplatcraftClientNetworking.setAndSendSquidForm(client.player, isSquid);
                }
            }

            if (OPEN_CONFIG_MENU.isPressed()) {
                client.openScreen(SplatcraftConfigManager.createScreen(client.currentScreen));
            }
            if (OPEN_SIGNAL_SELECTION_SCREEN.isPressed() && client.player != null && !PlayerHandler.shouldCancelInteraction(client.player) && AnimatablePlayerEntity.canStart(client.player)) {
                client.openScreen(new SignalSelectionScreen());
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
