package net.splatcraft.config;

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
}
