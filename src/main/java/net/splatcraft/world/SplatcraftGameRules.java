package net.splatcraft.world;

import net.fabricmc.fabric.api.gamerule.v1.CustomGameRuleCategory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.splatcraft.Splatcraft;

import static net.minecraft.world.GameRules.*;

public class SplatcraftGameRules {
    public static final CustomGameRuleCategory CATEGORY = new CustomGameRuleCategory(
        new Identifier(Splatcraft.MOD_ID, "category"),
        new TranslatableText("gamerule.%s.category".formatted(Splatcraft.MOD_ID))
            .formatted(Formatting.YELLOW, Formatting.BOLD)
    );

    public static final Key<BooleanRule> SPLATFEST_BAND_MUST_BE_HELD = register("splatfestBandMustBeHeld", true);
    public static final Key<BooleanRule> INKWELL_CHANGES_INK_COLOR = register("inkwellChangesInkColor", false);
    public static final Key<BooleanRule> HURT_INK_SQUIDS_IN_WATER = register("hurtInkSquidsInWater", true);
    public static final Key<BooleanRule> HURT_INK_SQUIDS_ON_ENEMY_INK = register("hurtInkSquidsOnEnemyInk", true);
    public static final Key<BooleanRule> INK_TANK_INK_REGENERATION = register("inkTankInkRegeneration", true);

    private static <T extends Rule<T>> Key<T> register(String id, Type<T> type) {
        return GameRuleRegistry.register("%s:%s".formatted(Splatcraft.MOD_ID, id), SplatcraftGameRules.CATEGORY, type);
    }

    private static Key<BooleanRule> register(String id, boolean defaultValue) {
        return register(id, GameRuleFactory.createBooleanRule(defaultValue));
    }
}
