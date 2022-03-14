package net.splatcraft.impl.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.splatcraft.impl.client.config.SplatcraftConfigScreenFactory;

@Environment(EnvType.CLIENT)
public final class SplatcraftModMenuImpl implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return p -> new SplatcraftConfigScreenFactory(p).create();
    }
}
