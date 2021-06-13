package com.cibernet.splatcraft.item.weapon;

import com.cibernet.splatcraft.entity.InkProjectileEntity;
import com.cibernet.splatcraft.handler.PlayerPoseHandler;
import com.cibernet.splatcraft.init.SplatcraftSoundEvents;
import com.cibernet.splatcraft.inkcolor.InkType;
import com.cibernet.splatcraft.item.DisablesAttack;
import com.cibernet.splatcraft.item.weapon.component.ShooterComponent;
import com.google.common.collect.ImmutableList;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class ShooterItem extends AbstractWeaponItem implements DisablesAttack {
    public final ShooterComponent component;

    public ShooterItem(Item.Settings settings, ShooterComponent component) {
        super(settings, 1.0f);
        this.component = component;
    }
    public ShooterItem(ShooterItem parent) {
        this(parent.settings, parent.component);
    }
    public ShooterItem(ShooterItem parent, ShooterComponent rollerComponent) {
        this(parent.settings, rollerComponent);
    }

    @Override
    protected ImmutableList<WeaponStat> createWeaponStats() {
        return ImmutableList.of(
            new WeaponStat("range", (int) (this.component.projectileSpeed / 1.2f * 100)),
            new WeaponStat("damage", (int) (this.component.damage / 20 * 100)),
            new WeaponStat("fire_rate", (int) ((11 - this.component.firingSpeed) * 10))
        );
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        this.shoot(world, user, hand);
        return super.use(world, user, hand);
    }

    @Override
    public float getInkConsumption(float data) {
        return this.component.consumption;
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        int useDelta = this.getMaxUseTime(stack) - remainingUseTicks - 1;
        if (!world.isClient && user instanceof PlayerEntity && useDelta != 0 && useDelta % this.component.firingSpeed == 0) {
            this.shoot(world, (PlayerEntity) user, user.getActiveHand());
        }
    }

    public void shoot(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (this.hasInk(player, stack)) {
            this.reduceInk(player, -1);

            InkProjectileEntity entity = new InkProjectileEntity(world, player, stack, InkType.from(player), this.component.size, this.component.damage).setShooterTrail();
            entity.setProperties(player, player.getPitch(), player.getYaw(), 0.0f, this.component.projectileSpeed, this.component.inaccuracy);
            world.spawnEntity(entity);

            world.playSound(null, player.getX(), player.getY() + 1, player.getZ(), SplatcraftSoundEvents.SHOOTER_FIRING, SoundCategory.PLAYERS, 0.7f, ((world.random.nextFloat() - world.random.nextFloat()) * 0.1f + 1.0f) * 0.95f);
        } else {
            sendNoInkMessage(player);
        }
    }

    @Override
    public PlayerPoseHandler.WeaponPose getPose() {
        return PlayerPoseHandler.WeaponPose.FIRE;
    }
}
