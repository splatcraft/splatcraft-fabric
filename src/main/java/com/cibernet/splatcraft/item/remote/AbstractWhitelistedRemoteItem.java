package com.cibernet.splatcraft.item.remote;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.game.turf_war.ColorModificationMode;
import com.cibernet.splatcraft.util.StringConstants;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkManager;

import java.util.List;

public abstract class AbstractWhitelistedRemoteItem extends AbstractRemoteItem {
    public static final String TEXT_AFFECT_INKABLE_BLOCKS = "text." + Splatcraft.MOD_ID + ".affect_inkable_blocks";

    public AbstractWhitelistedRemoteItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (!player.isSneaking() || player.getAbilities().flying) {
            if (AbstractRemoteItem.hasCornerPair(stack)) {
                try {
                    int affected = 0;

                    BlockPos cornerA = AbstractRemoteItem.getCornerA(stack);
                    BlockPos cornerB = AbstractRemoteItem.getCornerB(stack);
                    int minX = Math.min(cornerA.getX(), cornerB.getX());
                    int maxX = Math.max(cornerA.getX(), cornerB.getX());
                    int minY = Math.min(cornerA.getY(), cornerB.getY());
                    int maxY = Math.max(cornerA.getY(), cornerB.getY());
                    int minZ = Math.min(cornerA.getZ(), cornerB.getZ());
                    int maxZ = Math.max(cornerA.getZ(), cornerB.getZ());

                    for (BlockPos pos : BlockPos.iterate(cornerA, cornerB)) {
                        if (minY < 0 || maxY > (player.world.getHeight() - 1)) {
                            throw new IllegalArgumentException("out_of_world");
                        } else {
                            ChunkManager chunkManager = player.world.getChunkManager();

                            if (!chunkManager.isChunkLoaded(minX >> 4, minZ >> 4)
                                || !chunkManager.isChunkLoaded(maxX >> 4, maxZ >> 4)) {
                                throw new IllegalArgumentException("not_loaded");
                            }
                        }

                        if (this.whitelistedRemoteUse(world, player, hand, stack, pos, ColorModificationMode.valueOf(AbstractRemoteItem.getRemoteMode(stack)))) {
                            affected++;
                        }
                    }

                    if (affected > 0) {
                        player.sendMessage(new TranslatableText(TEXT_AFFECT_INKABLE_BLOCKS, affected), true);
                        return TypedActionResult.success(stack);
                    } else {
                        player.sendMessage(new TranslatableText(StringConstants.TEXT_NO_INK).formatted(Formatting.RED), true);
                    }
                } catch (Throwable e) {
                    player.sendMessage(new TranslatableText(createRemoteLang(e.getMessage())).formatted(Formatting.RED), true);
                }
            }
        } else {
            ColorModificationMode cycledMode = ColorModificationMode.valueOf(AbstractRemoteItem.getRemoteMode(stack)).cycle();
            AbstractRemoteItem.setRemoteMode(stack, cycledMode);
            player.sendMessage(new TranslatableText(TEXT_MODE_SET, cycledMode.getText()), true);

            return TypedActionResult.success(stack);
        }

        return super.use(world, player, hand);
    }

    public abstract boolean whitelistedRemoteUse(World world, PlayerEntity player, Hand hand, ItemStack stack, BlockPos pos, ColorModificationMode mode);

    @Override
    public float getRemoteModeOrdinal(ItemStack stack) {
        String remoteMode = AbstractRemoteItem.getRemoteMode(stack);
        return (remoteMode.isEmpty() ? ColorModificationMode.getDefault() : ColorModificationMode.valueOf(remoteMode)).ordinal();
    }

    @Override
    protected void appendModeTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext ctx) {
        String remoteMode = AbstractRemoteItem.getRemoteMode(stack);
        tooltip.add((remoteMode.isEmpty() ? ColorModificationMode.getDefault() : ColorModificationMode.valueOf(remoteMode)).getText().formatted(Formatting.GRAY));
    }

    @Override
    protected void setDefaultRemoteMode(ItemStack stack) {
        AbstractRemoteItem.setRemoteMode(stack, ColorModificationMode.getDefault());
    }
}
