package net.splatcraft.world;

import net.fabricmc.fabric.api.gamerule.v1.CustomGameRuleCategory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.splatcraft.Splatcraft;

import static net.minecraft.world.GameRules.BooleanRule;
import static net.minecraft.world.GameRules.Key;
import static net.minecraft.world.GameRules.Rule;
import static net.minecraft.world.GameRules.Type;

public class SplatcraftGameRules {
    public static final CustomGameRuleCategory CATEGORY = new CustomGameRuleCategory(
        new Identifier(Splatcraft.MOD_ID, "category"),
        new TranslatableText("gamerule.%s.category".formatted(Splatcraft.MOD_ID))
            .formatted(Formatting.YELLOW, Formatting.BOLD)
    );

    public static final Key<BooleanRule> SPLATFEST_BAND_MUST_BE_HELD = register("splatfestBandMustBeHeld", true);
    public static final Key<BooleanRule> INKWELL_CHANGES_INK_COLOR = register("inkwellChangesInkColor", false);
    public static final Key<BooleanRule> INK_TANK_INK_REGENERATION = register("inkTankInkRegeneration", true);
    public static final Key<BooleanRule> ENEMY_INK_SLOWS_DOWN = register("enemyInkSlowsDown", true);
    public static final Key<BooleanRule> ENEMY_INK_DAMAGE_ENABLED = register("damageOnEnemyInk", true);
    public static final Key<BooleanRule> ENEMY_INK_DAMAGE_SCALES_TO_MAX_HEALTH = register("damageOnEnemyInk/scalesToMaxHealth", true);
    public static final Key<BooleanRule> ENEMY_INK_DAMAGE_ONLY_IN_SQUID_FORM = register("damageOnEnemyInk/onlyInSquidForm", false);
    public static final Key<BooleanRule> WATER_DAMAGE_ENABLED = register("waterDamage", true);
    public static final Key<BooleanRule> WATER_DAMAGE_ONLY_IN_SQUID_FORM = register("waterDamage/onlyInSquidForm", false);
    public static final Key<BooleanRule> WATER_DAMAGE_SCALES_TO_MAX_HEALTH = register("waterDamage/scalesToMaxHealth", true);
    public static final Key<BooleanRule> WATER_DAMAGE_KILLS_INSTANTLY = register("waterDamage/instantKill", true);
    public static final Key<BooleanRule> SPLATOON_HEALTH_REGENERATION = register("splatoonHealthRegeneration", true);
    public static final Key<BooleanRule> HEALTH_REGENERATION_SCALES_TO_MAX_HEALTH = register("splatoonHealthRegeneration/scalesToMaxHealth", true);
    public static final Key<BooleanRule> REGENERATE_HEALTH_ONLY_IN_SQUID_FORM = register("splatoonHealthRegeneration/onlyInSquidForm", false);

    private static <T extends Rule<T>> Key<T> register(String id, Type<T> type) {
        return GameRuleRegistry.register("%s:%s".formatted(Splatcraft.MOD_ID, id), SplatcraftGameRules.CATEGORY, type);
    }

    private static Key<BooleanRule> register(String id, boolean defaultValue) {
        return register(id, GameRuleFactory.createBooleanRule(defaultValue));
    }
}
