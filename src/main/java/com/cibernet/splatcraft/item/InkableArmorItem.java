package com.cibernet.splatcraft.item;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.block.InkwellBlock;
import com.cibernet.splatcraft.block.entity.AbstractInkableBlockEntity;
import com.cibernet.splatcraft.inkcolor.ColorUtils;
import com.cibernet.splatcraft.inkcolor.InkColor;
import com.cibernet.splatcraft.inkcolor.InkColors;
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
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class InkableArmorItem extends DyeableArmorItem implements MatchItem, EntityTickable, InkableItem {
    public InkableArmorItem(ArmorMaterial material, EquipmentSlot slot, Item.Settings settings) {
        super(material, slot, settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);

        InkColor inkColor = ColorUtils.getInkColor(stack);
        if (inkColor != InkColors.NONE || ColorUtils.isColorLocked(stack)) {
            tooltip.add(ColorUtils.getFormattedColorName(ColorUtils.getInkColor(stack), true));
        } else {
            tooltip.add(new TranslatableText(Util.createTranslationKey("item", new Identifier(Splatcraft.MOD_ID, "ink_cloth_armor")) + ".tooltip.colorless"));
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (entity instanceof PlayerEntity) {
            InkColor inkColor = ColorUtils.getInkColor((PlayerEntity) entity);
            if (!ColorUtils.isColorLocked(stack) && inkColor != ColorUtils.getInkColor(stack)) {
                ColorUtils.setInkColor(stack, inkColor);
            }
        }

        super.inventoryTick(stack, world, entity, slot, selected);
    }

    @Override
    public void entityTick(ItemStack stack, ItemEntity entity) {
        BlockPos pos = entity.getBlockPos().down();

        if (entity.world.getBlockState(pos).getBlock() instanceof InkwellBlock) {
            BlockEntity blockEntity = entity.world.getBlockEntity(pos);
            if (blockEntity instanceof AbstractInkableBlockEntity) {
                AbstractInkableBlockEntity inkableBlockEntity = (AbstractInkableBlockEntity) blockEntity;

                if (ColorUtils.getInkColor(stack) != inkableBlockEntity.getInkColor() || !ColorUtils.isColorLocked(stack)) {
                    ColorUtils.setInkColor(stack, inkableBlockEntity.getInkColor());
                    ColorUtils.setColorLocked(stack, true);
                }
            }
        } else if (entity.world.getFluidState(pos.up()).isIn(FluidTags.WATER) && ColorUtils.isColorLocked(stack)) {
            ColorUtils.setInkColor(stack, InkColors.NONE);
            ColorUtils.setColorLocked(stack, false);
        }
    }

    @Override
    public boolean hasColor(ItemStack stack) {
        return true;
    }

    @Override
    public int getColor(ItemStack stack) {
        return ColorUtils.getInkColor(stack).getColor();
    }
}
