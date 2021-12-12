package net.splatcraft.client.config;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.splatcraft.Splatcraft;
import net.splatcraft.client.keybind.ChangeSquidFormKeyBehavior;
import net.splatcraft.config.Config;
import net.splatcraft.config.option.BooleanOption;
import net.splatcraft.config.option.ColorOption;
import net.splatcraft.config.option.EnumOption;

import java.io.File;

@Environment(EnvType.CLIENT)
public class ClientConfig extends Config {
    public static final ClientConfig INSTANCE = new ClientConfig(createFile("%1$s/%1$s-client".formatted(Splatcraft.MOD_ID)));
    static {
        INSTANCE.load();
    }

    public final EnumOption<ChangeSquidFormKeyBehavior> changeSquidKeyBehavior = add(
        "change_squid_form_key_behavior",
        EnumOption.of(ChangeSquidFormKeyBehavior.class, ChangeSquidFormKeyBehavior.HOLD)
    );
    public final BooleanOption instantSquidFormChangeClient = add(
        "instant_squid_form_change_client",
        BooleanOption.of(false)
    );

    public final BooleanOption colorLock = add("color_lock", BooleanOption.of(false));
    public final ColorOption colorLockFriendly = add("color_lock_friendly", ColorOption.of(0x2E0CB5));
    public final ColorOption colorLockHostile = add("color_lock_hostile", ColorOption.of(0xF86300));

    private ClientConfig(File file) {
        super(file);
    }

    @Override
    public void save() {
        super.save();
        MinecraftClient.getInstance().worldRenderer.reload();
    }
}
