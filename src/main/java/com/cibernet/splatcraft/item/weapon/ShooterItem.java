package com.cibernet.splatcraft.item.weapon;

import com.cibernet.splatcraft.entity.InkProjectileEntity;
import com.cibernet.splatcraft.handler.PlayerPoseHandler;
import com.cibernet.splatcraft.init.SplatcraftSoundEvents;
import com.cibernet.splatcraft.inkcolor.InkBlockUtils;
import com.cibernet.splatcraft.item.weapon.component.ShooterComponent;
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

        /*if (!(this instanceof BlasterItem)) {
            addStat(new WeaponStat("range", (stack, world) -> (int) ((projectileSpeed/1.2f)*100)));
            addStat(new WeaponStat("damage", (stack, world) -> (int) ((damage/20)*100)));
            addStat(new WeaponStat("fire_rate", (stack, world) -> (int) ((11-(firingSpeed))*10)));
        } TODO */
    }
    public ShooterItem(ShooterItem shooterItem) {
        this(shooterItem.settings, shooterItem.component);
    }
    public ShooterItem(ShooterItem rollerItem, ShooterComponent rollerComponent) {
        this(rollerItem.settings, rollerComponent);
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

            InkProjectileEntity proj = new InkProjectileEntity(world, player, stack, InkBlockUtils.getInkType(player), this.component.size, this.component.damage).setShooterTrail();
            proj.setProperties(player, player.pitch, player.yaw, 0.0f, this.component.speed, this.component.inaccuracy);
            world.spawnEntity(proj);

            world.playSound(null, player.getX(), player.getY() + 1, player.getZ(), SplatcraftSoundEvents.SHOOTER_FIRING, SoundCategory.PLAYERS, 0.7F, ((world.random.nextFloat() - world.random.nextFloat()) * 0.1F + 1.0F) * 0.95F);
        } else {
            sendNoInkMessage(player);
        }
    }

    @Override
    public PlayerPoseHandler.WeaponPose getPose() {
        return PlayerPoseHandler.WeaponPose.FIRE;
    }
}
