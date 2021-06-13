package com.cibernet.splatcraft.component;

import com.cibernet.splatcraft.inkcolor.InkColor;
import com.cibernet.splatcraft.inkcolor.InkColors;
import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class PlayerDataComponent implements Component, AutoSyncedComponent {
    private final Object provider;
    private boolean initialized = false;

    private InkColor inkColor = InkColors.NONE;

    @NotNull
    private Cooldown cooldown = Cooldown.DEFAULT;
    @NotNull
    private Charge charge = Charge.DEFAULT;

    private boolean moving = false;

    public PlayerDataComponent(Object provider) {
        this.provider = provider;
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        this.checkForInitialization();
        tag.putBoolean("Initialized", this.initialized);

        tag.putString("InkColor", this.inkColor.toString());

        this.cooldown.writeNbt(tag);
        this.charge.writeNbt(tag);

        tag.putBoolean("Moving", this.moving);
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        this.initialized = tag.getBoolean("Initialized");

        this.inkColor = InkColor.fromNonNull(tag.getString("InkColor"));

        this.cooldown = Cooldown.readNbt(tag);
        this.charge = Charge.readNbt(tag);

        this.moving = tag.getBoolean("Moving");
    }

    protected void checkForInitialization() {
        if (!this.initialized) {
            // this.inkColor = ColorUtil.getRandomStarterColor(new Random());
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

    public Cooldown getCooldown() {
        return this.cooldown;
    }
    @NotNull
    public static Cooldown getCooldown(PlayerEntity player) {
        return Optional.ofNullable(getComponent(player).getCooldown()).orElse(Cooldown.DEFAULT);
    }
    public void setCooldown(Cooldown cooldown) {
        this.cooldown = cooldown;
        this.sync();
    }
    public static void setCooldown(PlayerEntity player, int time, int slotIndex, boolean preventsWeaponUse) {
        getComponent(player).setCooldown(new Cooldown(time, time, slotIndex, preventsWeaponUse));
    }
    public static boolean hasCooldown(PlayerEntity player) {
        return getCooldown(player).getTime() > 0;
    }

    public Charge getCharge() {
        return this.charge;
    }
    @NotNull
    public static Charge getCharge(PlayerEntity player) {
        return Optional.ofNullable(getComponent(player).getCharge()).orElse(Charge.DEFAULT);
    }
    public void setCharge(Charge charge) {
        this.charge = charge;
        this.sync();
    }
    public static boolean hasCharge(PlayerEntity player) {
        return getCharge(player).getTime() > 0;
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
}
