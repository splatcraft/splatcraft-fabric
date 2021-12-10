package net.splatcraft.client.config;

import com.google.common.collect.HashBiMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import net.splatcraft.Splatcraft;
import net.splatcraft.config.Config;
import net.splatcraft.config.option.BooleanOption;
import net.splatcraft.config.option.Option;
import net.splatcraft.util.ModLoaded;

import java.io.File;

@Environment(EnvType.CLIENT)
public class ClientCompatConfig extends Config {
    public static final ClientCompatConfig INSTANCE = new ClientCompatConfig(createFile("%1$s/%1$s-client-compat".formatted(Splatcraft.MOD_ID)));
    static {
        INSTANCE.load();
    }

    public final BooleanOption sodium_inkBiomeBlendFix = add(
        new Identifier("sodium", "ink_biome_blend_fix"),
        BooleanOption.of(true)
    );

    private ClientCompatConfig(File file) {
        super(file);
    }

    @Override
    public HashBiMap<Identifier, Option<?>> getDisplayedConfigListEntries() {
        HashBiMap<Identifier, Option<?>> map = HashBiMap.create();

        if (ModLoaded.SODIUM) {
            map.put(this.map.inverse().get(this.sodium_inkBiomeBlendFix), this.sodium_inkBiomeBlendFix);
        }

        return map;
    }
}
