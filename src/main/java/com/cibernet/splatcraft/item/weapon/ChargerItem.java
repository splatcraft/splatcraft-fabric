package com.cibernet.splatcraft.item.weapon;

import com.cibernet.splatcraft.component.Charge;
import com.cibernet.splatcraft.component.PlayerDataComponent;
import com.cibernet.splatcraft.entity.InkProjectileEntity;
import com.cibernet.splatcraft.handler.PlayerPoseHandler;
import com.cibernet.splatcraft.init.SplatcraftSoundEvents;
import com.cibernet.splatcraft.inkcolor.InkType;
import com.cibernet.splatcraft.item.DisablesAttack;
import com.cibernet.splatcraft.item.weapon.component.ChargerComponent;
import com.cibernet.splatcraft.util.InkItemUtil;
import com.cibernet.splatcraft.util.Util;
import com.google.common.collect.ImmutableList;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ChargerItem extends AbstractWeaponItem implements DisablesAttack {
    public final ChargerComponent component;

    public ChargerItem(Settings settings, float mobility, ChargerComponent component) {
        super(settings, mobility);
        this.component = component;
    }

    public ChargerItem(ChargerItem parent) {
        this(parent.settings, parent.mobility, parent.component);
    }

    @Override
    protected ImmutableList<WeaponStat> createWeaponStats() {
        return ImmutableList.of(
            new WeaponStat("range", (float) this.component.projectile.lifetime * (this.component.projectile.speed / 2)),
            new WeaponStat("charge_speed", (40 - this.component.maxCharge) * 100 / 40f)
        );
    }

    @Override
    public float getMobility(@Nullable PlayerEntity player) {
        return super.getMobility(player) * (player != null && player.isUsingItem() ? 0.4f : 1.0f);
    }

    public void shoot(ItemStack stack, World world, PlayerEntity player) {
        float damage = this.component.projectile.damage;
        Charge charge = Charge.getCharge(player);
        int chargeTime = charge.getTime();

        // spawn projectile
        InkProjectileEntity proj = new InkProjectileEntity(
            world, player, stack, InkType.from(player), this.component.projectile.size,
            chargeTime > 0.95f
                ? damage
                : damage * chargeTime / 4f + damage / 4f
        ).setChargerStats(this.component.projectile.lifetime * chargeTime, chargeTime >= this.component.pierceCharge);

        float projectileSpeed = 0.5f + ((this.component.projectile.speed / 10) * (InkItemUtil.getInkAmount(player, stack) / InkItemUtil.getInkTankCapacity(player)));
        proj.setProperties(
            player, player.getPitch(), player.getYaw(), 0.0f,
            this.component.projectile.chargeAffectsSpeed
                ? ((float) chargeTime / this.component.maxCharge) * projectileSpeed
                : projectileSpeed,
            0.1f
        );
        world.spawnEntity(proj);

        // play sound
        world.playSoundFromEntity(null, player, SplatcraftSoundEvents.CHARGER_SHOT, SoundCategory.PLAYERS, 0.7F, ((world.random.nextFloat() - world.random.nextFloat()) * 0.1F + 1.0F) * 0.95F);

        // reduce ink
        this.reduceInk(player, chargeTime);
        // cooldown
        PlayerDataComponent.setCooldown(player, this.component.cooldownTime, player.getInventory().selectedSlot, true);
    }

    @Override
    public void usageTick(World world, LivingEntity entity, ItemStack stack, int remainingUseTicks) {
        if (!world.isClient && entity instanceof PlayerEntity player) {
            Charge charge = Charge.getCharge(player);
            int chargeTime = charge.getTime();

            boolean hasInk = this.hasInk(player, stack, chargeTime);

            if (this.canUse(player)) {
                if (chargeTime < this.component.maxCharge && (hasInk || entity.age % 3 == 0)) {
                    charge.setTime(chargeTime + 1);
                }
            } else if (!this.hasInk(player, stack, chargeTime)) {
                AbstractWeaponItem.sendNoInkMessage(player);
            }
        }
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity entity, int remainingUseTicks) {
        if (entity instanceof PlayerEntity player) {
            if (this.canUse(player)) {
                this.shoot(stack, world, player);
            }

            Charge.resetCharge(player, Util.getSlotWithStack(player.getInventory(), stack), true);
        }
    }

    public boolean canUse(PlayerEntity player) {
        return !PlayerDataComponent.hasCooldown(player);
    }

    @Override
    public float getInkConsumption(float charge) {
        return this.component.minConsumption + ((charge / this.component.maxCharge) * this.component.maxConsumption);
    }

    @Override
    public PlayerPoseHandler.WeaponPose getPose() {
        return PlayerPoseHandler.WeaponPose.FIRE;
    }
}
