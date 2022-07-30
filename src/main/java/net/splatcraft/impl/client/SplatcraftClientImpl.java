package net.splatcraft.impl.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.splatcraft.api.Splatcraft;

@Environment(EnvType.CLIENT)
public final class SplatcraftClientImpl implements Splatcraft, ClientModInitializer {
    @Override
    public void onInitializeClient() {
    }
}
