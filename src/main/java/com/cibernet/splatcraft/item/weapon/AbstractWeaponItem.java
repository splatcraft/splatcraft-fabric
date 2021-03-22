package com.cibernet.splatcraft.item.weapon;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.block.entity.AbstractInkableBlockEntity;
import com.cibernet.splatcraft.component.PlayerDataComponent;
import com.cibernet.splatcraft.handler.PlayerPoseHandler;
import com.cibernet.splatcraft.init.*;
import com.cibernet.splatcraft.inkcolor.ColorUtils;
import com.cibernet.splatcraft.inkcolor.InkColor;
import com.cibernet.splatcraft.inkcolor.InkColors;
import com.cibernet.splatcraft.item.EntityTickable;
import com.cibernet.splatcraft.item.InkTankArmorItem;
import com.cibernet.splatcraft.item.MatchItem;
import com.cibernet.splatcraft.item.inkable.ColorLockItemColorProvider;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import me.andante.chord.item.TabbedItemGroupAppendLogic;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.*;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.List;
import java.util.Objects;

public abstract class AbstractWeaponItem extends Item implements EntityTickable, TabbedItemGroupAppendLogic, MatchItem, ColorLockItemColorProvider {
    protected final float consumption;

    protected boolean secret;

    public AbstractWeaponItem(Item.Settings settings, float consumption) {
        super(settings);
        this.consumption = consumption;

        SplatcraftItems.addToInkables(this);
    }

    @Override
    public void appendStacksToTab(ItemGroup group, DefaultedList<ItemStack> stacks) {
        if (this.isIn(group) && !this.secret) {
            stacks.add(new ItemStack(this));
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext advanced) {
        super.appendTooltip(stack, world, tooltip, advanced);

        InkColor inkColor = ColorUtils.getInkColor(stack);
        if (!inkColor.equals(InkColors.NONE) || ColorUtils.isColorLocked(stack)) {
            tooltip.add(ColorUtils.getFormattedColorName(ColorUtils.getInkColor(stack), false));
        } else {
            tooltip.add(new TranslatableText("item." + Splatcraft.MOD_ID + ".tooltip.colorless"));
        }


    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        return ActionResult.PASS;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        user.setCurrentHand(hand);
        return super.use(world, user, hand);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (ColorUtils.isColorLocked(stack) || !(entity instanceof PlayerEntity)) {
            return;
        } else {
            PlayerDataComponent data = SplatcraftComponents.PLAYER_DATA.get(entity);
            ColorUtils.setInkColor(stack, data.getInkColor());
        }

        super.inventoryTick(stack, world, entity, slot, selected);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (user.getMainHandStack().equals(stack)) {
            user.clearActiveItem();
        }
        super.onStoppedUsing(stack, world, user, remainingUseTicks);
    }

    @Override
    public void entityTick(ItemStack stack, ItemEntity entity) {
        BlockPos floorPos = new BlockPos(entity.getPos().subtract(0.0D, -1.0D, 0.0D));

        if (entity.world.getBlockState(floorPos).getBlock().equals(SplatcraftBlocks.INKWELL)) {
            BlockEntity blockEntity = entity.world.getBlockEntity(floorPos);
            if (blockEntity instanceof AbstractInkableBlockEntity) {
                AbstractInkableBlockEntity inkableBlockEntity = (AbstractInkableBlockEntity) blockEntity;
                if (ColorUtils.getInkColor(stack) != inkableBlockEntity.getInkColor() || !ColorUtils.isColorLocked(stack)) {
                    ColorUtils.setInkColor(stack, inkableBlockEntity.getInkColor());
                    ColorUtils.setColorLocked(stack, true);
                }
            }
        } else if (entity.world.getBlockState(floorPos.up()).getMaterial().equals(Material.WATER) && ColorUtils.isColorLocked(stack)) {
            ColorUtils.setInkColor(stack, InkColors.NONE);
            ColorUtils.setColorLocked(stack, false);
        }
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 72000;
    }

    public static float getInkAmount(LivingEntity player, ItemStack weapon) {
        if (!SplatcraftGameRules.getBoolean(player.world, SplatcraftGameRules.REQUIRE_INK_TANK)) {
            return Float.MAX_VALUE;
        }

        ItemStack tank = player.getEquippedStack(EquipmentSlot.CHEST);
        if (!(tank.getItem() instanceof InkTankArmorItem)) {
            return 0;
        }

        return InkTankArmorItem.getInkAmount(tank, weapon);
    }

    public static boolean hasInk(PlayerEntity player, ItemStack weapon, boolean fling) {
        return !SplatcraftGameRules.getBoolean(player.world, SplatcraftGameRules.REQUIRE_INK_TANK) || getInkAmount(player, weapon) > getInkReductionAmount(player, (AbstractWeaponItem) weapon.getItem(), fling);
    }
    public static boolean hasInk(PlayerEntity player, ItemStack weapon) {
        return AbstractWeaponItem.hasInk(player, weapon, false);
    }

    public static void reduceInk(PlayerEntity player, float amount) {
        ItemStack tank = player.getEquippedStack(EquipmentSlot.CHEST);
        if (SplatcraftGameRules.getBoolean(player.world, SplatcraftGameRules.REQUIRE_INK_TANK) && tank.getItem() instanceof InkTankArmorItem) {
            InkTankArmorItem.setInkAmount(tank, InkTankArmorItem.getInkAmount(tank) - amount);
        }
    }
    public void reduceInk(PlayerEntity player, boolean fling) {
        reduceInk(player, getInkReductionAmount(player, this, fling));
    }
    public void reduceInk(PlayerEntity player) {
        reduceInk(player, getInkReductionAmount(player, this, false));
    }

    public static float getInkReductionAmount(PlayerEntity player, AbstractWeaponItem weapon, boolean fling) {
        return player.getEquippedStack(EquipmentSlot.CHEST).getMaxDamage() * ((fling ? Objects.requireNonNull(((RollerItem) weapon).component.fling).consumption : weapon.consumption) / 25);
    }

    public static void sendNoInkMessage(LivingEntity entity) {
        sendNoInkMessage(entity, SplatcraftSoundEvents.NO_INK);
    }
    public static void sendNoInkMessage(LivingEntity entity, SoundEvent sound) {
        if (entity instanceof PlayerEntity) {
            ((PlayerEntity) entity).sendMessage(new TranslatableText(Util.createTranslationKey("item", Registry.ITEM.getId(SplatcraftItems.INK_TANK)) + ".noInk").formatted(Formatting.RED), true);
            if (sound != null) {
                entity.world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), sound, SoundCategory.PLAYERS, 0.8F, ((entity.world.random.nextFloat() - entity.world.random.nextFloat()) * 0.1F + 1.0F) * 0.95F);
            }
        }
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        Multimap<EntityAttribute, EntityAttributeModifier> multimap = HashMultimap.create();
        if (slot == EquipmentSlot.MAINHAND) {
            multimap.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Weapon modifier", getWeaponSpeed(), EntityAttributeModifier.Operation.ADDITION));
        }
        return multimap;
    }
    public double getWeaponSpeed() {
        return 50.0D;
    }

    public PlayerPoseHandler.WeaponPose getPose() {
        return PlayerPoseHandler.WeaponPose.NONE;
    }

    public AbstractWeaponItem setSecret() {
        this.secret = true;
        return this;
    }
}
