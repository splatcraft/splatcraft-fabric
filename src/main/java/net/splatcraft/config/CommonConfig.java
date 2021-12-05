package net.splatcraft.config;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.splatcraft.Splatcraft;

import java.io.File;

public class CommonConfig extends Config {
    public static final CommonConfig INSTANCE = new CommonConfig(createFile("%1$s/%1$s-common".formatted(Splatcraft.MOD_ID)));
    static {
        INSTANCE.load();
    }

    private CommonConfig(File file) {
        super(file);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean canDisplayInMenu() {
        return super.canDisplayInMenu() && MinecraftClient.getInstance().isIntegratedServerRunning();
    }
}
