package net.splatcraft.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.splatcraft.entity.InkProjectileEntity;
import net.splatcraft.entity.access.PlayerEntityAccess;
import net.splatcraft.inkcolor.Inkable;
import net.splatcraft.item.weapon.InkConsumptionSettings;
import net.splatcraft.item.weapon.ShooterSettings;
import net.splatcraft.sound.SplatcraftSoundEvents;

import static net.splatcraft.item.InkTankItem.*;
import static net.splatcraft.util.SplatcraftConstants.*;

public class ShooterItem extends WeaponItem {
    protected final ShooterSettings shooter;

    public ShooterItem(ShooterSettings shooter, Settings settings) {
        super(settings);
        this.shooter = shooter;
    }

    public ShooterSettings getShooterSettings() {
        return this.shooter;
    }

    @Override
    public float getUsageMobility() {
        return this.shooter.getUsageMobility();
    }

    @Override
    public Pose getWeaponPose() {
        return Pose.SHOOTING;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        player.setSprinting(false);
        return super.use(world, player, hand);
    }

    @Override
    public void usageTick(World world, LivingEntity entity, ItemStack stack, int remainingUseTicks) {
        int useTicks = entity.getItemUseTime() - this.shooter.getInitialDelay();
        if (useTicks >= 0) {
            if (entity instanceof PlayerEntity player) {
                int rate = this.shooter.getFireInterval();
                if (useTicks == 0 || useTicks % rate == 0) {
                    this.shoot(world, player);
                }
            }
        }
    }

    public void shoot(World world, PlayerEntity player) {
        InkConsumptionSettings consumption = this.shooter.getConsumption();
        ((PlayerEntityAccess) player).setWeaponUseCooldown(consumption.getRegenerationCooldown());
        if (takeContainedInk(player, consumption.getConsumption())) {
            Inkable inkable = (Inkable) player;

            // initialize projectile
            InkProjectileEntity projectile = new InkProjectileEntity(player, world);
            projectile.setInkType(inkable.getInkType());
            projectile.setInkColor(inkable.getInkColor());
            projectile.setSize(this.shooter.getProjectileSize());
            projectile.setDropsInk(world.random.nextFloat() <= 0.75f);
            projectile.setVelocity(player, player.getPitch(), player.getYaw(), 0.0f, this.shooter.getProjectileSpeed(), /*8.0f*/ 0.0f);

            // spawn projectile
            world.spawnEntity(projectile);
            world.playSound(null, projectile.getX(), projectile.getY(), projectile.getZ(), SplatcraftSoundEvents.ITEM_SHOOTER_SHOOT, SoundCategory.PLAYERS, 1.0f, world.random.nextFloat() / 5);
        } else {
            player.sendMessage(new TranslatableText(T_LOW_INK).formatted(Formatting.RED), true);
        }
    }
}
