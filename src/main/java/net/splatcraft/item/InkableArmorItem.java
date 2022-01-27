package net.splatcraft.item;

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
import net.splatcraft.inkcolor.InkColors;
import net.splatcraft.inkcolor.Inkable;

import static net.splatcraft.client.util.ClientUtil.*;

@SuppressWarnings("ConstantConditions")
public class InkableArmorItem extends DyeableArmorItem {
    public InkableArmorItem(ArmorMaterial material, EquipmentSlot slot, Settings settings) {
        super(material, slot, settings);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public int getColor(ItemStack stack) {
        return getDecimalColor(Inkable.class.cast(stack).getInkColor());
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
