package com.cibernet.splatcraft.handler;

import com.cibernet.splatcraft.block.AbstractInkableBlock;
import com.cibernet.splatcraft.block.InkwellBlock;
import com.cibernet.splatcraft.block.entity.AbstractInkableBlockEntity;
import com.cibernet.splatcraft.component.LazyPlayerDataComponent;
import com.cibernet.splatcraft.component.PlayerDataComponent;
import com.cibernet.splatcraft.entity.damage.SplatcraftDamageSources;
import com.cibernet.splatcraft.init.SplatcraftAttributes;
import com.cibernet.splatcraft.init.SplatcraftGameRules;
import com.cibernet.splatcraft.init.SplatcraftSoundEvents;
import com.cibernet.splatcraft.init.SplatcraftStats;
import com.cibernet.splatcraft.inkcolor.ColorUtils;
import com.cibernet.splatcraft.inkcolor.InkColor;
import com.cibernet.splatcraft.inkcolor.InkColors;
import com.cibernet.splatcraft.network.SplatcraftNetworkingConstants;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;

public class PlayerHandler {
    public static final EntityDimensions SQUID_FORM_SUBMERGED_DIMENSIONS = EntityDimensions.fixed(0.5f, 0.5f);
    public static final EntityDimensions SQUID_FORM_DIMENSIONS = EntityDimensions.fixed(0.5f, 1.0f);

    protected static final double MOVING_THRESHOLD = 0.035d;

    public static void onPlayerTick(PlayerEntity player) {
        PlayerDataComponent data = PlayerDataComponent.getComponent(player);
        LazyPlayerDataComponent lazyData = LazyPlayerDataComponent.getComponent(player);

        Vec3d vel = player.getVelocity();
        data.setMoving(Math.abs(vel.getX()) >= MOVING_THRESHOLD || Math.abs(vel.getZ()) >= MOVING_THRESHOLD);

        if (player.abilities.flying && SplatcraftGameRules.getBoolean(player.world, SplatcraftGameRules.FLYING_DISABLES_SQUID_FORM) && lazyData.isSquid()) {
            lazyData.setIsSquid(false);
            return;
        }

        if (SplatcraftGameRules.getBoolean(player.world, SplatcraftGameRules.WATER_DAMAGE) && player.isTouchingWater() && player.age % 10 == 0) {
            player.damage(SplatcraftDamageSources.WATER, 8.0f);
        }

        boolean wasSubmerged = lazyData.isSubmerged();
        boolean shouldBeSubmerged = shouldBeSubmerged(player);
        if (shouldBeSubmerged != wasSubmerged) {
            InkColor inkColor = ColorUtils.getInkColor(player.world.getBlockEntity(shouldBeSubmerged ? getVelocityAffectingPos(player) : getVelocityAffectingPos(player).down()));

            if (!player.world.isClient) {
                player.world.playSoundFromEntity(null, player, shouldBeSubmerged ? SplatcraftSoundEvents.INK_SUBMERGE : SplatcraftSoundEvents.INK_UNSUBMERGE, SoundCategory.PLAYERS, 0.23f, 0.86f);

                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeUuid(player.getUuid());
                buf.writeIdentifier(inkColor.id);

                for (ServerPlayerEntity serverPlayer : PlayerLookup.tracking((ServerWorld) player.world, player.getBlockPos())) {
                    if (!serverPlayer.equals(player)) {
                        ServerPlayNetworking.send(serverPlayer, SplatcraftNetworkingConstants.PLAY_PLAYER_TOGGLE_SQUID_EFFECTS_PACKET_ID, buf);
                    }
                }
            } else {
                com.cibernet.splatcraft.client.network.SplatcraftClientNetworking.playPlayerToggleSquidEffects(player, player.world, inkColor); // static import to prevent ClassNotFoundException
            }

            lazyData.setSubmerged(shouldBeSubmerged);
            player.setInvisible(shouldBeSubmerged || shouldBeInvisible(player));
            player.calculateDimensions();
        }

        if (lazyData.isSquid()) {
            if (player.isOnGround()) {
                player.setPose(EntityPose.FALL_FLYING);
            }

            player.stopUsingItem();
            player.setSneaking(false);
            player.setSwimming(false);
            player.setSprinting(false);
            player.incrementStat(SplatcraftStats.SQUID_TIME);

            tryInkDamage(player);

            if (canSwim(player)) {
                player.fallDistance = 0;
                if (player.age % 5 == 0 && !player.hasStatusEffect(StatusEffects.POISON) && !player.hasStatusEffect(StatusEffects.WITHER)) {
                    player.heal(0.5f);
                }
            }

            if (SplatcraftGameRules.getBoolean(player.world, SplatcraftGameRules.INKWELL_CHANGES_PLAYER_INK_COLOR) && player.world.getBlockState(player.getBlockPos().down()).getBlock() instanceof InkwellBlock) {
                AbstractInkableBlockEntity blockEntity = (AbstractInkableBlockEntity) player.world.getBlockEntity(player.getBlockPos().down());
                if (blockEntity != null) {
                    ColorUtils.setInkColor(player, blockEntity.getInkColor());
                }
            }
        }
    }

    protected static boolean shouldBeInvisible(PlayerEntity player) {
        return player.hasStatusEffect(StatusEffects.INVISIBILITY) || player.isSpectator();
    }

    public static boolean canEnterSquidForm(PlayerEntity player) {
        return !player.hasVehicle();
    }

    public static boolean shouldCancelInteraction(PlayerEntity player) {
        return LazyPlayerDataComponent.isSquid(player);
    }
    public static ActionResult getEventActionResult(PlayerEntity player) {
        if (LazyPlayerDataComponent.isSquid(player)) {
            return ActionResult.FAIL;
        }

        return ActionResult.PASS;
    }
    public static <T> TypedActionResult<T> getEventActionResult(PlayerEntity player, T data) {
        if (LazyPlayerDataComponent.isSquid(player)) {
            return TypedActionResult.fail(data);
        }

        return TypedActionResult.pass(data);
    }

    public static float getMovementSpeed(PlayerEntity player, float movementSpeed) {
        return canSwim(player) ? (float) (movementSpeed * (1.0f + player.getAttributeValue(SplatcraftAttributes.INK_SWIM_SPEED) * 100)) : -1.0f;
    }

    public static boolean shouldBeSubmerged(PlayerEntity player) {
        return !player.isSpectator() && (LazyPlayerDataComponent.isSquid(player) && (canSwim(player) || canClimb(player)));
    }

    public static boolean canSwim(PlayerEntity player, boolean ignoreOnGround) {
        return (ignoreOnGround || player.isOnGround()) && isOnInk(player) && !PlayerDataComponent.getInkColor(player).equals(InkColors.NONE) && !onEnemyInk(player);
    }

    public static boolean canSwim(PlayerEntity player) {
        return canSwim(player, false);
    }

    public static boolean onEnemyInk(World world, BlockPos pos, InkColor comparisonColor) {
        if (!SplatcraftGameRules.getBoolean(world, SplatcraftGameRules.UNIVERSAL_INK)) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof AbstractInkableBlockEntity) {
                Block block = blockEntity.getCachedState().getBlock();
                if (block instanceof AbstractInkableBlock && ((AbstractInkableBlock) block).canDamage()) {
                    InkColor inkColor = ColorUtils.getInkColor(blockEntity);
                    return !comparisonColor.equals(InkColors.NONE) && !inkColor.equals(InkColors.NONE) && !comparisonColor.matches(inkColor.color);
                }
            }
        }

        return false;
    }

    public static boolean onEnemyInk(PlayerEntity player) {
        return onEnemyInk(player.world, player.getVelocityAffectingPos(), ColorUtils.getInkColor(player));
    }

    public static boolean canSwim(World world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof AbstractInkableBlockEntity) {
            return !ColorUtils.getInkColor(blockEntity).equals(InkColors.NONE) && ((AbstractInkableBlock) blockEntity.getCachedState().getBlock()).canSwim();
        }

        return false;
    }

    public static boolean isOnInk(PlayerEntity player) {
        return canSwim(player.world, player.getVelocityAffectingPos());
    }

    public static BlockPos getVelocityAffectingPos(PlayerEntity player) {
        if (!player.hasVehicle()) {
            BlockPos pos = getClimbingPos(player);
            if (pos != null) {
                return pos;
            }
        }

        return player.getVelocityAffectingPos();
    }

    public static BlockPos getClimbingPos(PlayerEntity player) {
        for (int i = 0; i < 4; i++) {
            float xOff = (i < 2 ? 0.32f : 0) * (i % 2 == 0 ? 1 : -1), zOff = (i < 2 ? 0 : 0.32f) * (i % 2 == 0 ? 1 : -1);
            BlockPos pos = new BlockPos(player.getX() - xOff, player.getY(), player.getZ() - zOff);
            Block block = player.world.getBlockState(pos).getBlock();

            if (block instanceof AbstractInkableBlock && ((AbstractInkableBlock) block).canClimb() && player.getBlockState().getBlock() != block) {
                BlockEntity blockEntity = player.world.getBlockEntity(pos);
                if (blockEntity instanceof AbstractInkableBlockEntity && ((AbstractInkableBlockEntity) blockEntity).getInkColor().matches(ColorUtils.getInkColor(player).color)) {
                    return pos;
                }
            }
        }

        return null;
    }

    public static boolean canClimb(PlayerEntity player) {
        if (onEnemyInk(player) || PlayerDataComponent.getInkColor(player).equals(InkColors.NONE)) {
            return false;
        } else {
            BlockPos pos = PlayerHandler.getClimbingPos(player);
            if (pos != null) {
                BlockEntity blockEntity = player.world.getBlockEntity(pos);
                if (blockEntity instanceof AbstractInkableBlockEntity) {
                    return ((AbstractInkableBlockEntity) blockEntity).getInkColor().matches(ColorUtils.getInkColor(player).color);
                }
            }
        }

        return false;
    }

    public static void tryInkDamage(PlayerEntity player) {
        if (player.age % 20 == 0 && player.world.getDifficulty() != Difficulty.PEACEFUL && onEnemyInk(player)) {
            player.damage(SplatcraftDamageSources.ENEMY_INK, 2.0f);
        }
    }

    public static void playSquidTravelEffects(Entity entity, InkColor inkColor, float scale) {
        if (!entity.world.isClient) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeUuid(entity.getUuid());

            buf.writeDouble(entity.getX());
            buf.writeDouble(entity.getY());
            buf.writeDouble(entity.getZ());

            buf.writeIdentifier(inkColor.id);
            buf.writeFloat(scale);

            for (ServerPlayerEntity serverPlayer : PlayerLookup.tracking((ServerWorld) entity.world, entity.getBlockPos())) {
                if (!serverPlayer.equals(entity)) {
                    ServerPlayNetworking.send(serverPlayer, SplatcraftNetworkingConstants.PLAY_SQUID_TRAVEL_EFFECTS_PACKET_ID, buf);
                }
            }
        } else if (entity instanceof PlayerEntity) {
            com.cibernet.splatcraft.client.network.SplatcraftClientNetworking.playSquidTravelEffects((PlayerEntity) entity, inkColor, scale, entity.getPos());
        }
    }
}
