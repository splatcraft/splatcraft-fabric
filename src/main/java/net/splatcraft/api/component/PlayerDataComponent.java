package net.splatcraft.api.component;

import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.chunk.WorldChunk;
import net.splatcraft.api.block.InkPassableBlock;
import net.splatcraft.api.block.entity.InkableBlockEntity;
import net.splatcraft.api.inkcolor.InkColor;
import net.splatcraft.api.inkcolor.InkColors;
import net.splatcraft.api.inkcolor.Inkable;
import net.splatcraft.api.item.SplatcraftItems;
import net.splatcraft.impl.client.config.ClientConfig;
import net.splatcraft.mixin.LivingEntityInvoker;
import net.splatcraft.mixin.client.ClientChunkManagerAccessor;
import net.splatcraft.mixin.client.ClientChunkManagerClientChunkMapAccessor;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReferenceArray;

import static net.splatcraft.api.particle.SplatcraftParticleType.*;
import static net.splatcraft.api.util.SplatcraftConstants.*;

public final class PlayerDataComponent implements Component, AutoSyncedComponent {
    private final PlayerEntity player;

    /**
     * Defines a player's ink color.
     */
    private InkColor inkColor = InkColors.getDefault();

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

    /**
     * Defines whether this component has been loaded at least once before.
     */
    private boolean initialized = false;

    @SuppressWarnings("unused")
    public PlayerDataComponent(PlayerEntity player) {
        this.player = player;
    }

    @Override
    public boolean shouldSyncWith(ServerPlayerEntity player) {
        return true;
    }

    public static PlayerDataComponent get(PlayerEntity player) {
        return SplatcraftComponents.PLAYER_DATA.get(player);
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

        if (this.player.world.isClient) setInkColorClient(inkColor);

        this.sync();
        return true;
    }

    @Environment(EnvType.CLIENT)
    private void setInkColorClient(InkColor inkColor) {
        MinecraftClient client = MinecraftClient.getInstance();

        // if the updated player is the client player, and color lock is enabled, re-render modelled inked blocks
        if (this.player == client.player && client.world != null && ClientConfig.INSTANCE.colorLock.getValue()) {
            ClientChunkManager clientChunkManager = client.world.getChunkManager();
            ClientChunkManager.ClientChunkMap chunkMap = ((ClientChunkManagerAccessor) clientChunkManager).getChunks();
            AtomicReferenceArray<WorldChunk> chunks = ((ClientChunkManagerClientChunkMapAccessor) chunkMap).getChunks();
            for (int i = 0, l = chunks.length(); i < l; i++) {
                WorldChunk chunk = chunks.get(i);
                if (chunk != null) {
                    for (Map.Entry<BlockPos, BlockEntity> entry : chunk.getBlockEntities().entrySet()) {
                        BlockPos pos = entry.getKey();
                        BlockEntity blockEntity = entry.getValue();
                        if (blockEntity instanceof InkableBlockEntity) {
                            BlockState state = blockEntity.getCachedState();
                            if (state.getRenderType() == BlockRenderType.MODEL) {
                                client.world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean isSquid() {
        return this.squid;
    }

    public boolean setSquid(boolean squid) {
        if (this.squid == squid) return false;
        this.squid = squid;

        this.player.calculateDimensions();

        if (squid) {
            this.player.clearActiveItem();
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

        ((LivingEntityInvoker) this.player).invokeUpdatePotionVisibility();
        if (this.player.isSpectator()) this.player.setInvisible(true);

        this.player.calculateDimensions();

        if (this.player.world.isClient) setSubmergedClient(submerged);

        this.sync();
        return true;
    }

    @Environment(EnvType.CLIENT)
    private void setSubmergedClient(boolean submerged) {
        if (ClientConfig.INSTANCE.inkSplashParticleOnTravel.getValue()) {
            Vec3d pos = this.player.getPos();
            Random random = this.player.getRandom();
            for (int i = 0; i < (submerged ? 12 : 4); i++) {
                double x = (random.nextDouble() - 0.5d) / 1.5d;
                double z = (random.nextDouble() - 0.5d) / 1.5d;
                inkSplash(this.player.world, (Inkable) this.player, pos.add(x, 0.0d, z), 1.0f);
            }
        }
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

    private boolean isInitialized() {
        return this.initialized;
    }

    private boolean setInitialized(boolean initialized) {
        if (this.initialized == initialized) return false;
        this.initialized = initialized;
        this.sync();
        return true;
    }

    public boolean tryInitialize() {
        return this.setInitialized(true);
    }

    @Override
    public void writeToNbt(NbtCompound nbt) {
        nbt.putString(NBT_INK_COLOR, InkColor.toString(this.getInkColor()));
        nbt.putBoolean(NBT_IS_SQUID, this.isSquid());
        nbt.putBoolean(NBT_IS_SUBMERGED, this.isSubmerged());
        nbt.putBoolean(NBT_HAS_SPLATFEST_BAND, this.hasSplatfestBand());
        nbt.putBoolean(NBT_INITIALIZED, this.isInitialized());
    }

    @Override
    public void readFromNbt(NbtCompound nbt) {
        this.setInkColor(InkColor.fromString(nbt.getString(NBT_INK_COLOR)));
        this.setSquid(nbt.getBoolean(NBT_IS_SQUID));
        this.setSubmerged(nbt.getBoolean(NBT_IS_SUBMERGED));
        this.setHasSplatfestBand(nbt.getBoolean(NBT_HAS_SPLATFEST_BAND));
        this.setInitialized(nbt.getBoolean(NBT_INITIALIZED));
    }
}
