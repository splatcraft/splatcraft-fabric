package net.splatcraft.client.keybind;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.splatcraft.client.config.ClientConfig;
import net.splatcraft.entity.access.PlayerEntityAccess;

@Environment(EnvType.CLIENT)
public class ChangeSquidFormKeyBinding extends SinglePressKeyBinding {
    public ChangeSquidFormKeyBinding(String translationKey, InputUtil.Type type, int code, String category) {
        super(translationKey, type, code, category);
    }

    public boolean getSquidForm(boolean squid) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || !((PlayerEntityAccess) client.player).canEnterSquidForm()) return false;
        return switch (ClientConfig.INSTANCE.changeSquidKeyBehavior.getValue()) {
            case TOGGLE -> this.wasToggled() != squid;
            case HOLD -> this.isPressed();
        };
    }
}
