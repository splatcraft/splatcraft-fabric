package com.cibernet.splatcraft.item;

import com.cibernet.splatcraft.block.AbstractColorableBlock;
import com.cibernet.splatcraft.block.InkwellBlock;
import com.cibernet.splatcraft.block.entity.AbstractColorableBlockEntity;
import com.cibernet.splatcraft.block.entity.InkwellBlockEntity;
import com.cibernet.splatcraft.inkcolor.ColorUtils;
import com.cibernet.splatcraft.inkcolor.InkColor;
import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.Objects;

public class ColorChangerItem extends RemoteItem implements EntityTickable {
    public ColorChangerItem(Item.Settings settings) {
        super(settings);
    }


    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> texts, TooltipContext ctx) {
        super.appendTooltip(stack, world, texts, ctx);

        if (ColorUtils.isColorLocked(stack)) {
            texts.add(ColorUtils.getFormattedColorName(ColorUtils.getInkColor(stack), true));
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);

        if (entity instanceof PlayerEntity && !ColorUtils.isColorLocked(stack) && ColorUtils.getInkColor(stack) != ColorUtils.getPlayerColor((PlayerEntity) entity)) {
            ColorUtils.setInkColor(stack, ColorUtils.getPlayerColor((PlayerEntity) entity));
        }
    }

    @Override
    public void entityTick(ItemStack stack, ItemEntity entity) {
        BlockPos pos = entity.getBlockPos().down();

        if (entity.world.getBlockState(pos).getBlock() instanceof InkwellBlock) {
            InkwellBlockEntity blockEntity = (InkwellBlockEntity) entity.world.getBlockEntity(pos);

            if (ColorUtils.getInkColor(stack) != ColorUtils.getInkColor(blockEntity)) {
                ColorUtils.setInkColor(entity.getStack(), ColorUtils.getInkColor(blockEntity));
                ColorUtils.setColorLocked(entity.getStack(), true);
            }
        }
    }

    @Override
    public RemoteResult onRemoteUse(World world, BlockPos from, BlockPos to, ItemStack stack, InkColor color, int mode) {
        return ColorChangerItem.replaceColor(world, from, to, color, mode, ColorUtils.getInkColor(stack));
    }

    @SuppressWarnings("deprecation")
    public static RemoteResult replaceColor(World world, BlockPos from, BlockPos to, InkColor affectedColor, int mode, InkColor color) {
        BlockPos blockpos2 = new BlockPos(Math.min(from.getX(), to.getX()), Math.min(to.getY(), from.getY()), Math.min(from.getZ(), to.getZ()));
        BlockPos blockpos3 = new BlockPos(Math.max(from.getX(), to.getX()), Math.max(to.getY(), from.getY()), Math.max(from.getZ(), to.getZ()));

        if (!(blockpos2.getY() >= 0 && blockpos3.getY() < 256))
            return createResult(false, new TranslatableText("status.change_color.out_of_world"));


        for (int iZ = blockpos2.getZ(); iZ <= blockpos3.getZ(); iZ += 16) {
            for (int iX = blockpos2.getX(); iX <= blockpos3.getX(); iX += 16) {
                if (!world.isChunkLoaded(new BlockPos(iX, blockpos3.getY() - blockpos2.getY(), iZ))) {
                    return ColorChangerItem.createResult(false, new TranslatableText("status.change_color.out_of_world"));
                }
            }
        }

        int count = 0;
        int blockTotal = 0;
        for (int x = blockpos2.getX(); x <= blockpos3.getX(); x++)
            for (int y = blockpos2.getY(); y <= blockpos3.getY(); y++)
                for (int z = blockpos2.getZ(); z <= blockpos3.getZ(); z++) {
                    BlockPos pos = new BlockPos(x,y,z);
                    Block block = world.getBlockState(pos).getBlock();
                    if (block instanceof AbstractColorableBlock && world.getBlockEntity(pos) instanceof AbstractColorableBlockEntity) {
                        InkColor blockEntityColor = ((AbstractColorableBlockEntity) Objects.requireNonNull(world.getBlockEntity(pos))).getInkColor();

                        if (blockEntityColor != affectedColor && (mode == 0 || mode == 1 && blockEntityColor == color || mode == 2 && blockEntityColor != color) && ((AbstractColorableBlock) block).remoteColorChange(world, pos, affectedColor)) {
                            count++;
                        }
                    }

                    blockTotal++;
                }

        return ColorChangerItem.createResult(true, new TranslatableText("status.change_color.success", count, ColorUtils.getFormattedColorName(affectedColor, false))).setIntResults(count, count * 15 / blockTotal);
    }
}
