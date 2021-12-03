package net.splatcraft.client.config;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import net.splatcraft.Splatcraft;
import net.splatcraft.config.Config;
import net.splatcraft.config.option.EnumOption;
import net.splatcraft.client.keybind.ChangeSquidFormKeyBehavior;

import java.io.File;

@Environment(EnvType.CLIENT)
public class ClientConfig extends Config {
    public static final ClientConfig INSTANCE = new ClientConfig(createFile("%1$s/%1$s-client".formatted(Splatcraft.MOD_ID)));
    static {
        INSTANCE.load();
    }

    public final EnumOption<ChangeSquidFormKeyBehavior> changeSquidKeyBehavior = add("change_squid_form_key_behavior", ChangeSquidFormKeyBehavior.class, ChangeSquidFormKeyBehavior.HOLD);

    private ClientConfig(File file) {
        super(file);
    }

    public <T extends Enum<T>> EnumOption<T> add(String id, Class<T> clazz, T defaultValue) {
        return this.add(new Identifier(Splatcraft.MOD_ID, id), new EnumOption<>(clazz, defaultValue));
    }
}
