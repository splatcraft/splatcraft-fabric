package com.cibernet.splatcraft;

import com.cibernet.splatcraft.client.config.SplatcraftConfigManager;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class SplatcraftModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return SplatcraftConfigManager::createScreen;
    }
}
