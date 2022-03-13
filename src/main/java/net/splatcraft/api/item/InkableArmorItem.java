package net.splatcraft.api.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.DyeableArmorItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import net.splatcraft.api.inkcolor.InkColors;
import net.splatcraft.api.inkcolor.Inkable;

import static net.splatcraft.api.client.util.ClientUtil.*;

public class InkableArmorItem extends DyeableArmorItem {
    public InkableArmorItem(ArmorMaterial material, EquipmentSlot slot, Settings settings) {
        super(material, slot, settings);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public int getColor(ItemStack stack) {
        return getDecimalColor(((Inkable) (Object) stack).getInkColor());
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (entity instanceof Inkable inkable) ((Inkable) (Object) stack).setInkColor(inkable.getInkColor());
    }

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
        if (this.isIn(group)) {
            ItemStack stack = new ItemStack(this);
            ((Inkable) (Object) stack).setInkColor(InkColors.getDefault());
            stacks.add(stack);
        }
    }
}
