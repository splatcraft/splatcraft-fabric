package com.cibernet.splatcraft.init;

import com.cibernet.splatcraft.Splatcraft;
import net.fabricmc.fabric.api.gamerule.v1.CustomGameRuleCategory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class SplatcraftGameRules {
    public static CustomGameRuleCategory CATEGORY = new CustomGameRuleCategory(new Identifier(Splatcraft.MOD_ID, "category"), new TranslatableText("gamerule." + Splatcraft.MOD_ID + ".category").formatted(Formatting.BOLD).formatted(Formatting.YELLOW));

    public static GameRules.Key<GameRules.BooleanRule> INK_DECAY = register("inkDecay", GameRuleFactory.createBooleanRule(true));
    public static GameRules.Key<GameRules.BooleanRule> COLORED_PLAYER_NAMES = register("coloredPlayerNames", GameRuleFactory.createBooleanRule(false));
    public static GameRules.Key<GameRules.BooleanRule> KEEP_MATCH_ITEMS = register("keepMatchItems", GameRuleFactory.createBooleanRule(false));
    public static GameRules.Key<GameRules.BooleanRule> UNIVERSAL_INK = register("universalInk", GameRuleFactory.createBooleanRule(false));
    public static GameRules.Key<GameRules.BooleanRule> DROP_CRATE_LOOT = register("dropCrateLoot", GameRuleFactory.createBooleanRule(false));
    public static GameRules.Key<GameRules.BooleanRule> WATER_DAMAGE = register("waterDamage", GameRuleFactory.createBooleanRule(false));
    public static GameRules.Key<GameRules.BooleanRule> REQUIRE_INK_TANK = register("requireInkTank", GameRuleFactory.createBooleanRule(true));
    public static GameRules.Key<GameRules.BooleanRule> INK_MOB_DAMAGE = register("inkMobDamage", GameRuleFactory.createBooleanRule(false));
    public static GameRules.Key<GameRules.BooleanRule> INK_FRIENDLY_FIRE = register("inkFriendlyFire", GameRuleFactory.createBooleanRule(false));

    public SplatcraftGameRules() {}

    public static boolean getBoolean(World world, GameRules.Key<GameRules.BooleanRule> rule) {
        return world.getGameRules().getBoolean(rule);
    }

    private static <T extends GameRules.Rule<T>> GameRules.Key<T> register(String id, GameRules.Type<T> type) {
        return GameRuleRegistry.register(Splatcraft.MOD_ID + "." + id, CATEGORY, type);
    }
}
