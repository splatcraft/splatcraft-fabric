package com.cibernet.splatcraft.item.remote;

import com.cibernet.splatcraft.block.InkwellBlock;
import com.cibernet.splatcraft.block.entity.InkableBlockEntity;
import com.cibernet.splatcraft.game.turf_war.ColorModificationMode;
import com.cibernet.splatcraft.game.turf_war.ColorModifications;
import com.cibernet.splatcraft.init.SplatcraftItems;
import com.cibernet.splatcraft.inkcolor.ColorUtil;
import com.cibernet.splatcraft.item.EntityTickable;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class ColorChangerItem extends AbstractWhitelistedRemoteItem implements EntityTickable {
    public ColorChangerItem(Item.Settings settings) {
        super(settings);
        SplatcraftItems.addToInkables(this);
    }

    @Override
    public boolean whitelistedRemoteUse(World world, PlayerEntity player, Hand hand, ItemStack stack, BlockPos pos, ColorModificationMode mode) {
        return ColorModifications.changeInkColor(world, player, ColorUtil.getInkColor(stack), pos, mode);
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext ctx) {
        ColorUtil.appendTooltip(stack, tooltip);
        super.appendTooltip(stack, world, tooltip, ctx);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);

        if (entity instanceof PlayerEntity && !ColorUtil.isColorLocked(stack) && ColorUtil.getInkColor(stack) != ColorUtil.getInkColor((PlayerEntity) entity)) {
            ColorUtil.setInkColor(stack, ColorUtil.getInkColor((PlayerEntity) entity));
        }
    }

    @Override
    public void entityTick(ItemStack stack, ItemEntity entity) {
        BlockPos pos = entity.getBlockPos().down();

        if (entity.world.getBlockState(pos).getBlock() instanceof InkwellBlock) {
            InkableBlockEntity blockEntity = (InkableBlockEntity) entity.world.getBlockEntity(pos);

            if (ColorUtil.getInkColor(stack) != ColorUtil.getInkColor(blockEntity)) {
                ColorUtil.setInkColor(entity.getStack(), ColorUtil.getInkColor(blockEntity));
                ColorUtil.setColorLocked(entity.getStack(), true);
            }
        }
    }
}
