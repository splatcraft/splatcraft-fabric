package net.splatcraft.world;

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
import net.splatcraft.Splatcraft;
import net.splatcraft.entity.access.PlayerEntityAccess;

import java.util.function.BiConsumer;

import static net.minecraft.util.Identifier.NAMESPACE_SEPARATOR;
import static net.minecraft.world.GameRules.*;
import static net.splatcraft.network.NetworkingCommon.*;

public class SplatcraftGameRules {
    public static final CustomGameRuleCategory CATEGORY = new CustomGameRuleCategory(
        new Identifier(Splatcraft.MOD_ID, "category"),
        new TranslatableText("gamerule.%s.category".formatted(Splatcraft.MOD_ID))
            .formatted(Formatting.YELLOW, Formatting.BOLD)
    );

    public static final Key<BooleanRule> INKWELL_CHANGES_INK_COLOR = register("inkwellChangesInkColor", false);
    public static final Key<BooleanRule> INK_TANK_INK_REGENERATION = register("inkTankInkRegeneration", true);
    public static final Key<BooleanRule> LEAVE_SQUID_FORM_ON_ENEMY_INK = register("leaveSquidFormOnEnemyInk", true);

    public static final Key<BooleanRule> SPLATFEST_BAND_MUST_BE_HELD = register("splatfestBandMustBeHeld", true, (server, rule) -> {
        for (ServerPlayerEntity player : PlayerLookup.all(server)) {
            ((PlayerEntityAccess) player).updateSplatfestBand();
        }
    });

    public static final Key<BooleanRule> UNIVERSAL_INK = register("universalInk", false, (server, rule) -> {
        for (ServerPlayerEntity player : PlayerLookup.all(server)) {
            updateUniversalInk(player, rule);
        }
    });

    public static final Key<BooleanRule> ENEMY_INK_SLOWNESS = register("enemyInkSlowness", true, (server, rule) -> {
        for (ServerPlayerEntity player : PlayerLookup.all(server)) {
            updateEnemyInkSlowness(player, rule);
        }
    });

    public static final Key<BooleanRule> DAMAGE_ON_ENEMY_INK = register("damageOnEnemyInk", true);
    public static final Key<BooleanRule> DAMAGE_ON_ENEMY_INK_ONLY_IN_SQUID_FORM = register("damageOnEnemyInk/onlyInSquidForm", false);
    public static final Key<BooleanRule> DAMAGE_ON_ENEMY_INK_SCALES_TO_MAX_HEALTH = register("damageOnEnemyInk/scalesToMaxHealth", true);
    public static final Key<BooleanRule> DAMAGE_ON_ENEMY_INK_CAN_KILL = register("damageOnEnemyInk/canKill", false);

    public static final Key<BooleanRule> DAMAGE_WHEN_WET = register("damageWhenWet", true);
    public static final Key<BooleanRule> DAMAGE_WHEN_WET_ONLY_IN_SQUID_FORM = register("damageWhenWet/onlyInSquidForm", false);
    public static final Key<BooleanRule> DAMAGE_WHEN_WET_SCALES_TO_MAX_HEALTH = register("damageWhenWet/scalesToMaxHealth", true);
    public static final Key<BooleanRule> DAMAGE_WHEN_WET_CAN_KILL = register("damageWhenWet/canKill", true);
    public static final Key<BooleanRule> DAMAGE_WHEN_WET_INSTANT_KILL = register("damageWhenWet/instantKill", true);

    public static final Key<BooleanRule> ACCURATE_REGENERATION = register("accurateRegeneration", false);
    public static final Key<BooleanRule> ACCURATE_REGENERATION_ONLY_IN_SQUID_FORM = register("accurateRegeneration/onlyInSquidForm", false);
    public static final Key<BooleanRule> ACCURATE_REGENERATION_SCALES_TO_MAX_HEALTH = register("accurateRegeneration/scalesToMaxHealth", true);

    public static final Key<BooleanRule> REGENERATE_WHEN_SUBMERGED = register("regenerateWhenSubmerged", true);
    public static final Key<BooleanRule> REGENERATE_WHEN_SUBMERGED_SCALES_TO_MAX_HEALTH = register("regenerateWhenSubmerged/scalesToMaxHealth", true);

    private static <T extends Rule<T>> Key<T> register(String id, Type<T> type) {
        return GameRuleRegistry.register(getId(id), SplatcraftGameRules.CATEGORY, type);
    }

    private static Key<BooleanRule> register(String id, boolean defaultValue, BiConsumer<MinecraftServer, BooleanRule> onChanged) {
        return register(id, GameRuleFactory.createBooleanRule(defaultValue, onChanged));
    }

    private static Key<BooleanRule> register(String id, boolean defaultValue) {
        return register(id, defaultValue, (s, r) -> {});
    }

    private static String getId(String id) {
        return Splatcraft.MOD_ID + NAMESPACE_SEPARATOR + id;
    }

    public static boolean gameRule(World world, Key<BooleanRule> rule) {
        return world.getGameRules().getBoolean(rule);
    }
}
