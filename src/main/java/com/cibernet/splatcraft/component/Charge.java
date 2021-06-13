package com.cibernet.splatcraft.component;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class Charge {
    public static final Charge DEFAULT = new Charge(0, -1, true);

    private int time;
    private int slotIndex;
    private boolean canDischarge;

    public Charge(int time, int slotIndex, boolean canDischarge) {
        this.time = time;
        this.slotIndex = slotIndex;
        this.canDischarge = canDischarge;
    }

    public static void dischargeWeapon(PlayerEntity player) {
        player.world.playSoundFromEntity(null, player, SoundEvents.BLOCK_REDSTONE_TORCH_BURNOUT, SoundCategory.PLAYERS, 1.0f, 1.0f);
    }

    @NotNull
    public static Charge getCharge(PlayerEntity player) {
        return Optional.ofNullable(PlayerDataComponent.getComponent(player).getCharge()).orElse(DEFAULT);
    }
    public static void resetCharge(PlayerEntity player, int time, int slotIndex, boolean canDischarge) {
        PlayerDataComponent.getComponent(player).setCharge(new Charge(time, slotIndex, canDischarge));
    }
    public static void resetCharge(PlayerEntity player, int slotIndex, boolean canDischarge) {
        Charge.resetCharge(player, 0, slotIndex, canDischarge);
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

    public int getSlotIndex() {
        return this.slotIndex;
    }
    public void setSlotIndex(int slotIndex) {
        this.slotIndex = slotIndex;
    }

    public boolean canDischarge() {
        return this.canDischarge;
    }
    public void setCanDischarge(boolean canDischarge) {
        this.canDischarge = canDischarge;
    }
    public static boolean canDischarge(PlayerEntity player) {
        return Charge.getCharge(player).canDischarge();
    }

    /*
     * NBT utilities.
     */

    public void writeNbt(NbtCompound tag) {
        NbtCompound charge = new NbtCompound();
        charge.putInt("Time", this.time);
        charge.putInt("SlotIndex", this.slotIndex);
        charge.putBoolean("CanDischarge", this.canDischarge);

        tag.put("Charge", charge);
    }
    public static Charge readNbt(NbtCompound tag) {
        NbtCompound charge = tag.getCompound("Charge");

        return new Charge(
            charge.getInt("Time"),
            charge.getInt("SlotIndex"),
            charge.getBoolean("CanDischarge")
        );
    }
}
