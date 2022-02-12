package net.splatcraft.client.config;

import com.google.common.collect.HashBiMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import net.moddingplayground.frame.api.config.Config;
import net.moddingplayground.frame.api.config.option.BooleanOption;
import net.moddingplayground.frame.api.config.option.Option;
import net.splatcraft.Splatcraft;

import java.io.File;

import static net.splatcraft.util.SplatcraftUtil.*;

@Environment(EnvType.CLIENT)
public class ClientCompatConfig extends Config {
    public static final ClientCompatConfig INSTANCE = new ClientCompatConfig(createFile("%1$s/%1$s-client-compat".formatted(Splatcraft.MOD_ID))).load();

    public final BooleanOption sodium_inkBiomeBlendFix = this.add(
        new Identifier("sodium", "ink_biome_blend_fix"),
        BooleanOption.of(true)
    );

    private ClientCompatConfig(File file) {
        super(file);
    }

    @Override
    public HashBiMap<Identifier, Option<?>> getDisplayedOptions() {
        HashBiMap<Identifier, Option<?>> map = HashBiMap.create();
        if (isModLoaded("sodium")) map.put(this.map.inverse().get(this.sodium_inkBiomeBlendFix), this.sodium_inkBiomeBlendFix);
        return map;
    }
}
