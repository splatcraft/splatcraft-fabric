package com.cibernet.splatcraft.item;

import com.cibernet.splatcraft.block.AbstractInkableBlock;
import com.cibernet.splatcraft.inkcolor.InkColor;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class InkDisruptorItem extends RemoteItem {
    public InkDisruptorItem(Item.Settings settings) {
        super(settings);
    }

    @SuppressWarnings("deprecation")
    @Override
    public RemoteResult onRemoteUse(World world, BlockPos posA, BlockPos posB, ItemStack stack, InkColor colorIn, int mode) {
        BlockPos from = new BlockPos(Math.min(posA.getX(), posB.getX()), Math.min(posB.getY(), posA.getY()), Math.min(posA.getZ(), posB.getZ()));
        BlockPos to = new BlockPos(Math.max(posA.getX(), posB.getX()), Math.max(posB.getY(), posA.getY()), Math.max(posA.getZ(), posB.getZ()));

        if (!(from.getY() >= 0 && to.getY() < 256)) {
            return createResult(false, new TranslatableText("status.clear_ink.out_of_world"));
        }


        for (int j = from.getZ(); j <= to.getZ(); j += 16) {
            for (int k = from.getX(); k <= to.getX(); k += 16) {
                if (!world.isChunkLoaded(new BlockPos(k, to.getY() - from.getY(), j))) {
                    return createResult(false, new TranslatableText("status.clear_ink.out_of_world"));
                }
            }
        }
        int count = 0;
        int blockTotal = 0;
        for (int x = from.getX(); x <= to.getX(); x++)
            for (int y = from.getY(); y <= to.getY(); y++)
                for (int z = from.getZ(); z <= to.getZ(); z++) {
                    BlockPos pos = new BlockPos(x,y,z);
                    Block block = world.getBlockState(pos).getBlock();
                    if (block instanceof AbstractInkableBlock) {
                        if (((AbstractInkableBlock) block).remoteInkClear(world, pos)) {
                            count++;
                        }
                    }
                    blockTotal++;
                }

        return createResult(true, new TranslatableText("status.clear_ink."+ (count > 0 ? "success" : "no_ink"), count)).setIntResults(count, (count * 15) / blockTotal);
    }
}
