package net.splatcraft.api.client.keybind;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.option.KeyBinding;
import net.splatcraft.api.Splatcraft;
import org.lwjgl.glfw.GLFW;

import static net.splatcraft.impl.client.keybind.SplatcraftKeyBindingsClientImpl.*;

@Environment(EnvType.CLIENT)
public interface SplatcraftKeyBindings {
    String KEY_CATEGORY = "key.categories.%s".formatted(Splatcraft.MOD_ID);

    ChangeSquidFormKeyBinding CHANGE_SQUID_FORM = register("change_squid_form", GLFW.GLFW_KEY_Z, ChangeSquidFormKeyBinding::new);
    KeyBinding                OPEN_CONFIG       = register("open_config", GLFW.GLFW_KEY_UNKNOWN);
}
