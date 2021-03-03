package com.cibernet.splatcraft.item;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.client.model.ink_tank.AbstractInkTankArmorModel;
import com.cibernet.splatcraft.component.PlayerDataComponent;
import com.cibernet.splatcraft.inkcolor.ColorUtils;
import com.cibernet.splatcraft.inkcolor.InkBlockUtils;
import com.cibernet.splatcraft.tag.SplatcraftItemTags;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderingRegistry;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class InkTankArmorItem extends InkableArmorItem {
    public static final String id = "ink_tank";

    public final float capacity;
    public final Item.Settings settings;

    @Environment(EnvType.CLIENT)
    private AbstractInkTankArmorModel model;

    public InkTankArmorItem(float capacity, ArmorMaterial material, Item.Settings settings) {
        super(material, EquipmentSlot.CHEST, settings);
        this.capacity = capacity;
        this.settings = settings;
    }

    public InkTankArmorItem(float capacity, ArmorMaterial material) {
        this(capacity, material, new FabricItemSettings().maxDamage((int) capacity).group(Splatcraft.ItemGroups.WEAPONS));
    }

    public InkTankArmorItem(InkTankArmorItem parent) {
        this(parent.capacity, parent.getMaterial(), parent.settings);
        this.model = parent.model;
    }

    @SuppressWarnings("unused")
    public InkTankArmorItem(float capacity) {
        this(capacity, SplatcraftArmorMaterials.INK_TANK);
    }

    @Override
    public boolean isDamageable() {
        return true;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        super.inventoryTick(stack, world, entity, itemSlot, isSelected);

        if (entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            float ink = getInkAmount(stack);

            if (player.getEquippedStack(EquipmentSlot.CHEST).equals(stack) && ColorUtils.colorEquals(player, stack) && ink < capacity && !(player.getActiveItem().getItem() instanceof AbstractWeaponItem)) {
                setInkAmount(stack, Math.min(capacity, ink + (InkBlockUtils.canSwim(player) && PlayerDataComponent.isSquid(player) ? 1 : 0.1f)));
            }
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        tooltip.add(new TranslatableText(Util.createTranslationKey("item", new Identifier(Splatcraft.MOD_ID, InkTankArmorItem.id)) + ".tooltip.ink", String.format("%.1f",getInkAmount(stack)), capacity));
    }

    @Environment(EnvType.CLIENT)
    public BipedEntityModel<LivingEntity> getArmorModel(LivingEntity entity, ItemStack stack, EquipmentSlot slot, BipedEntityModel<LivingEntity> defaultModel) {
        if (entity.getEntityWorld().isClient) {
            BipedEntityModel<LivingEntity> model = createInkTankModel(stack, slot, defaultModel);
            return model != null ? model : ArmorRenderingRegistry.getArmorModel(entity, stack, slot, defaultModel);
        }

        return null;
    }

    @Environment(EnvType.CLIENT)
    private BipedEntityModel<LivingEntity> createInkTankModel(ItemStack stack, EquipmentSlot slot, BipedEntityModel<LivingEntity> defaultModel) {
        if (model == null) {
            model = AbstractInkTankArmorModel.getModelFromItem(stack.getItem());
        }

        if (!stack.isEmpty()) {
            if (stack.getItem() instanceof InkTankArmorItem) {
                model.rightLeg.visible = slot == EquipmentSlot.LEGS || slot == EquipmentSlot.FEET;
                model.leftLeg.visible = slot == EquipmentSlot.LEGS || slot == EquipmentSlot.FEET;

                model.torso.visible = slot == EquipmentSlot.CHEST;
                model.leftArm.visible = slot == EquipmentSlot.CHEST;
                model.rightArm.visible = slot == EquipmentSlot.CHEST;

                model.head.visible = slot == EquipmentSlot.HEAD;
                model.helmet.visible = slot == EquipmentSlot.HEAD;

                model.sneaking = defaultModel.sneaking;
                model.riding = defaultModel.riding;
                model.child = defaultModel.child;

                model.rightArmPose = defaultModel.rightArmPose;
                model.leftArmPose = defaultModel.leftArmPose;

                model.setInkLevels(InkTankArmorItem.getInkAmount(stack) / ((InkTankArmorItem) stack.getItem()).capacity);

                return model;
            }
        }

        return null;
    }

    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return false;
    }

    public static float getInkAmount(ItemStack stack) {
        return stack.getOrCreateSubTag(Splatcraft.MOD_ID).getFloat("ContainedInk");
    }

    public static float getInkAmount(ItemStack tank, ItemStack weapon) {
        return ((InkTankArmorItem) tank.getItem()).canUse(weapon.getItem()) ? getInkAmount(tank) : 0;
    }

    public static void setInkAmount(ItemStack stack, float value) {
        CompoundTag tag = stack.getOrCreateSubTag(Splatcraft.MOD_ID);
        if (tag != null) {
            tag.putFloat("ContainedInk", value);
        }
    }

    public boolean canUse(Item item) {
        boolean hasWhitelist = SplatcraftItemTags.INK_TANK_WHITELIST.values().size() > 0;
        boolean inWhitelist = SplatcraftItemTags.INK_TANK_WHITELIST.contains(item);
        boolean inBlacklist = SplatcraftItemTags.INK_TANK_BLACKLIST.contains(item);

        return !inBlacklist && (!hasWhitelist || inWhitelist);
    }

    public void refill(ItemStack stack) {
        setInkAmount(stack, capacity);
    }

    public static void deplete(ItemStack stack) {
        setInkAmount(stack, 0);
    }
}
