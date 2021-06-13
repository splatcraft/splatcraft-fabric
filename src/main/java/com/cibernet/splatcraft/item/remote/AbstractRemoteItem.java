package com.cibernet.splatcraft.item.remote;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.util.TagUtil;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public abstract class AbstractRemoteItem extends Item {
    public static final String TOOLTIP_CORNERS_PAIR = createRemoteLang("tooltip.corners.pair");
    public static final String TOOLTIP_CORNERS_SINGLE = createRemoteLang("tooltip.corners.single");
    public static final String TEXT_CORNERS_PAIR = createRemoteLang("corners.pair");
    public static final String TEXT_CORNERS_SINGLE = createRemoteLang("corners.single");

    public static final String TEXT_MODE_SET = createRemoteLang("mode_set");

    public AbstractRemoteItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext ctx) {
        PlayerEntity player = ctx.getPlayer();

        if (AbstractRemoteItem.addCorner(ctx.getStack(), ctx.getBlockPos())) {
            World world = ctx.getWorld();

            if (world.isClient && player != null) {
                BlockPos pos = ctx.getBlockPos();
                player.sendMessage(new TranslatableText(hasCornerB(ctx.getStack()) ? TEXT_CORNERS_PAIR : TEXT_CORNERS_SINGLE, pos.toShortString()), true);
            }

            return ActionResult.success(world.isClient);
        }

        return super.useOnBlock(ctx);
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext ctx) {
        super.appendTooltip(stack, world, tooltip, ctx);

        Pair<Optional<BlockPos>, Optional<BlockPos>> corners = AbstractRemoteItem.getCornerPair(stack);
        Optional<BlockPos> cornerA = corners.getLeft();
        Optional<BlockPos> cornerB = corners.getRight();
        TranslatableText text = cornerA.isPresent() && cornerB.isPresent()
            ? new TranslatableText(TOOLTIP_CORNERS_PAIR, cornerA.get().toShortString(), cornerB.get().toShortString())
            : cornerA.map(singlePos -> new TranslatableText(TOOLTIP_CORNERS_SINGLE, singlePos.toShortString())).orElse(null);

        if (text != null) {
            tooltip.add(text.formatted(Formatting.GRAY));
        }

        this.appendModeTooltip(stack, world, tooltip, ctx);
    }
    protected abstract void appendModeTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext ctx);

    public abstract float getRemoteModeOrdinal(ItemStack stack);

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);

        if (!AbstractRemoteItem.hasRemoteMode(stack)) {
            this.setDefaultRemoteMode(stack);
        }
    }

    public static boolean hasRemoteMode(ItemStack stack) {
        String remoteMode = AbstractRemoteItem.getRemoteMode(stack);
        return remoteMode != null && !remoteMode.isEmpty();
    }
    public static String getRemoteMode(ItemStack stack) {
        return TagUtil.getOrCreateSplatcraftTag(stack).getString("RemoteMode");
    }
    protected static void setRemoteMode(ItemStack stack, Enum<?> mode) {
        NbtCompound splatcraft = TagUtil.getOrCreateSplatcraftTag(stack);
        splatcraft.putString("RemoteMode", mode.toString());

        stack.getOrCreateTag().put(Splatcraft.MOD_ID, splatcraft);
    }
    protected abstract void setDefaultRemoteMode(ItemStack stack);

    public static Pair<Optional<BlockPos>, Optional<BlockPos>> getCornerPair(ItemStack stack) {
        NbtCompound splatcraft = TagUtil.getOrCreateSplatcraftTag(stack);

        NbtCompound cornerA = splatcraft.getCompound("CornerA");
        NbtCompound cornerB = splatcraft.getCompound("CornerB");

        return new Pair<>(
            cornerA.isEmpty()
                ? Optional.empty()
                : Optional.of(NbtHelper.toBlockPos(cornerA)),
            cornerB.isEmpty()
                ? Optional.empty()
                : Optional.of(NbtHelper.toBlockPos(cornerB))
        );
    }

    public static boolean hasCornerPair(ItemStack stack) {
        return AbstractRemoteItem.hasCornerA(stack) && AbstractRemoteItem.hasCornerB(stack);
    }
    public static boolean hasCornerA(ItemStack stack) {
        return AbstractRemoteItem.getCornerPair(stack).getLeft().isPresent();
    }
    public static boolean hasCornerB(ItemStack stack) {
        return AbstractRemoteItem.getCornerPair(stack).getRight().isPresent();
    }
    @NotNull
    public static BlockPos getCornerA(ItemStack stack) {
        return AbstractRemoteItem.getCornerPair(stack).getLeft().orElseThrow(NullPointerException::new);
    }
    @NotNull
    public static BlockPos getCornerB(ItemStack stack) {
        return AbstractRemoteItem.getCornerPair(stack).getRight().orElseThrow(NullPointerException::new);
    }

    public static void setCornerPair(ItemStack stack, BlockPos cornerA, BlockPos cornerB) {
        NbtCompound splatcraft = TagUtil.getOrCreateSplatcraftTag(stack);

        if (cornerA != null) {
            splatcraft.put("CornerA", NbtHelper.fromBlockPos(cornerA));
        }
        if (cornerB != null) {
            splatcraft.put("CornerB", NbtHelper.fromBlockPos(cornerB));
        }

        stack.getOrCreateTag().put(Splatcraft.MOD_ID, splatcraft);
    }
    public static boolean addCorner(ItemStack stack, BlockPos pos) {
        if (!AbstractRemoteItem.hasCornerPair(stack)) {
            if (!AbstractRemoteItem.hasCornerA(stack)) {
                AbstractRemoteItem.setCornerPair(stack, pos, null);
            } else {
                AbstractRemoteItem.setCornerPair(stack, AbstractRemoteItem.getCornerPair(stack).getLeft().orElse(null), pos);
            }

            return true;
        }

        return false;
    }

    public static final String REMOTE_TRANSLATION = "item." + Splatcraft.MOD_ID + ".remote";
    public static String createRemoteLang(String append) {
        return REMOTE_TRANSLATION + "." + append;
    }
}
