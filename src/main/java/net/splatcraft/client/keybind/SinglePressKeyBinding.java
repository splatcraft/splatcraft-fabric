package net.splatcraft.client.keybind;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

@Environment(EnvType.CLIENT)
public class SinglePressKeyBinding extends KeyBinding {
    private boolean togglePressed = false;

    public SinglePressKeyBinding(String translationKey, InputUtil.Type type, int code, String category) {
        super(translationKey, type, code, category);
    }

    public boolean wasToggled() {
        if (this.isPressed()) {
            if (!this.togglePressed) return this.togglePressed = true;
        } else return this.togglePressed = false;
        return false;
    }
}
