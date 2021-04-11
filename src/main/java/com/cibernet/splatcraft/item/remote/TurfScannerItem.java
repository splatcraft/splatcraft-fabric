package com.cibernet.splatcraft.item.remote;

import com.cibernet.splatcraft.game.turf_war.TurfScanMode;
import com.cibernet.splatcraft.game.turf_war.TurfScanner;
import com.cibernet.splatcraft.util.TagUtils;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

public class TurfScannerItem extends AbstractRemoteItem {
    public TurfScannerItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    protected void appendModeTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext ctx) {
        String remoteMode = AbstractRemoteItem.getRemoteMode(stack);
        tooltip.add((remoteMode.isEmpty() ? TurfScanMode.getDefault() : TurfScanMode.valueOf(remoteMode)).getText().formatted(Formatting.GRAY));
    }

    @Override
    public float getRemoteModeOrdinal(ItemStack stack) {
        return TurfScanMode.valueOf(AbstractRemoteItem.getRemoteMode(stack)).ordinal();
    }

    @Override
    protected void setDefaultRemoteMode(ItemStack stack) {
        AbstractRemoteItem.setRemoteMode(stack, TurfScanMode.getDefault());
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (!player.isSneaking() || player.abilities.flying) {
            if (AbstractRemoteItem.hasCornerPair(stack)) {
                try {
                    TurfScanner.scanArea(
                        world,
                        AbstractRemoteItem.getCornerA(stack), AbstractRemoteItem.getCornerB(stack),
                        TurfScanMode.valueOf(AbstractRemoteItem.getRemoteMode(stack)), player,
                        TurfScannerItem.countOnlyInkable(stack), TurfScannerItem.debug(stack)
                    );

                    return TypedActionResult.success(stack);
                } catch (Throwable e) {
                    player.sendMessage(new TranslatableText(createRemoteLang(e.getMessage())).formatted(Formatting.RED), true);
                }
            }
        } else {
            TurfScanMode cycledMode = TurfScanMode.valueOf(AbstractRemoteItem.getRemoteMode(stack)).cycle();
            AbstractRemoteItem.setRemoteMode(stack, cycledMode);
            player.sendMessage(new TranslatableText(TEXT_MODE_SET, cycledMode.getText()), true);

            return TypedActionResult.success(stack);
        }

        return super.use(world, player, hand);
    }

    private static boolean countOnlyInkable(ItemStack stack) {
        return TagUtils.getOrCreateSplatcraftTag(stack).getBoolean("CountOnlyInkable");
    }
    private static boolean debug(ItemStack stack) {
        return TagUtils.getOrCreateSplatcraftTag(stack).getBoolean("Debug");
    }
}
