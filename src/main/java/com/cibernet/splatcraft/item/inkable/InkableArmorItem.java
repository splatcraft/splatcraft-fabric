package com.cibernet.splatcraft.item.inkable;

import com.cibernet.splatcraft.block.InkwellBlock;
import com.cibernet.splatcraft.block.entity.AbstractInkableBlockEntity;
import com.cibernet.splatcraft.init.SplatcraftItems;
import com.cibernet.splatcraft.inkcolor.ColorUtil;
import com.cibernet.splatcraft.inkcolor.InkColor;
import com.cibernet.splatcraft.inkcolor.InkColors;
import com.cibernet.splatcraft.item.EntityTickable;
import com.cibernet.splatcraft.item.MatchItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.DyeableArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.FluidTags;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class InkableArmorItem extends DyeableArmorItem implements MatchItem, EntityTickable {
    public InkableArmorItem(ArmorMaterial material, EquipmentSlot slot, Item.Settings settings) {
        super(material, slot, settings);
        SplatcraftItems.addToInkables(this);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        ColorUtil.appendTooltip(stack, tooltip);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (entity instanceof PlayerEntity) {
            InkColor inkColor = ColorUtil.getInkColor((PlayerEntity) entity);
            if (!ColorUtil.isColorLocked(stack) && !inkColor.equals(ColorUtil.getInkColor(stack))) {
                ColorUtil.setInkColor(stack, inkColor);
            }
        }

        super.inventoryTick(stack, world, entity, slot, selected);
    }

    @Override
    public void entityTick(ItemStack stack, ItemEntity entity) {
        BlockPos pos = entity.getBlockPos().down();

        if (entity.world.getBlockState(pos).getBlock() instanceof InkwellBlock) {
            BlockEntity blockEntity = entity.world.getBlockEntity(pos);
            if (blockEntity instanceof AbstractInkableBlockEntity inkableBlockEntity) {
                if (ColorUtil.getInkColor(stack) != inkableBlockEntity.getInkColor() || !ColorUtil.isColorLocked(stack)) {
                    ColorUtil.setInkColor(stack, inkableBlockEntity.getInkColor());
                    ColorUtil.setColorLocked(stack, true);
                }
            }
        } else if (entity.world.getFluidState(pos.up()).isIn(FluidTags.WATER) && ColorUtil.isColorLocked(stack)) {
            ColorUtil.setInkColor(stack, InkColors.NONE);
            ColorUtil.setColorLocked(stack, false);
        }
    }

    @Override
    public boolean hasColor(ItemStack stack) {
        return true;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public int getColor(ItemStack stack) {
        return ColorUtil.getInkColor(stack).getColorOrLocked();
    }
}
