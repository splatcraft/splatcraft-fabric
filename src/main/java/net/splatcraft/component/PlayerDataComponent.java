package net.splatcraft.component;

import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerDataComponent implements Component, AutoSyncedComponent {
    /**
     * Defines whether the player is in squid form.
     */
    private boolean squid;

    @SuppressWarnings("unused")
    public PlayerDataComponent(PlayerEntity provider) {
        this.squid = false;
    }

    public static PlayerDataComponent get(PlayerEntity player) {
        return SplatcraftComponents.PLAYER_DATA.get(player);
    }

    @Override
    public boolean shouldSyncWith(ServerPlayerEntity player) {
        return true;
    }

    public boolean isSquid() {
        return this.squid;
    }

    public void setSquid(boolean squid) {
        this.squid = squid;
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        this.squid = tag.getBoolean("Squid");
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putBoolean("Squid", this.squid);
    }
}
