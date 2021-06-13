package com.cibernet.splatcraft.game.turf_war;

import com.cibernet.splatcraft.block.AbstractInkableBlock;
import com.cibernet.splatcraft.block.entity.AbstractInkableBlockEntity;
import com.cibernet.splatcraft.component.PlayerDataComponent;
import com.cibernet.splatcraft.inkcolor.InkColor;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ColorModifications {
    public static boolean changeInkColor(World world, @Nullable PlayerEntity player, InkColor inkColor, BlockPos pos, ColorModificationMode mode) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof AbstractInkableBlockEntity inkableBlockEntity) {
            InkColor blockInkColor = inkableBlockEntity.getInkColor();
            if (mode == ColorModificationMode.ALL) {
                if (inkableBlockEntity.hasBaseInkColor()) {
                    inkableBlockEntity.setBaseInkColor(inkColor);
                    return true;
                } else {
                    return inkableBlockEntity.setInkColor(inkColor);
                }
            } else {
                InkColor playerInkColor = PlayerDataComponent.getInkColor(player);

                if (mode == ColorModificationMode.WHITELIST_PLAYER) {
                    if (blockInkColor.equals(playerInkColor)) {
                        return inkableBlockEntity.setInkColor(inkColor);
                    }
                } else if (mode == ColorModificationMode.BLACKLIST_PLAYER) {
                    if (!blockInkColor.equals(playerInkColor)) {
                        return inkableBlockEntity.setInkColor(inkColor);
                    }
                }
            }
        }

        return false;
    }

    public static boolean clearInk(World world, @Nullable PlayerEntity player, BlockPos pos, ColorModificationMode mode) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof AbstractInkableBlockEntity inkableBlockEntity) {
            AbstractInkableBlock inkableBlock = (AbstractInkableBlock) world.getBlockState(pos).getBlock();
            InkColor blockInkColor = inkableBlockEntity.getInkColor();

            if (mode == ColorModificationMode.ALL) {
                return inkableBlock.massInkClear(world, pos);
            } else {
                InkColor playerInkColor = PlayerDataComponent.getInkColor(player);

                if (mode == ColorModificationMode.WHITELIST_PLAYER) {
                    if (blockInkColor.equals(playerInkColor)) {
                        return inkableBlock.massInkClear(world, pos);
                    }
                } else if (mode == ColorModificationMode.BLACKLIST_PLAYER) {
                    if (!blockInkColor.equals(playerInkColor)) {
                        return inkableBlock.massInkClear(world, pos);
                    }
                }
            }
        }

        return false;
    }
}
