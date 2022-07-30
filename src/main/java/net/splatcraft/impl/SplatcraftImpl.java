package net.splatcraft.impl;

import com.google.common.reflect.Reflection;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.moddingplayground.frame.api.util.InitializationLogger;
import net.splatcraft.api.Splatcraft;
import net.splatcraft.api.command.InkColorCommand;
import net.splatcraft.api.inkcolor.InkColors;
import net.splatcraft.api.registry.SplatcraftRegistry;

public final class SplatcraftImpl implements Splatcraft, ModInitializer {
    private final InitializationLogger initializer = new InitializationLogger(LOGGER, MOD_NAME);

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public void onInitialize() {
        this.initializer.start();

        Reflection.initialize(
            SplatcraftRegistry.class,
            InkColors.class
        );

        CommandRegistrationCallback.EVENT.register((dispatcher, access, environment) -> {
            InkColorCommand.register(dispatcher);
        });

        this.initializer.finish();
    }
}
