package com.cibernet.splatcraft.handler;

import com.cibernet.splatcraft.block.InkwellBlock;
import com.cibernet.splatcraft.block.entity.AbstractInkableBlockEntity;
import com.cibernet.splatcraft.component.PlayerDataComponent;
import com.cibernet.splatcraft.entity.damage.SplatcraftDamageSources;
import com.cibernet.splatcraft.init.*;
import com.cibernet.splatcraft.inkcolor.ColorUtils;
import com.cibernet.splatcraft.inkcolor.InkBlockUtils;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
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
                Item item = player.getStackInHand(hand).getItem();
                if (!item.isFood()) {
                    return ActionResult.FAIL;
                }
            }

            return ActionResult.PASS;
        });
        UseItemCallback.EVENT.register((player, world, hand) -> {
            ItemStack stack = player.getStackInHand(hand);

            PlayerDataComponent data = SplatcraftComponents.PLAYER_DATA.get(player);
            if (data.isSquid()) {
                if (!stack.getItem().isFood()) {
                    return TypedActionResult.fail(stack);
                }
            }

            return TypedActionResult.pass(stack);
        });
    }

    public static void onPlayerTick(PlayerEntity player) {
        PlayerDataComponent data = SplatcraftComponents.PLAYER_DATA.get(player);

        if (data.isSquid() && SplatcraftGameRules.getBoolean(player.world, SplatcraftGameRules.FLYING_DISABLES_SQUID_FORM) && player.abilities.flying) {
            data.setIsSquid(false);
            return;
        }

        if (SplatcraftGameRules.getBoolean(player.world, SplatcraftGameRules.WATER_DAMAGE) && player.isTouchingWater() && player.age % 10 == 0) {
            player.damage(SplatcraftDamageSources.WATER, 8.0f);
        }

        player.setInvisible(PlayerHandler.shouldBeInvisible(player));

        if (InkBlockUtils.shouldBeInvisible(player) && data.isSquid()) {
            data.setSquidSubmergeMode(Math.min(2,Math.max(data.getSquidSubmergeMode() + 1, 1)));
            player.setInvisible(true);
        } else {
            data.setSquidSubmergeMode(Math.max(-2, Math.min(data.getSquidSubmergeMode() - 1, -1)));
        }

        if (data.getSquidSubmergeMode() == 1) {
            player.world.playSound(null, player.getX(), player.getY(), player.getZ(), SplatcraftSoundEvents.INK_SUBMERGE, SoundCategory.PLAYERS, 0.5F, ((player.world.random.nextFloat() - player.world.random.nextFloat()) * 0.2F + 1.0F) * 0.95F);
        } else if (data.getSquidSubmergeMode() == -1) {
            player.world.playSound(null, player.getX(), player.getY(), player.getZ(), SplatcraftSoundEvents.INK_SUBMERGE, SoundCategory.PLAYERS, 0.5F, ((player.world.random.nextFloat() - player.world.random.nextFloat()) * 0.2F + 1.0F) * 0.95F);
        }

        if (data.isSquid()) {
            if (player.isOnGround()) {
                player.setSprinting(false);
                player.setPose(EntityPose.FALL_FLYING);
            }
            player.setSneaking(false);
            player.setSwimming(false);
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
