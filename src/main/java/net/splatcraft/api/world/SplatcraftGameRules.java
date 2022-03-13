package net.splatcraft.api.world;

import net.fabricmc.fabric.api.gamerule.v1.CustomGameRuleCategory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.moddingplayground.frame.api.gamerules.v0.SynchronizedBooleanGameRuleRegistry;
import net.splatcraft.api.Splatcraft;
import net.splatcraft.impl.entity.access.PlayerEntityAccess;

import java.util.function.BiConsumer;

import static net.minecraft.world.GameRules.*;

public interface SplatcraftGameRules {
    CustomGameRuleCategory CATEGORY = new CustomGameRuleCategory(
        new Identifier(Splatcraft.MOD_ID, "category"),
        new TranslatableText("gamerule.%s.category".formatted(Splatcraft.MOD_ID))
            .formatted(Formatting.YELLOW, Formatting.BOLD)
    );

    Key<BooleanRule> INKWELL_CHANGES_INK_COLOR = register("inkwellChangesInkColor", false);
    Key<BooleanRule> INK_TANK_INK_REGENERATION = register("inkTankInkRegeneration", true);
    Key<BooleanRule> DISABLE_FOOD_HEAL_AFTER_DAMAGE = register("disableFoodHealAfterDamage", true);

    Key<BooleanRule> SPLATFEST_BAND_MUST_BE_HELD = register("splatfestBandMustBeHeld", true, (server, rule) -> {
        for (ServerPlayerEntity player : PlayerLookup.all(server)) ((PlayerEntityAccess) player).checkSplatfestBand();
    });

    Key<BooleanRule> LEAVE_SQUID_FORM_ON_ENEMY_INK = synced("leaveSquidFormOnEnemyInk", true);
    Key<BooleanRule> UNIVERSAL_INK = synced("universalInk", false);
    Key<BooleanRule> ENEMY_INK_SLOWNESS = synced("enemyInkSlowness", true);

    Key<BooleanRule> DAMAGE_ON_ENEMY_INK = register("damageOnEnemyInk", true);
    Key<BooleanRule> DAMAGE_ON_ENEMY_INK_ONLY_IN_SQUID_FORM = register("damageOnEnemyInk/onlyInSquidForm", false);
    Key<BooleanRule> DAMAGE_ON_ENEMY_INK_SCALES_TO_MAX_HEALTH = register("damageOnEnemyInk/scalesToMaxHealth", true);
    Key<BooleanRule> DAMAGE_ON_ENEMY_INK_CAN_KILL = register("damageOnEnemyInk/canKill", false);

    Key<BooleanRule> DAMAGE_WHEN_WET = register("damageWhenWet", true);
    Key<BooleanRule> DAMAGE_WHEN_WET_ONLY_IN_SQUID_FORM = register("damageWhenWet/onlyInSquidForm", false);
    Key<BooleanRule> DAMAGE_WHEN_WET_SCALES_TO_MAX_HEALTH = register("damageWhenWet/scalesToMaxHealth", true);
    Key<BooleanRule> DAMAGE_WHEN_WET_CAN_KILL = register("damageWhenWet/canKill", true);
    Key<BooleanRule> DAMAGE_WHEN_WET_INSTANT_KILL = register("damageWhenWet/instantKill", true);

    Key<BooleanRule> GAME_HEALTH = register("gameHealth", false);
    Key<BooleanRule> GAME_HEALTH_ONLY_IN_SQUID_FORM = register("gameHealth/onlyInSquidForm", false);
    Key<BooleanRule> GAME_HEALTH_SCALES_TO_MAX_HEALTH = register("gameHealth/scalesToMaxHealth", true);
    Key<BooleanRule> GAME_HEALTH_INVULNERABILITY_TICKS_ON_INK_DAMAGE = register("gameHealth/invulnerabilityTicksOnInkDamage", false);

    Key<BooleanRule> REGENERATE_WHEN_SUBMERGED = register("regenerateWhenSubmerged", true);
    Key<BooleanRule> REGENERATE_WHEN_SUBMERGED_SCALES_TO_MAX_HEALTH = register("regenerateWhenSubmerged/scalesToMaxHealth", true);

    private static <T extends Rule<T>> Key<T> register(String id, Type<T> type) {
        return GameRuleRegistry.register(Splatcraft.MOD_ID + Identifier.NAMESPACE_SEPARATOR + id, SplatcraftGameRules.CATEGORY, type);
    }

    private static Key<BooleanRule> register(String id, boolean defaultValue, BiConsumer<MinecraftServer, BooleanRule> onChanged) {
        return register(id, GameRuleFactory.createBooleanRule(defaultValue, onChanged));
    }

    private static Key<BooleanRule> register(String id, boolean defaultValue) {
        return register(id, defaultValue, (s, r) -> {});
    }

    private static Key<BooleanRule> synced(String id, boolean defaultValue) {
        Key<BooleanRule> key = register(id, SynchronizedBooleanGameRuleRegistry.createRule(defaultValue));
        SynchronizedBooleanGameRuleRegistry.INSTANCE.register(key, defaultValue);
        return key;
    }

    static boolean gameRule(World world, Key<BooleanRule> key) {
        return world.getGameRules().getBoolean(key);
    }
}
