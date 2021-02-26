package com.cibernet.splatcraft.init;

import com.cibernet.splatcraft.Splatcraft;
import net.minecraft.stat.StatFormatter;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class SplatcraftStats {
    public static final Identifier BLOCKS_INKED = register("blocks_inked", StatFormatter.DEFAULT);
    public static final Identifier SQUID_TIME = register("squid_time", StatFormatter.TIME);

    public SplatcraftStats() {}

    private static Identifier register(String id, StatFormatter statFormatter) {
        Identifier identifier = new Identifier(Splatcraft.MOD_ID, id);

        Registry.register(Registry.CUSTOM_STAT, id, identifier);
        Stats.CUSTOM.getOrCreateStat(identifier, statFormatter);

        return identifier;
    }
}
