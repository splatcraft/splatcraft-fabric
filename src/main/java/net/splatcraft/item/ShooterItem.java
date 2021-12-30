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
import net.splatcraft.entity.InkEntityAccess;
import net.splatcraft.entity.InkProjectileEntity;
import net.splatcraft.inkcolor.Inkable;
import net.splatcraft.item.weapon.ShooterSettings;
import net.splatcraft.sound.SplatcraftSoundEvents;

import static net.splatcraft.item.InkTankItem.takeContainedInk;
import static net.splatcraft.util.SplatcraftConstants.T_LOW_INK;

public class ShooterItem extends WeaponItem {
    protected final ShooterSettings shooter;

    public ShooterItem(ShooterSettings shooter, Settings settings) {
        super(settings);
        this.shooter = shooter;
    }

    @Override
    public float getMobility() {
        return this.shooter.mobility();
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        player.setSprinting(false);
        this.shoot(world, player, player.getStackInHand(hand));
        return super.use(world, player, hand);
    }

    @Override
    public void usageTick(World world, LivingEntity entity, ItemStack stack, int remainingUseTicks) {
        int useTicks = entity.getItemUseTime();
        if (useTicks != 0) {
            if (entity instanceof PlayerEntity player) {
                int rate = this.shooter.calculateFireRateModulo();
                if (useTicks % rate == 0) this.shoot(world, player, stack);
            }
        }
    }

    public void shoot(World world, PlayerEntity player, ItemStack stack) {
        if (takeContainedInk(player, this.shooter.inkConsumption())) {
            InkEntityAccess access = (InkEntityAccess) player;
            Inkable inkable = (Inkable) player;

            // initialize projectile
            InkProjectileEntity projectile = new InkProjectileEntity(player, world);
            projectile.setInkType(access.getInkType());
            projectile.setInkColor(inkable.getInkColor());
            projectile.setSize(this.shooter.projectileSize());
            projectile.setDropsInk(world.random.nextFloat() <= 0.75f);
            projectile.setVelocity(player, player.getPitch(), player.getYaw(), 0.0f, this.shooter.calculateSpeed(), 8.0f);

            // spawn projectile
            world.spawnEntity(projectile);
            world.playSound(null, projectile.getX(), projectile.getY(), projectile.getZ(), SplatcraftSoundEvents.ITEM_SHOOTER_SHOOT, SoundCategory.PLAYERS, 1.0f, world.random.nextFloat() / 5);
        } else {
            player.sendMessage(new TranslatableText(T_LOW_INK).formatted(Formatting.RED), true);
        }
    }
}
