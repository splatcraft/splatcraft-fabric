package com.cibernet.splatcraft.component;

import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

public class LazyPlayerDataComponent implements Component, AutoSyncedComponent {
    private final Object provider;

    private boolean isSquid = false;
    private boolean isSubmerged = false;
    private boolean forceSync = false;

    public LazyPlayerDataComponent(Object provider) {
        this.provider = provider;
    }

    @Override
    public boolean shouldSyncWith(ServerPlayerEntity player) {
        boolean shouldSyncAlways = this.forceSync;
        this.forceSync = false;
        return shouldSyncAlways || !player.equals(this.provider);
    }
    public static void markForceSync(ServerPlayerEntity player) {
        getComponent(player).forceSync = true;
    }

    @Override
    public void writeToNbt(CompoundTag tag) {
        tag.putBoolean("IsSquid", this.isSquid);
        tag.putBoolean("IsSubmerged", this.isSubmerged);
    }

    @Override
    public void readFromNbt(CompoundTag tag) {
        this.isSquid = tag.getBoolean("IsSquid");
        this.isSubmerged = tag.getBoolean("IsSubmerged");
    }

    public void sync() {
        SplatcraftComponents.LAZY_PLAYER_DATA.sync(this.provider);
    }

    /*
        GETTERS/SETTERS
     */

    public static LazyPlayerDataComponent getComponent(@NotNull PlayerEntity player) {
        return SplatcraftComponents.LAZY_PLAYER_DATA.get(player);
    }

    public boolean isSquid() {
        return this.isSquid;
    }
    public static boolean isSquid(PlayerEntity player) {
        return getComponent(player).isSquid();
    }
    public void setIsSquid(boolean isSquid) {
        this.isSquid = isSquid;
        this.sync();
        ((PlayerEntity) this.provider).calculateDimensions();
    }
    public static void setIsSquid(PlayerEntity player, boolean isSquid) {
        LazyPlayerDataComponent lazyData = getComponent(player);
        lazyData.setIsSquid(isSquid);
    }

    public boolean isSubmerged() {
        return this.isSubmerged;
    }
    public static boolean isSubmerged(PlayerEntity player) {
        return getComponent(player).isSubmerged();
    }
    public void setSubmerged(boolean isSubmerged) {
        this.isSubmerged = isSubmerged;
        this.sync();
    }
}
