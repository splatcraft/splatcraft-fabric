package net.splatcraft.item.weapon;

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
import net.splatcraft.item.UsageSpeedProvider;
import net.splatcraft.item.weapon.settings.InkConsumptionSettings;
import net.splatcraft.item.weapon.settings.InkProjectileSettings;
import net.splatcraft.item.weapon.settings.ShooterSettings;
import net.splatcraft.sound.SplatcraftSoundEvents;

import static net.splatcraft.item.InkTankItem.*;
import static net.splatcraft.util.SplatcraftConstants.*;

public class ShooterItem extends WeaponItem implements ShooterSettings.Provider, UsageSpeedProvider {
    protected final ShooterSettings shooter;

    public ShooterItem(ShooterSettings shooter, Settings settings) {
        super(settings);
        this.shooter = shooter;
    }

    @Override
    public ShooterSettings getWeaponSettings() {
        return this.shooter;
    }

    @Override
    public Pose getWeaponPose() {
        return Pose.SHOOTING;
    }

    @Override
    public float getMovementSpeedModifier(Context ctx) {
        return super.getMovementSpeedModifier(ctx) * (ctx.using() ? this.getWeaponSettings().getUsageMobility() : 1.0f);
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
                if (useTicks == 0 || useTicks % rate == 0) this.shoot(world, player);
            }
        }
    }

    public void shoot(World world, PlayerEntity player) {
        if (player instanceof Inkable inkable && player instanceof PlayerEntityAccess access) {
            InkConsumptionSettings consumption = this.shooter.getConsumption();
            access.setWeaponUseCooldown(consumption.getRegenerationCooldown());
            if (takeContainedInk(player, consumption.getConsumption())) {
                ShooterSettings settings = this.getWeaponSettings();
                InkProjectileSettings projectile = settings.getProjectileSettings();

                // initialize projectile
                InkProjectileEntity entity = new InkProjectileEntity(player, world);
                inkable.copyInkableTo(entity);
                entity.setSource(this);
                entity.setDropsInk(world.random.nextFloat() <= 0.75f);
                entity.setVelocity(player, player.getPitch(), player.getYaw(), 0.0f, projectile.getSpeed(), /*8.0f*/ 0.0f);

                // spawn projectile
                world.spawnEntity(entity);
                world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SplatcraftSoundEvents.ITEM_SHOOTER_SHOOT, SoundCategory.PLAYERS, 1.0f, world.random.nextFloat() / 5);
            } else {
                player.sendMessage(new TranslatableText(T_LOW_INK).formatted(Formatting.RED), true);
            }
        }
    }
}
