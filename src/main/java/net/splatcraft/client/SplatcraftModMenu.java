package net.splatcraft.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.splatcraft.client.config.SplatcraftConfigScreenFactory;

@Environment(EnvType.CLIENT)
public class SplatcraftModMenu implements ModMenuApi {
    private static SplatcraftModMenu instance = null;

    public SplatcraftModMenu() {
        instance = this;
    }

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return p -> new SplatcraftConfigScreenFactory(p).create();
    }

    public static SplatcraftModMenu getInstance() {
        return instance;
    }
}
