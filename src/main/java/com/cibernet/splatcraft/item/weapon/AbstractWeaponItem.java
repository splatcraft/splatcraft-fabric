package com.cibernet.splatcraft.item.weapon;

import com.cibernet.splatcraft.block.entity.AbstractInkableBlockEntity;
import com.cibernet.splatcraft.component.PlayerDataComponent;
import com.cibernet.splatcraft.handler.PlayerPoseHandler;
import com.cibernet.splatcraft.init.SplatcraftBlocks;
import com.cibernet.splatcraft.init.SplatcraftGameRules;
import com.cibernet.splatcraft.init.SplatcraftItems;
import com.cibernet.splatcraft.init.SplatcraftSoundEvents;
import com.cibernet.splatcraft.inkcolor.ColorUtil;
import com.cibernet.splatcraft.inkcolor.InkColors;
import com.cibernet.splatcraft.item.EntityTickable;
import com.cibernet.splatcraft.item.MatchItem;
import com.cibernet.splatcraft.item.inkable.ColorLockItemColorProvider;
import com.cibernet.splatcraft.util.InkItemUtil;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
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
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.*;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

public abstract class AbstractWeaponItem extends Item implements EntityTickable, TabbedItemGroupAppendLogic, MatchItem, ColorLockItemColorProvider {
    protected boolean secret;
    protected boolean junior;
    public final float mobility;

    protected final Item.Settings settings;

    public AbstractWeaponItem(Item.Settings settings, float mobility) {
        super(settings);

        this.mobility = mobility;
        this.settings = settings;
        SplatcraftItems.addToInkables(this);
    }

    protected abstract ImmutableList<WeaponStat> createWeaponStats();

    @Override
    public void appendStacksToTab(ItemGroup group, DefaultedList<ItemStack> stacks) {
        if (this.isIn(group) && !this.secret) {
            stacks.add(new ItemStack(this));
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext advanced) {
        super.appendTooltip(stack, world, tooltip, advanced);
        ColorUtil.appendTooltip(stack, tooltip);

        LinkedList<WeaponStat> stats = new LinkedList<>(this.createWeaponStats());
        stats.add(new WeaponStat("mobility", this.getMobility(null) * 100));
        for (int i = 0; i < stats.size(); i++) {
            if (i == 0) {
                tooltip.add(LiteralText.EMPTY);
            }

            tooltip.add(stats.get(i).getTextComponent().formatted(Formatting.DARK_GREEN));
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
        if (entity instanceof PlayerEntity && !ColorUtil.isColorLocked(stack)) {
            ColorUtil.setInkColor(stack, PlayerDataComponent.getInkColor((PlayerEntity) entity));
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
        BlockPos floorPos = new BlockPos(entity.getPos().subtract(0.0d, -1.0d, 0.0d));

        if (entity.world.getBlockState(floorPos).getBlock().equals(SplatcraftBlocks.INKWELL)) {
            BlockEntity blockEntity = entity.world.getBlockEntity(floorPos);
            if (blockEntity instanceof AbstractInkableBlockEntity) {
                AbstractInkableBlockEntity inkableBlockEntity = (AbstractInkableBlockEntity) blockEntity;
                if (ColorUtil.getInkColor(stack) != inkableBlockEntity.getInkColor() || !ColorUtil.isColorLocked(stack)) {
                    ColorUtil.setInkColor(stack, inkableBlockEntity.getInkColor());
                    ColorUtil.setColorLocked(stack, true);
                }
            }
        } else if (entity.world.getBlockState(floorPos.up()).getMaterial().equals(Material.WATER) && ColorUtil.isColorLocked(stack)) {
            ColorUtil.setInkColor(stack, InkColors.NONE);
            ColorUtil.setColorLocked(stack, false);
        }
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return Integer.MAX_VALUE;
    }

    public boolean hasInk(PlayerEntity player, ItemStack weapon, float data) {
        return !SplatcraftGameRules.getBoolean(player.world, SplatcraftGameRules.REQUIRE_INK_TANK) || InkItemUtil.getInkAmount(player, weapon) > InkItemUtil.getInkReductionAmount(player, (AbstractWeaponItem) weapon.getItem(), data);
    }
    public boolean hasInk(PlayerEntity player, ItemStack weapon) {
        return this.hasInk(player, weapon, -1);
    }

    public void reduceInk(PlayerEntity player, float data) {
        InkItemUtil.reduceInk(player, this.getInkConsumption(data));
    }

    public abstract float getInkConsumption(float data);

    public static void sendNoInkMessage(LivingEntity entity) {
        sendNoInkMessage(entity, SplatcraftSoundEvents.NO_INK);
    }
    public static void sendNoInkMessage(LivingEntity entity, SoundEvent sound) {
        if (entity instanceof PlayerEntity) {
            ((PlayerEntity) entity).sendMessage(new TranslatableText(Util.createTranslationKey("item", Registry.ITEM.getId(SplatcraftItems.INK_TANK)) + ".noInk").formatted(Formatting.RED), true);
            if (sound != null) {
                entity.world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), sound, SoundCategory.PLAYERS, 0.8f, ((entity.world.random.nextFloat() - entity.world.random.nextFloat()) * 0.1f + 1.0f) * 0.95f);
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
        return 50.0d;
    }

    public float getMobility(@Nullable PlayerEntity player) {
        return player != null && PlayerDataComponent.hasCooldown(player) ? 1.0f : mobility;
    }

    public PlayerPoseHandler.WeaponPose getPose() {
        return PlayerPoseHandler.WeaponPose.NONE;
    }

    public AbstractWeaponItem setSecret() {
        this.secret = true;
        return this;
    }
    public AbstractWeaponItem setJunior() {
        this.junior = true;
        return this;
    }
}
