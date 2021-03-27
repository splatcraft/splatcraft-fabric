package com.cibernet.splatcraft.item.remote;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.init.SplatcraftSoundEvents;
import com.cibernet.splatcraft.inkcolor.ColorUtils;
import com.cibernet.splatcraft.inkcolor.InkColor;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.Objects;

public abstract class RemoteItem extends Item {
    protected final int totalModes;

    public RemoteItem(Item.Settings properties) {
        this(properties, 1);
    }

    public RemoteItem(Item.Settings properties, int totalModes) {
        super(properties);

        this.totalModes = totalModes;
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext ctx) {
        super.appendTooltip(stack, world, tooltip, ctx);

        TranslatableText text = null;

        CompoundTag tag = stack.getOrCreateSubTag(Splatcraft.MOD_ID);
        if (RemoteItem.hasCoordSet(stack)) {
            text = new TranslatableText(
                "item." + Splatcraft.MOD_ID + ".remote.coords.b",
                tag.getInt("PointAX"), tag.getInt("PointAY"), tag.getInt("PointAZ"),
                tag.getInt("PointBX"), tag.getInt("PointBY"), tag.getInt("PointBZ")
            );
        } else if (RemoteItem.hasCoordA(stack)) {
            text = new TranslatableText("item." + Splatcraft.MOD_ID + ".remote.coords.a",
                tag.getInt("PointAX"), tag.getInt("PointAY"), tag.getInt("PointAZ")
            );
        }

        if (text != null) {
            tooltip.add(text.formatted(Formatting.GRAY));
        }
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext ctx) {
        String key = hasCoordA(ctx.getStack()) ? "b" : "a";
        if (addCoords(ctx.getStack(), ctx.getBlockPos())) {
            BlockPos pos = ctx.getBlockPos();

            if (ctx.getWorld().isClient) {
                Objects.requireNonNull(ctx.getPlayer()).sendMessage(new TranslatableText("status." + Splatcraft.MOD_ID + ".coord_set." + key, pos.getX(), pos.getY(), pos.getZ()), true);
            }

            return ActionResult.success(ctx.getWorld().isClient);
        }
        return this.use(ctx.getWorld(), Objects.requireNonNull(ctx.getPlayer()), ctx.getHand()).getResult();
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
        ItemStack stack = playerEntity.getStackInHand(hand);
        int mode = getRemoteMode(stack);

        if (playerEntity.isSneaking() && totalModes > 1) {
            mode = cycleRemoteMode(stack);
            String statusMsg = getTranslationKey() + ".mode." + mode;
            playerEntity.sendMessage(new TranslatableText("status." + Splatcraft.MOD_ID + ".remote_mode", new TranslatableText(statusMsg)), true);
        } else if (hasCoordSet(stack)) {
            RemoteResult remoteResult = this.onRemoteUse(world, stack, ColorUtils.getInkColor(playerEntity), mode);

            Text output = remoteResult.getOutput();
            if (output != null) {
                playerEntity.sendMessage(output, true);
            }
            world.playSound(playerEntity, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), SplatcraftSoundEvents.REMOTE_USE, SoundCategory.PLAYERS, 0.8f, 1);
            return new TypedActionResult<>(remoteResult.wasSuccessful() ? ActionResult.SUCCESS : ActionResult.FAIL, stack);
        }

        return super.use(world, playerEntity, hand);
    }

    public static int getRemoteMode(ItemStack stack) {
        return stack.getOrCreateSubTag(Splatcraft.MOD_ID).getInt("Mode");
    }
    public static void setRemoteMode(ItemStack stack, int mode) {
        stack.getOrCreateSubTag(Splatcraft.MOD_ID).putInt("Mode", mode);
    }
    public static int cycleRemoteMode(ItemStack stack) {
        int mode = RemoteItem.getRemoteMode(stack) + 1;
        if (stack.getItem() instanceof RemoteItem) {
            mode %= ((RemoteItem) stack.getItem()).totalModes;
        }
        RemoteItem.setRemoteMode(stack, mode);
        return mode;
    }

    public static boolean hasCoordA(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateSubTag(Splatcraft.MOD_ID);
        return tag != null && tag.contains("PointAX") && tag.contains("PointAY") && tag.contains("PointAZ");
    }
    public static boolean hasCoordB(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateSubTag(Splatcraft.MOD_ID);
        return tag != null && tag.contains("PointBX") && tag.contains("PointBY") && tag.contains("PointBZ");
    }

    public static boolean hasCoordSet(ItemStack stack) {
        return RemoteItem.hasCoordA(stack) && RemoteItem.hasCoordB(stack);
    }
    public static BlockPos[] getCoordSet(ItemStack stack) {
        if (!hasCoordSet(stack)) {
            return new BlockPos[0];
        }
        CompoundTag tag = stack.getOrCreateSubTag(Splatcraft.MOD_ID);

        return new BlockPos[] {
            new BlockPos(tag.getInt("PointAX"), tag.getInt("PointAY"), tag.getInt("PointAZ")),
            new BlockPos(tag.getInt("PointBX"), tag.getInt("PointBY"), tag.getInt("PointBZ"))
        };
    }
    public static boolean addCoords(ItemStack stack, BlockPos pos) {
        if (!RemoteItem.hasCoordSet(stack)) {
            CompoundTag tag = stack.getOrCreateSubTag(Splatcraft.MOD_ID);

            String key = hasCoordA(stack) ? "B" : "A";

            tag.putInt("Point" + key + "X", pos.getX());
            tag.putInt("Point" + key + "Y", pos.getY());
            tag.putInt("Point" + key + "Z", pos.getZ());

            return true;
        }

        return false;
    }

    public abstract RemoteResult onRemoteUse(World world, BlockPos posA, BlockPos posB, ItemStack stack, InkColor color, int mode);
    public RemoteResult onRemoteUse(World world, ItemStack stack, InkColor color, int mode) {
        BlockPos[] coordSet = getCoordSet(stack);
        BlockPos start = coordSet[0];
        BlockPos end = coordSet[1];

        return this.onRemoteUse(world, start, end, stack, color, mode);
    }

    public static RemoteResult createResult(boolean success, Text output) {
        return new RemoteResult(success, output);
    }

    public static class RemoteResult {
        boolean success;
        Text output;

        int commandResult = 0;
        int comparatorResult = 0;

        public RemoteResult(boolean success, Text output) {
            this.success = success;
            this.output = output;
        }

        public RemoteResult setIntResults(int commandResult, int comparatorResult) {
            this.commandResult = commandResult;
            this.comparatorResult = comparatorResult;

            return this;
        }

        public boolean wasSuccessful() {
            return this.success;
        }
        public Text getOutput() {
            return this.output;
        }
    }
}
