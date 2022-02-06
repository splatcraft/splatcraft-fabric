package net.splatcraft.mixin;

import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GameRules.Rule.class)
public interface GameRulesRuleAccessor {
    @Accessor <T extends GameRules.Rule<T>> GameRules.Type<T> getType();
}
