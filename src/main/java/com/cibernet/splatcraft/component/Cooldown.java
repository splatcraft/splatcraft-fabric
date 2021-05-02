package com.cibernet.splatcraft.component;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class Cooldown {
    public static final Cooldown DEFAULT = new Cooldown(0, -1, -1, false);

    private int time;
    private int maxTime;
    private int slotIndex;
    private boolean preventsWeaponUse;

    public Cooldown(int time, int maxTime, int slotIndex, boolean preventsWeaponUse) {
        this.time = time;
        this.maxTime = maxTime;
        this.slotIndex = slotIndex;
        this.preventsWeaponUse = preventsWeaponUse;
    }

    public static boolean tick(PlayerEntity player) {
        Cooldown cooldown = PlayerDataComponent.getCooldown(player);
        int time = cooldown.getTime();
        if (time > 0) {
            cooldown.setTime(time - 1);
            return true;
        } else {
            cooldown.setMaxTime(-1);
            return false;
        }
    }

    /*
     * Getters and setters.
     */

    public int getTime() {
        return this.time;
    }
    public void setTime(int time) {
        this.time = time;
    }

    public int getMaxTime() {
        return this.maxTime;
    }
    public void setMaxTime(int maxTime) {
        this.maxTime = maxTime;
    }

    @NotNull
    public static Cooldown getCooldown(PlayerEntity player) {
        return Optional.ofNullable(PlayerDataComponent.getComponent(player).getCooldown()).orElse(DEFAULT);
    }

    public int getSlotIndex() {
        return this.slotIndex;
    }
    public void setSlotIndex(int slotIndex) {
        this.slotIndex = slotIndex;
    }

    public boolean preventsWeaponUse() {
        return this.preventsWeaponUse;
    }
    public void setPreventsWeaponUse(boolean preventsWeaponUse) {
        this.preventsWeaponUse = preventsWeaponUse;
    }

    /*
     * NBT utilities.
     */

    public void toTag(CompoundTag tag) {
        CompoundTag cooldown = new CompoundTag();
        cooldown.putInt("Time", this.time);
        cooldown.putInt("MaxTime", this.maxTime);
        cooldown.putInt("SlotIndex", this.slotIndex);
        cooldown.putBoolean("PreventsWeaponUse", this.preventsWeaponUse);

        tag.put("Cooldown", cooldown);
    }
    public static Cooldown fromTag(CompoundTag tag) {
        CompoundTag cooldown = tag.getCompound("Cooldown");

        return new Cooldown(
            cooldown.getInt("Time"),
            cooldown.getInt("MaxTime"),
            cooldown.getInt("SlotIndex"),
            cooldown.getBoolean("PreventsWeaponUse")
        );
    }
}
