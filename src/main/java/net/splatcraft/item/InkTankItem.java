package net.splatcraft.item;

import net.minecraft.block.DispenserBlock;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import net.splatcraft.entity.InkEntityAccess;
import net.splatcraft.inkcolor.InkColors;
import net.splatcraft.inkcolor.Inkable;
import net.splatcraft.world.SplatcraftGameRules;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static net.splatcraft.util.SplatcraftConstants.NBT_CONTAINED_INK;
import static net.splatcraft.util.SplatcraftConstants.T_CONTAINED_INK;

public class InkTankItem extends Item implements Wearable {
    private final float capacity;

    public InkTankItem(float capacity, Settings settings) {
        super(settings);
        DispenserBlock.registerBehavior(this, ArmorItem.DISPENSER_BEHAVIOR);

        this.capacity = capacity;
    }

    public float getCapacity() {
        return this.capacity;
    }

    public static float getContainedInk(ItemStack stack) {
        if (!(stack.getItem() instanceof InkTankItem item)) return 0;
        NbtCompound nbt = stack.getNbt();
        if (nbt != null && nbt.contains(NBT_CONTAINED_INK)) return nbt.getFloat(NBT_CONTAINED_INK);
        return item.getCapacity();
    }

    public static ItemStack setContainedInk(ItemStack stack, float containedInk) {
        if (!(stack.getItem() instanceof InkTankItem)) return stack;
        int accuracy = 1000;
        stack.getOrCreateNbt().putFloat(NBT_CONTAINED_INK, (float) ((int) (containedInk * accuracy)) / accuracy); // round to 3 decimal places
        return stack;
    }

    public static boolean takeContainedInk(ItemStack stack, float amount) {
        if (!(stack.getItem() instanceof InkTankItem)) return false;

        float containedInk = getContainedInk(stack);
        float nu = containedInk - amount;

        if (nu < 0) return false;
        setContainedInk(stack, nu);
        return true;
    }

    public static boolean takeContainedInk(PlayerEntity player, float percentage) {
        if (player.isCreative()) return true;
        if (player.isSpectator()) return false;

        ItemStack stack = player.getEquippedStack(EquipmentSlot.CHEST);
        if (!(stack.getItem() instanceof InkTankItem item)) return false;
        float capacity = item.getCapacity();
        return takeContainedInk(stack, capacity * (percentage / 100));
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!entity.world.isClient && entity instanceof Inkable inkable) {
            Inkable.class.cast(stack).setInkColor(inkable.getInkColor());

            float containedInk = getContainedInk(stack);
            float capacity = this.getCapacity();

            if (containedInk == capacity) {
                NbtCompound nbt = stack.getNbt();
                if (nbt == null || !nbt.contains(NBT_CONTAINED_INK)) setContainedInk(stack, capacity);
            } else {
                if (world.getGameRules().getBoolean(SplatcraftGameRules.INK_TANK_INK_REGENERATION)) {
                    if (!(entity instanceof PlayerEntity player) || (!player.isUsingItem() && player.getEquippedStack(EquipmentSlot.CHEST) == stack)) {
                        float nu = Math.min(
                            containedInk + (
                                ((InkEntityAccess) entity).isSubmerged()
                                    ? (int) (100f / (20 * 3.253f)) // splatoon-accurate calculation
                                    : entity.age % 10 == 0
                                        ? 1
                                        : 0
                            ), capacity
                        );
                        if (nu != containedInk) setContainedInk(stack, nu);
                    }
                }
            }
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        EquipmentSlot slot = MobEntity.getPreferredEquipmentSlot(stack);

        ItemStack equippedStack = player.getEquippedStack(slot);
        if (equippedStack.isEmpty()) {
            player.equipStack(slot, stack.copy());
            stack.setCount(0);
            if (!world.isClient) player.incrementStat(Stats.USED.getOrCreateStat(this));
            return TypedActionResult.success(stack, world.isClient);
        }

        return super.use(world, player, hand);
    }

    @Nullable
    @Override
    public SoundEvent getEquipSound() {
        return SoundEvents.ITEM_ARMOR_EQUIP_CHAIN;
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        String key = super.getTranslationKey(stack);
        return getContainedInk(stack) <= 1 ? "%s.empty".formatted(key) : key;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext ctx) {
        super.appendTooltip(stack, world, tooltip, ctx);
        tooltip.add(new TranslatableText(T_CONTAINED_INK, (int) getContainedInk(stack), (int) this.getCapacity()).formatted(Formatting.GRAY));
    }

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
        if (this.isIn(group)) {
            ItemStack stack = setContainedInk(new ItemStack(this), 0);
            Inkable.class.cast(stack).setInkColor(InkColors.getDefault());
            stacks.add(stack);
        }
    }
}
