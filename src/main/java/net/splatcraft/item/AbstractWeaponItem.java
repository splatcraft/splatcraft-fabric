package net.splatcraft.item;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.splatcraft.inkcolor.Inkable;

import static net.splatcraft.util.SplatcraftUtil.setInkColorOnStack;

public class AbstractWeaponItem extends Item {
    public AbstractWeaponItem(Settings settings) {
        super(settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (entity instanceof Inkable inkable) setInkColorOnStack(stack, inkable.getInkColor());
        super.inventoryTick(stack, world, entity, slot, selected);
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return Integer.MAX_VALUE;
    }
}
