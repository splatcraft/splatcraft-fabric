package net.splatcraft.util;

import net.minecraft.util.Util;

import java.util.function.Function;

public class SplatcraftUtil {
    public static final int SPLATOON_FPS = 60;
    private static final Function<Integer, Integer> FRAMES_TO_TICK = Util.memoize(SplatcraftUtil::rawFrameToTick);

    public static int frameToTick(int frames) {
        return FRAMES_TO_TICK.apply(frames);
    }

    public static Integer rawFrameToTick(Integer frames) {
        return (int) ((frames / (float) SPLATOON_FPS) * 20);
    }
}
