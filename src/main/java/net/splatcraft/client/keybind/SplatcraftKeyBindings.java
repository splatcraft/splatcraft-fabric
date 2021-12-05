package net.splatcraft.client.keybind;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import net.splatcraft.Splatcraft;
import net.splatcraft.client.config.ClientConfig;
import net.splatcraft.client.network.NetworkingClient;
import net.splatcraft.component.PlayerDataComponent;
import net.splatcraft.util.SplatcraftUtil;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class SplatcraftKeyBindings {
    public static final String KEY_CATEGORY = "key.categories.%s".formatted(Splatcraft.MOD_ID);

    public static final KeyBinding CHANGE_SQUID_FORM = register("change_squid_form", GLFW.GLFW_KEY_Z);

    private static boolean _changeSquidForm_togglePressed = false;

    static {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.world != null) {
                ClientPlayerEntity player = client.player;
                if (player != null) {
                    PlayerDataComponent data = PlayerDataComponent.get(player);

                    // squid form
                    boolean wasSquid = data.isSquid();
                    boolean squid = wasSquid;
                    switch (ClientConfig.INSTANCE.changeSquidKeyBehavior.getValue()) {
                        case TOGGLE -> {
                            if (CHANGE_SQUID_FORM.isPressed() && !_changeSquidForm_togglePressed) {
                                squid = !wasSquid;
                                _changeSquidForm_togglePressed = true;
                            }
                        }
                        case HOLD -> squid = CHANGE_SQUID_FORM.isPressed();
                    }

                    if (squid) squid = SplatcraftUtil.canSquid(player);
                    if (wasSquid != squid) NetworkingClient.sendKeyChangeSquidForm(squid);
                }

                if (!CHANGE_SQUID_FORM.isPressed()) _changeSquidForm_togglePressed = false;
            }
        });
    }

    private static KeyBinding register(String id, int code) {
        Identifier identifier = new Identifier(Splatcraft.MOD_ID, id);
        String dottedIdentifier = "%s.%s".formatted(identifier.getNamespace(), identifier.getPath());
        return KeyBindingHelper.registerKeyBinding(new KeyBinding("key.%s".formatted(dottedIdentifier), InputUtil.Type.KEYSYM, code, KEY_CATEGORY));
    }
}
