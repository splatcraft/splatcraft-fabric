package net.splatcraft.api.item.weapon;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.splatcraft.api.inkcolor.InkColors;
import net.splatcraft.api.inkcolor.Inkable;
import net.splatcraft.api.item.UsageSpeedProvider;
import net.splatcraft.api.item.weapon.settings.WeaponSettings;
import net.splatcraft.api.item.weapon.settings.WeaponWeight;
import net.splatcraft.impl.entity.access.InkEntityAccess;

public abstract class WeaponItem extends Item implements WeaponSettings.Provider, UsageSpeedProvider {
    public WeaponItem(Settings settings) {
        super(settings);
    }

    @Override
    public float getMovementSpeedModifier(Context ctx) {
        float ret = ctx.base();
        WeaponSettings settings = this.getWeaponSettings();
        WeaponWeight weight = settings.getWeight();

        if (ctx.using()) {
            if (!ctx.duplicate()) ret /= 0.2f;
            ret *= weight.getUsingMovementSpeed();
        }

        if (((InkEntityAccess) ctx.player()).isSubmergedInInk()) ret *= weight.getInkSwimSpeed();

        return ret;
    }

    public abstract Pose getWeaponPose();

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (entity instanceof Inkable inkable) ((Inkable) (Object) stack).setInkColor(inkable.getInkColor());
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        user.setCurrentHand(hand);
        return super.use(world, user, hand);
    }

    @Override
    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        return !player.isCreative();
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return Integer.MAX_VALUE;
    }

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
        if (this.isIn(group)) {
            ItemStack stack = new ItemStack(this);
            ((Inkable) (Object) stack).setInkColor(InkColors.getDefault());
            stacks.add(stack);
        }
    }

    public enum Pose {
        NONE, SHOOTING, DUAL_SHOOTING, ROLLING, BOW_CHARGE, SWINGING
    }
}
