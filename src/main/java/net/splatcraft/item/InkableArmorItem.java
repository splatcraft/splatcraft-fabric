package net.splatcraft.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.DyeableArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.splatcraft.inkcolor.Inkable;

import static net.splatcraft.client.util.ClientUtil.getDecimalColor;

@SuppressWarnings("ConstantConditions")
public class InkableArmorItem extends DyeableArmorItem {
    public InkableArmorItem(ArmorMaterial material, EquipmentSlot slot, Settings settings) {
        super(material, slot, settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (entity instanceof Inkable inkable) Inkable.class.cast(stack).setInkColor(inkable.getInkColor());
    }

    @Override
    @Environment(EnvType.CLIENT)
    public int getColor(ItemStack stack) {
        return getDecimalColor(Inkable.class.cast(stack).getInkColor());
    }
}
