package net.splatcraft.api.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;
import net.splatcraft.api.inkcolor.InkColor;
import net.splatcraft.api.inkcolor.InkColors;
import net.splatcraft.api.registry.SplatcraftRegistries;
import net.splatcraft.api.util.Color;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

import static net.splatcraft.api.client.util.ClientUtil.*;

public class InkableSpawnEggItem extends SpawnEggItem {
    public InkableSpawnEggItem(EntityType<? extends MobEntity> type, int primaryColor, int secondaryColor, Settings settings) {
        super(type, primaryColor, secondaryColor, settings);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public int getColor(int tintIndex) {
        if (tintIndex == 0) return 0xFFFFFF;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            InkColor inkColor = getTargetedInkColor();
            if (inkColor != null && client.player.isHolding(this)) return getDecimalColor(inkColor);

            // else randomise color, similar to jeb_ sheep
            int rate = 6;
            int r = client.player.age / rate + client.player.getId();
            int colors = SplatcraftRegistries.INK_COLOR.size();
            int a = r % colors;
            int b = (r + 1) % colors;

            InkColor ia = SplatcraftRegistries.INK_COLOR.get(a);
            InkColor ib = SplatcraftRegistries.INK_COLOR.get(b);
            if (ia != null && ib != null) {
                Vec3f va = ia.getVectorColor();
                Vec3f vb = ib.getVectorColor();

                float rand = ((float) (client.player.age % rate) + 1) / rate;
                float red = va.getX() * (1.0f - rand) + vb.getX() * rand;
                float green = va.getY() * (1.0f - rand) + vb.getY() * rand;
                float blue = va.getZ() * (1.0f - rand) + vb.getZ() * rand;

                return Color.ofRGB(red, green, blue).getColor();
            }
        }

        return 0x474F52; // will never return here, probably...
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext ctx) {
        super.appendTooltip(stack, world, tooltip, ctx);

        if (ctx.isAdvanced()) {
            InkColor inkColor = Optional.ofNullable(getTargetedInkColor()).orElse(InkColors.getDefault());
            tooltip.add(new LiteralText(inkColor.toString()).formatted(Formatting.DARK_GRAY));
        }
    }
}
