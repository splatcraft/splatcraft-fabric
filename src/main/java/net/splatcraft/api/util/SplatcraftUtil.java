package net.splatcraft.api.util;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Util;

import java.util.function.Function;

public final class SplatcraftUtil {
    private static final FabricLoader LOADER = FabricLoader.getInstance();

    private SplatcraftUtil() {}

    /* Frame-Tick Conversion */

    public static final int SPLATOON_FPS = 60;
    private static final Function<Integer, Integer> FRAMES_TO_TICK = Util.memoize(SplatcraftUtil::rawFrameToTick);

    public static Integer rawFrameToTick(Integer frames) {
        return (int) ((frames / (float) SPLATOON_FPS) * 20);
    }

    public static int frameToTick(int frames) {
        return FRAMES_TO_TICK.apply(frames);
    }

    /* Health Scaling */

    public static final float SPLATOON_MAX_HEALTH = 100;
    private static final Function<Number, Float> SCALE_GAME_HEALTH = Util.memoize(SplatcraftUtil::rawScaleGameHealth);

    public static Float rawScaleGameHealth(Number health) {
        return (health.floatValue() / SPLATOON_MAX_HEALTH) * 20;
    }

    public static float scaleGameHealth(Number health) {
        return SCALE_GAME_HEALTH.apply(health);
    }

    /* Mod Loaded Checks */

    private static final Function<String, Boolean> MOD_LOADED = Util.memoize(LOADER::isModLoaded);

    public static boolean isModLoaded(String modId) {
        return MOD_LOADED.apply(modId);
    }
}
