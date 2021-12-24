package net.splatcraft.item;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.splatcraft.Splatcraft;

import java.util.function.Supplier;

public enum SplatcraftArmorMaterial implements ArmorMaterial {
    INK_CLOTH("ink_cloth", 0, new int[]{ 1, 2, 3, 1 }, 0, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0.0f, 0.0f, null);

    private static final int[] BASE_DURABILITY = new int[]{ 13, 15, 16, 11 };

    private final String name;
    private final int durabilityMultiplier;
    private final int[] protectionAmounts;
    private final int enchantability;
    private final SoundEvent equipSound;
    private final float toughness;
    private final float knockbackResistance;
    private final Supplier<Ingredient> repairIngredient;

    SplatcraftArmorMaterial(
        String name, int durabilityMultiplier, int[] protectionAmounts,
        int enchantability, SoundEvent equipSound, float toughness,
        float knockbackResistance, Supplier<Ingredient> repairIngredient
    ) {
        this.name = new Identifier(Splatcraft.MOD_ID, name).toString();
        this.durabilityMultiplier = durabilityMultiplier;
        this.protectionAmounts = protectionAmounts;
        this.enchantability = enchantability;
        this.equipSound = equipSound;
        this.toughness = toughness;
        this.knockbackResistance = knockbackResistance;
        this.repairIngredient = repairIngredient == null ? () -> null : repairIngredient;
    }

    @Override
    public int getDurability(EquipmentSlot slot) {
        return BASE_DURABILITY[slot.getEntitySlotId()] * this.durabilityMultiplier;
    }

    @Override
    public int getProtectionAmount(EquipmentSlot slot) {
        return this.protectionAmounts[slot.getEntitySlotId()];
    }

    @Override
    public int getEnchantability() {
        return this.enchantability;
    }

    @Override
    public SoundEvent getEquipSound() {
        return this.equipSound;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return this.repairIngredient.get();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public float getToughness() {
        return this.toughness;
    }

    @Override
    public float getKnockbackResistance() {
        return this.knockbackResistance;
    }
}
