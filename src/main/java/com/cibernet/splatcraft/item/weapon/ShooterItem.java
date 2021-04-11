package com.cibernet.splatcraft.item.weapon;

import com.cibernet.splatcraft.entity.InkProjectileEntity;
import com.cibernet.splatcraft.handler.PlayerPoseHandler;
import com.cibernet.splatcraft.init.SplatcraftSoundEvents;
import com.cibernet.splatcraft.inkcolor.InkType;
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

public class ShooterItem extends AbstractWeaponItem {
    protected final Item.Settings settings;
    public final ShooterComponent component;

    public ShooterItem(Item.Settings settings, ShooterComponent component) {
        super(settings, component.consumption);

        this.settings = settings;
        this.component = component;
    }
    public ShooterItem(ShooterItem shooterItem) {
        this(shooterItem.settings, shooterItem.component);
    }
    public ShooterItem(ShooterItem rollerItem, ShooterComponent rollerComponent) {
        this(rollerItem.settings, rollerComponent);
    }

    @Override
    protected ImmutableList<WeaponStat> createWeaponStats() {
        return ImmutableList.of(
            new WeaponStat("range", (stack, world) -> (int) (this.component.projectileSpeed / 1.2f * 100)),
            new WeaponStat("damage", (stack, world) -> (int) (this.component.damage / 20 * 100)),
            new WeaponStat("fire_rate", (stack, world) -> (int) ((11 - this.component.firingSpeed) * 10))
        );
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        this.shoot(world, user, hand);
        return super.use(world, user, hand);
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
        if (hasInk(player, stack)) {
            reduceInk(player);

            InkProjectileEntity entity = new InkProjectileEntity(world, player, stack, InkType.from(player), this.component.size, this.component.damage).setShooterTrail();
            entity.setProperties(player, player.pitch, player.yaw, 0.0f, this.component.projectileSpeed, this.component.inaccuracy);
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
