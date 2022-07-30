package net.splatcraft.test.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.splatcraft.api.Splatcraft;
import net.splatcraft.test.client.command.InkColorListClientCommand;

@Environment(EnvType.CLIENT)
public final class SplatcraftTestClientImpl implements Splatcraft, ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, access) -> {
            InkColorListClientCommand.register(dispatcher);
        });
    }
}
