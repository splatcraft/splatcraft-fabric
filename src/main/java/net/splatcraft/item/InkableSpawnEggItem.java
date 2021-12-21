package net.splatcraft.item;

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
import net.splatcraft.inkcolor.InkColor;
import net.splatcraft.inkcolor.InkColors;
import net.splatcraft.registry.SplatcraftRegistries;
import net.splatcraft.util.Color;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

import static net.splatcraft.client.util.ClientUtil.getDecimalColor;
import static net.splatcraft.client.util.ClientUtil.getTargetedBlockInkColor;

public class InkableSpawnEggItem extends SpawnEggItem {
    public InkableSpawnEggItem(EntityType<? extends MobEntity> type, int primaryColor, int secondaryColor, Settings settings) {
        super(type, primaryColor, secondaryColor, settings);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public int getColor(int tintIndex) {
        if (tintIndex == 0) return 0xFFFFFF;

        InkColor inkColor = getTargetedBlockInkColor();
        if (inkColor != null) return getDecimalColor(inkColor);

        // randomising color, similar to jeb_ sheep
        MinecraftClient client = MinecraftClient.getInstance();
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

    @Environment(EnvType.CLIENT)
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext ctx) {
        super.appendTooltip(stack, world, tooltip, ctx);

        if (ctx.isAdvanced()) {
            InkColor inkColor = Optional.ofNullable(getTargetedBlockInkColor()).orElse(InkColors._DEFAULT);
            tooltip.add(new LiteralText(inkColor.toString()).formatted(Formatting.DARK_GRAY));
        }
    }
}
