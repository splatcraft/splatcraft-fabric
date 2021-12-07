package net.splatcraft.client.keybind;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public class ToggleKeyBinding extends KeyBinding {
    private boolean togglePressed = false;

    public ToggleKeyBinding(String translationKey, InputUtil.Type type, int code, String category) {
        super(translationKey, type, code, category);
    }

    public boolean wasToggled() {
        if (this.isPressed()) {
            if (!this.togglePressed) {
                this.togglePressed = true;
                return true;
            }
        } else {
            this.togglePressed = false;
        }

        return false;
    }
}
