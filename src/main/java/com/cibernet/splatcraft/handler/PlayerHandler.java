package com.cibernet.splatcraft.handler;

import com.cibernet.splatcraft.block.InkwellBlock;
import com.cibernet.splatcraft.block.entity.AbstractInkableBlockEntity;
import com.cibernet.splatcraft.component.PlayerDataComponent;
import com.cibernet.splatcraft.entity.damage.SplatcraftDamageSources;
import com.cibernet.splatcraft.init.SplatcraftAttributes;
import com.cibernet.splatcraft.init.SplatcraftComponents;
import com.cibernet.splatcraft.init.SplatcraftGameRules;
import com.cibernet.splatcraft.init.SplatcraftStats;
import com.cibernet.splatcraft.inkcolor.ColorUtils;
import com.cibernet.splatcraft.inkcolor.InkBlockUtils;
import com.cibernet.splatcraft.network.SplatcraftNetworkingConstants;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.Difficulty;

public class PlayerHandler {
    public static final EntityDimensions SQUID_FORM_DIMENSIONS = EntityDimensions.fixed(0.5F, 0.5F);
    public static final EntityDimensions SQUID_FORM_AIRBORNE_DIMENSIONS = EntityDimensions.fixed(0.5F, 1.0F);

    public static void registerEvents() {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            PlayerDataComponent data = SplatcraftComponents.PLAYER_DATA.get(player);
            if (data.isSquid()) {
                return ActionResult.FAIL;
            }

            return ActionResult.PASS;
        });
        UseItemCallback.EVENT.register((player, world, hand) -> {
            ItemStack stack = player.getStackInHand(hand);

            PlayerDataComponent data = SplatcraftComponents.PLAYER_DATA.get(player);
            if (data.isSquid()) {
                return TypedActionResult.fail(stack);
            }

            return TypedActionResult.pass(stack);
        });
    }

    public static void onPlayerTick(PlayerEntity player) {
        PlayerDataComponent data = SplatcraftComponents.PLAYER_DATA.get(player);

        if (player.abilities.flying && SplatcraftGameRules.getBoolean(player.world, SplatcraftGameRules.FLYING_DISABLES_SQUID_FORM) && data.isSquid()) {
            data.setIsSquid(false);
            player.calculateDimensions();
            return;
        }

        if (SplatcraftGameRules.getBoolean(player.world, SplatcraftGameRules.WATER_DAMAGE) && player.isTouchingWater() && player.age % 10 == 0) {
            player.damage(SplatcraftDamageSources.WATER, 8.0f);
        }

        boolean wasSubmerged = data.isSubmerged();
        boolean shouldBeSubmerged = data.isSquid() ? InkBlockUtils.shouldBeSubmerged(player) : PlayerHandler.shouldBeInvisible(player);
        if (shouldBeSubmerged != wasSubmerged) {
            if (!player.world.isClient) {
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeUuid(player.getUuid());
                buf.writeBoolean(shouldBeSubmerged);
                buf.writeString(ColorUtils.getInkColor(player.world.getBlockEntity(InkBlockUtils.getVelocityAffectingPos(player))).toString());
                buf.writeBlockPos(InkBlockUtils.getVelocityAffectingPos(player));

                for (ServerPlayerEntity serverPlayer : PlayerLookup.tracking((ServerWorld) player.world, player.getBlockPos())) {
                    ServerPlayNetworking.send(serverPlayer, SplatcraftNetworkingConstants.PLAY_TOGGLE_SQUID_FORM_EFFECTS_PACKET_ID, buf);
                }
            }

            data.setSubmerged(shouldBeSubmerged);
            player.setInvisible(shouldBeSubmerged);
        }

        if (data.isSquid()) {
            if (player.isOnGround()) {
                player.setPose(EntityPose.FALL_FLYING);
            }

            player.stopUsingItem();
            player.setSneaking(false);
            player.setSwimming(false);
            player.setSprinting(false);
            player.incrementStat(SplatcraftStats.SQUID_TIME);

            if (InkBlockUtils.takeDamage(player) && player.age % 20 == 0 && player.world.getDifficulty() != Difficulty.PEACEFUL) {
                player.damage(SplatcraftDamageSources.ENEMY_INK, 2.0f);
            } else if (InkBlockUtils.canSwim(player)) {
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
        return player.hasStatusEffect(StatusEffects.INVISIBILITY);
    }

    public static boolean shouldCancelPlayerToWorldInteraction(PlayerEntity player) {
        PlayerDataComponent data = SplatcraftComponents.PLAYER_DATA.get(player);
        return data.isSquid();
    }

    public static float getMovementSpeed(PlayerEntity player, float movementSpeed) {
        return !InkBlockUtils.canSwim(player) ? -1.0F : (float) (movementSpeed * (1.0F + player.getAttributeValue(SplatcraftAttributes.INK_SWIM_SPEED) * 100));
    }
}
