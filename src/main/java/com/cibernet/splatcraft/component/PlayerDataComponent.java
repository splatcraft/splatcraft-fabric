package com.cibernet.splatcraft.component;

import com.cibernet.splatcraft.inkcolor.ColorUtils;
import com.cibernet.splatcraft.inkcolor.InkColor;
import com.cibernet.splatcraft.inkcolor.InkColors;
import com.cibernet.splatcraft.item.weapon.ChargeableItem;
import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class PlayerDataComponent implements Component, AutoSyncedComponent {
    private final Object provider;

    private boolean initialized = false;
    private InkColor inkColor = InkColors.NONE;
    private Cooldown cooldown = null;
    private Charge charge = null;

    private boolean moving = false;

    public PlayerDataComponent(Object provider) {
        this.provider = provider;
    }

    @Override
    public void writeToNbt(CompoundTag tag) {
        this.checkForInitialization();

        tag.putBoolean("Initialized", this.initialized);
        tag.putString("InkColor", this.inkColor.toString());
        tag.putBoolean("Moving", this.moving);
        if (this.cooldown != null) {
            this.cooldown.toTag(tag);
        }
        if (this.charge != null) {
            this.charge.toTag(tag);
        }
    }

    @Override
    public void readFromNbt(CompoundTag tag) {
        this.initialized = tag.getBoolean("Initialized");
        this.inkColor = InkColor.fromNonNull(tag.getString("InkColor"));
        this.moving = tag.getBoolean("Moving");
        this.cooldown = Cooldown.fromTag(tag);
        this.charge = Charge.fromTag(tag);

        this.checkForInitialization();
    }

    protected void checkForInitialization() {
        if (!this.initialized) {
            this.inkColor = ColorUtils.getRandomStarterColor(new Random());
            this.initialized = true;
        }
    }

    public void sync() {
        SplatcraftComponents.PLAYER_DATA.sync(this.provider);
    }

    // getters/setters
    public static PlayerDataComponent getComponent(@NotNull PlayerEntity player) {
        return SplatcraftComponents.PLAYER_DATA.get(player);
    }

    public InkColor getInkColor() {
        return this.inkColor;
    }
    public static InkColor getInkColor(PlayerEntity player) {
        return getComponent(player).getInkColor();
    }
    public void setInkColor(InkColor inkColor) {
        this.inkColor = inkColor;
        this.sync();
    }

    public boolean isMoving() {
        return this.moving;
    }
    @SuppressWarnings("unused")
    public static boolean isMoving(PlayerEntity player) {
        return getComponent(player).isMoving();
    }
    public void setMoving(boolean moving) {
        this.moving = moving;
        this.sync();
    }

    public Cooldown getCooldown() {
        return this.cooldown;
    }
    public void setCooldown(Cooldown cooldown) {
        this.cooldown = cooldown;
        this.sync();
    }

    public Charge getCharge() {
        return this.charge;
    }
    public void setCharge(Charge charge) {
        this.charge = charge;
        this.sync();
    }

    @SuppressWarnings("unused")
    public static class Cooldown {
        private int time;
        private final int maxTime;
        private final int slotIndex;
        private final boolean canMove;
        private final boolean forceCrouch;
        private final boolean preventWeaponUse;

        public Cooldown(int time, int maxTime, int slotIndex, boolean canMove, boolean forceCrouch, boolean preventWeaponUse) {
            this.time = time;
            this.maxTime = maxTime;
            this.slotIndex = slotIndex;
            this.canMove = canMove;
            this.forceCrouch = forceCrouch;
            this.preventWeaponUse = preventWeaponUse;
        }

        public boolean canMove() {
                return this.canMove;
        }
        public boolean forceCrouch() {
            return this.forceCrouch;
        }
        public boolean preventWeaponUse() {
            return this.preventWeaponUse;
        }
        public int getTime() {
            return this.time;
        }
        public int getMaxTime() {
            return this.maxTime;
        }
        public int getSlotIndex() {
            return this.slotIndex;
        }
        public Cooldown setTime(int time) {
            this.time = time;
            return this;
        }
        public Cooldown shrinkTime(int time) {
            this.time -= time;
            return this;
        }

        public static Cooldown fromTag(CompoundTag tag) {
            CompoundTag cooldownData = tag.getCompound("CooldownData");
            return new Cooldown(
                cooldownData.getInt("Time"),
                cooldownData.getInt("MaxTime"),
                cooldownData.getInt("SlotIndex"),
                cooldownData.getBoolean("CanMove"),
                cooldownData.getBoolean("ForceCrouch"),
                cooldownData.getBoolean("PreventWeaponUse")
            );
        }

        public void toTag(CompoundTag tag) {
            CompoundTag cooldownData = new CompoundTag();
            cooldownData.putInt("Time", this.time);
            cooldownData.putInt("MaxTime", this.maxTime);
            cooldownData.putInt("SlotIndex", this.slotIndex);
            cooldownData.putBoolean("CanMove", this.canMove);
            cooldownData.putBoolean("ForceCrouch", this.forceCrouch);

            tag.put("CooldownData", cooldownData);
        }

        public static Cooldown getCooldown(PlayerEntity player) {
            return getComponent(player).getCooldown();
        }

        public static void setCooldown(PlayerEntity player, Cooldown playerCooldown) {
            getComponent(player).setCooldown(playerCooldown);
        }

        public static Cooldown setCooldownTime(PlayerEntity player, int time) {
            PlayerDataComponent data = getComponent(player);

            if (data.getCooldown() == null) {
                return null;
            } else {
                data.getCooldown().setTime(time);
            }

            return data.getCooldown();
        }

        public static Cooldown shrinkCooldownTime(PlayerEntity player, int time) {
            return hasCooldown(player) ? setCooldownTime(player, getComponent(player).getCooldown().getTime() - time) : null;
        }

        public static boolean hasCooldown(PlayerEntity player) {
            Cooldown cooldown = getComponent(player).getCooldown();
            return cooldown != null && cooldown.getTime() > 0;
        }
    }

    @SuppressWarnings("unused")
    public static class Charge {
        public ItemStack chargedWeapon;
        public float charge;
        public boolean canDischarge = false;

        public Charge(ItemStack stack, float charge) {
            this.chargedWeapon = stack;
            this.charge = charge;
        }

        public static Charge fromTag(CompoundTag tag) {
            CompoundTag cooldownData = tag.getCompound("CooldownData");
            return new Charge(
                ItemStack.fromTag(cooldownData.getCompound("Item")),
                cooldownData.getFloat("Charge")
            );
        }

        public void toTag(CompoundTag tag) {
            CompoundTag cooldownData = new CompoundTag();
            cooldownData.put("Item", this.chargedWeapon.toTag(new CompoundTag()));
            cooldownData.putFloat("Charge", this.charge);

            tag.put("CooldownData", cooldownData);
        }

        public static Charge getCharge(PlayerEntity player) {
            return SplatcraftComponents.PLAYER_DATA.get(player).getCharge();
        }

        public static void setCharge(PlayerEntity player, Charge charge) {
            SplatcraftComponents.PLAYER_DATA.get(player).setCharge(charge);
        }

        public static boolean hasCharge(PlayerEntity player) {
            if (player == null) {
                return false;
            } else {
                PlayerDataComponent data = SplatcraftComponents.PLAYER_DATA.get(player);
                return data.getCharge() != null && data.getCharge().charge > 0;
            }
        }

        public static boolean shouldCreateCharge(PlayerEntity player) {
            if (player == null) {
                return false;
            } else {
                PlayerDataComponent data = SplatcraftComponents.PLAYER_DATA.get(player);
                return data.getCharge() == null;
            }
        }

        public static boolean chargeMatches(PlayerEntity player, ItemStack stack) {
            return hasCharge(player) && getCharge(player).chargedWeapon.isItemEqual(stack);
        }

        public static void addChargeValue(PlayerEntity player, ItemStack stack, float value) {
            if (shouldCreateCharge(player)) {
                setCharge(player, new Charge(stack, 0));
            }

            Charge charge = getCharge(player);

            if (chargeMatches(player, stack))
                charge.charge = Math.max(0, Math.min(charge.charge + value, 1f));
            else setCharge(player, new Charge(stack, 0));
        }

        public static float getChargeValue(PlayerEntity player, ItemStack stack) {
            return chargeMatches(player, stack) ? getCharge(player).charge : 0;
        }

        public static boolean canDischarge(PlayerEntity playerEntity) {
            return hasCharge(playerEntity) && SplatcraftComponents.PLAYER_DATA.get(playerEntity).getCharge().canDischarge;
        }

        public static void setCanDischarge(PlayerEntity player, boolean canDischarge) {
            if (hasCharge(player))
                getCharge(player).canDischarge = canDischarge;
        }

        public static void reset(PlayerEntity entity) {
            if (shouldCreateCharge(entity))
                setCharge(entity, new Charge(ItemStack.EMPTY, 0));
        }

        public static void dischargeWeapon(PlayerEntity player) {
            if (!(!player.world.isClient || !hasCharge(player))) {
                Charge charge = getCharge(player);
                Item dischargeItem = charge.chargedWeapon.getItem();

                if (dischargeItem instanceof ChargeableItem)
                    charge.charge = Math.max(0, charge.charge - ((ChargeableItem) dischargeItem).getDischargeSpeed());
                else charge.charge = 0;
            }
        }
    }
}
