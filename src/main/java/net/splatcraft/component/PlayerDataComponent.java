package net.splatcraft.component;

import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.splatcraft.inkcolor.InkColor;
import net.splatcraft.inkcolor.InkColors;

import static net.splatcraft.util.SplatcraftConstants.*;

public class PlayerDataComponent implements Component, AutoSyncedComponent {
    /**
     * Defines a player's ink color.
     */
    private InkColor inkColor = InkColors.DYE_WHITE;

    /**
     * Defines whether the player is in squid form.
     */
    private boolean squid = false;

    @SuppressWarnings("unused")
    public PlayerDataComponent(PlayerEntity player) {}

    public static PlayerDataComponent get(PlayerEntity player) {
        return SplatcraftComponents.PLAYER_DATA.get(player);
    }

    @Override
    public boolean shouldSyncWith(ServerPlayerEntity player) {
        return true;
    }

    public InkColor getInkColor() {
        return this.inkColor;
    }

    public boolean setInkColor(InkColor inkColor) {
        if (this.inkColor == inkColor) return false;
        this.inkColor = inkColor;
        return true;
    }

    public boolean isSquid() {
        return this.squid;
    }

    public boolean setSquid(boolean squid) {
        if (this.squid == squid) return false;
        this.squid = squid;
        return true;
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putBoolean(NBT_IS_SQUID, this.squid);

        if (this.inkColor == null) this.inkColor = InkColors.DYE_WHITE;
        tag.putString(NBT_INK_COLOR, this.inkColor.toString());
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        this.squid = tag.getBoolean(NBT_IS_SQUID);
        this.inkColor = InkColor.fromString(tag.getString(NBT_INK_COLOR));
    }
}
