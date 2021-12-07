package net.splatcraft.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.splatcraft.client.config.SplatcraftConfigScreenFactory;

@Environment(EnvType.CLIENT)
public class SplatcraftModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return new SplatcraftConfigScreenFactory();
    }
}
