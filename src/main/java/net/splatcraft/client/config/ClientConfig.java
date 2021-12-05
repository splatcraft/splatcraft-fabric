package net.splatcraft.client.config;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.splatcraft.Splatcraft;
import net.splatcraft.client.keybind.ChangeSquidFormKeyBehavior;
import net.splatcraft.config.Config;
import net.splatcraft.config.option.EnumOption;
import net.splatcraft.config.option.IntOption;

import java.io.File;

@Environment(EnvType.CLIENT)
public class ClientConfig extends Config {
    public static final ClientConfig INSTANCE = new ClientConfig(createFile("%1$s/%1$s-client".formatted(Splatcraft.MOD_ID)));
    static {
        INSTANCE.load();
    }

    public final EnumOption<ChangeSquidFormKeyBehavior> changeSquidKeyBehavior = add("change_squid_form_key_behavior", new EnumOption<>(ChangeSquidFormKeyBehavior.class, ChangeSquidFormKeyBehavior.HOLD));
    public final IntOption latencyForInstantSquidFormChange = add("instant_squid_form_change_latency", new IntOption(150));

    private ClientConfig(File file) {
        super(file);
    }
}
