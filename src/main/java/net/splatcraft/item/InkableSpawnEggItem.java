package net.splatcraft.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3f;
import net.splatcraft.inkcolor.InkColor;
import net.splatcraft.inkcolor.Inkable;
import net.splatcraft.registry.SplatcraftRegistries;
import net.splatcraft.util.Color;

import static net.splatcraft.util.SplatcraftUtil.getDecimalColor;

public class InkableSpawnEggItem extends SpawnEggItem {
    public InkableSpawnEggItem(EntityType<? extends MobEntity> type, int primaryColor, int secondaryColor, Settings settings) {
        super(type, primaryColor, secondaryColor, settings);
    }

    // set tinted color of spawn egg to targeted block's ink color
    @Environment(EnvType.CLIENT)
    @Override
    public int getColor(int tintIndex) {
        if (tintIndex == 0) return 0xFFFFFF;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.crosshairTarget != null && client.world != null) {
            HitResult target = client.crosshairTarget;
            if (target instanceof BlockHitResult result) {
                BlockPos pos = new BlockPos(result.getPos()).offset(result.getSide(), -1);
                if (client.world.getBlockEntity(pos) instanceof Inkable inkable) return getDecimalColor(inkable.getInkColor());
            } else if (target instanceof EntityHitResult result) {
                if (result.getEntity() instanceof Inkable inkable) return getDecimalColor(inkable.getInkColor());
            }
        }

        if (client.player != null) {
            int r = client.player.age / 25 + client.player.getId();
            int colors = SplatcraftRegistries.INK_COLOR.size();
            int a = r % colors;
            int b = (r + 1) % colors;

            InkColor ia = SplatcraftRegistries.INK_COLOR.get(a);
            InkColor ib = SplatcraftRegistries.INK_COLOR.get(b);
            if (ia != null && ib != null) {
                Vec3f va = ia.getVectorColor();
                Vec3f vb = ib.getVectorColor();

                float rand = ((float) (client.player.age % 25) + 1) / 25.0f;
                float red = va.getX() * (1.0f - rand) + vb.getX() * rand;
                float green = va.getY() * (1.0f - rand) + vb.getY() * rand;
                float blue = va.getZ() * (1.0f - rand) + vb.getZ() * rand;

                return Color.ofRGB(red, green, blue).getColor();
            }
        }

        return 0x474F52;
    }
}
