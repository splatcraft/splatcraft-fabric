package net.splatcraft.test;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.splatcraft.api.Splatcraft;
import net.splatcraft.test.command.InkColorListCommand;

public final class SplatcraftTestImpl implements Splatcraft, ModInitializer {
    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, access, environment) -> {
            InkColorListCommand.register(dispatcher);
        });
    }
}
