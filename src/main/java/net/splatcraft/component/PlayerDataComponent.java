package net.splatcraft.component;

import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.splatcraft.block.InkPassableBlock;
import net.splatcraft.entity.InkEntityAccess;
import net.splatcraft.entity.InkableCaster;
import net.splatcraft.inkcolor.InkColor;
import net.splatcraft.inkcolor.InkColors;
import net.splatcraft.item.SplatcraftItems;
import net.splatcraft.mixin.LivingEntityInvoker;

import java.util.Random;

import static net.splatcraft.network.NetworkingCommon.inkSplashParticleAtPos;
import static net.splatcraft.util.SplatcraftConstants.*;

public class PlayerDataComponent implements Component, AutoSyncedComponent {
    private final PlayerEntity player;

    /**
     * Defines a player's ink color.
     */
    private InkColor inkColor = InkColors._DEFAULT;

    /**
     * Defines whether the player is in squid form.
     */
    private boolean squid = false;

    /**
     * Defines whether the player is submerged in ink.
     */
    private boolean submerged = false;

    /**
     * Defines whether the player is holding a {@link SplatcraftItems#SPLATFEST_BAND}.
     */
    private boolean hasSplatfestBand = false;

    @SuppressWarnings("unused")
    public PlayerDataComponent(PlayerEntity player) {
        this.player = player;
    }

    public static PlayerDataComponent get(PlayerEntity player) {
        return SplatcraftComponents.PLAYER_DATA.get(player);
    }

    @Override
    public boolean shouldSyncWith(ServerPlayerEntity player) {
        return true;
    }

    public void sync() {
        SplatcraftComponents.PLAYER_DATA.sync(this.player);
    }

    public InkColor getInkColor() {
        return this.inkColor;
    }

    public boolean setInkColor(InkColor inkColor) {
        if (this.inkColor.equals(inkColor)) return false;
        this.inkColor = inkColor;
        this.sync();
        return true;
    }

    public boolean isSquid() {
        return this.squid;
    }

    public boolean setSquid(boolean squid) {
        if (this.squid == squid) return false;
        this.squid = squid;

        this.player.calculateDimensions();

        if (squid) {
            this.player.checkFallFlying();
            if (!this.player.getAbilities().flying) this.player.setSprinting(false);
        } else {
            // teleport up if inside block
            BlockPos pos = this.player.getBlockPos();
            BlockState state = this.player.world.getBlockState(pos);
            if (state.getBlock() instanceof InkPassableBlock) {
                VoxelShape shape = state.getCollisionShape(this.player.world, pos);
                double maxY = shape.getMax(Direction.Axis.Y);
                if (maxY < 1.0d) {
                    double y = pos.getY() + maxY;
                    if (y > this.player.getY()) this.player.setPosition(this.player.getX(), y, this.player.getZ());
                }
            }
        }

        this.sync();
        return true;
    }

    public boolean isSubmerged() {
        return this.submerged;
    }

    public boolean setSubmerged(boolean submerged) {
        if (this.submerged == submerged) return false;
        this.submerged = submerged;

        ((LivingEntityInvoker) this.player).invoke_updatePotionVisibility();
        if (this.player.isSpectator()) this.player.setInvisible(true);

        this.player.calculateDimensions();

        if (!this.player.world.isClient) {
            Vec3d pos = ((InkEntityAccess) this.player).getInkSplashParticlePos();
            Random rand = this.player.getRandom();
            for (int i = 0; i < 10; i++) {
                inkSplashParticleAtPos(
                    ((InkableCaster) this.player).toInkable(), pos.add(
                        rand.nextDouble() - 0.5d,
                        rand.nextDouble() / 3,
                        rand.nextDouble() - 0.5d
                    ), 0.9f
                );
            }
        }

        this.sync();
        return true;
    }

    public boolean hasSplatfestBand() {
        return this.hasSplatfestBand;
    }

    public boolean setHasSplatfestBand(boolean hasSplatfestBand) {
        if (this.hasSplatfestBand == hasSplatfestBand) return false;
        this.hasSplatfestBand = hasSplatfestBand;
        this.sync();
        return true;
    }

    @Override
    public void writeToNbt(NbtCompound nbt) {
        nbt.putString(NBT_INK_COLOR, InkColor.toString(this.inkColor));
        nbt.putBoolean(NBT_IS_SQUID, this.squid);
        nbt.putBoolean(NBT_IS_SUBMERGED, this.submerged);
        nbt.putBoolean(NBT_HAS_SPLATFEST_BAND, this.hasSplatfestBand);
    }

    @Override
    public void readFromNbt(NbtCompound nbt) {
        this.setInkColor(InkColor.fromString(nbt.getString(NBT_INK_COLOR)));
        this.setSquid(nbt.getBoolean(NBT_IS_SQUID));
        this.setSubmerged(nbt.getBoolean(NBT_IS_SUBMERGED));
        this.setHasSplatfestBand(nbt.getBoolean(NBT_HAS_SPLATFEST_BAND));
    }
}
