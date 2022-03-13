package net.splatcraft.impl.client.model.quad.blender;

import me.jellysquid.mods.sodium.client.model.quad.ModelQuadView;
import me.jellysquid.mods.sodium.client.model.quad.blender.ColorSampler;
import me.jellysquid.mods.sodium.client.model.quad.blender.LinearColorBlender;
import me.jellysquid.mods.sodium.client.util.color.ColorARGB;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import net.splatcraft.api.inkcolor.InkColor;
import net.splatcraft.api.inkcolor.Inkable;

import java.util.Arrays;

import static net.splatcraft.api.client.util.ClientUtil.*;

@Environment(EnvType.CLIENT)
public class InkColorBlender extends LinearColorBlender {
    protected final int[] inkCachedRet = new int[4];

    @Override
    public <T> int[] getColors(BlockRenderView world, BlockPos origin, ModelQuadView quad, ColorSampler<T> sampler, T state) {
        if (world.getBlockEntity(origin) instanceof Inkable inkable) {
            InkColor inkColor = inkable.getInkColor();
            int color = getDecimalColor(inkColor);
            Arrays.fill(this.inkCachedRet, ColorARGB.toABGR(color));
            return this.inkCachedRet;
        }
        return super.getColors(world, origin, quad, sampler, state);
    }
}
