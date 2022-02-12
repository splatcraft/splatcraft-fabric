package net.splatcraft.util;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Util;

import java.util.function.Function;

public class SplatcraftUtil {
    private static final FabricLoader LOADER = FabricLoader.getInstance();

    public static final int SPLATOON_FPS = 60;
    private static final Function<Integer, Integer> FRAMES_TO_TICK = Util.memoize(SplatcraftUtil::rawFrameToTick);

    public static Integer rawFrameToTick(Integer frames) {
        return (int) ((frames / (float) SPLATOON_FPS) * 20);
    }

    public static int frameToTick(int frames) {
        return FRAMES_TO_TICK.apply(frames);
    }

    private static final Function<String, Boolean> MOD_LOADED = Util.memoize(LOADER::isModLoaded);

    public static boolean isModLoaded(String modId) {
        return MOD_LOADED.apply(modId);
    }
}
