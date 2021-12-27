package net.splatcraft.item;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import net.splatcraft.inkcolor.InkColors;
import net.splatcraft.inkcolor.Inkable;

@SuppressWarnings("ConstantConditions")
public class WeaponItem extends Item {
    public WeaponItem(Settings settings) {
        super(settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (entity instanceof Inkable inkable) Inkable.class.cast(stack).setInkColor(inkable.getInkColor());
    }

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
        if (this.isIn(group)) {
            ItemStack stack = new ItemStack(this);
            Inkable.class.cast(stack).setInkColor(InkColors.getDefault());
            stacks.add(stack);
        }
    }
}
