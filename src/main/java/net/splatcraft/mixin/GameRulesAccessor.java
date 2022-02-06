package net.splatcraft.mixin;

import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(GameRules.class)
public interface GameRulesAccessor {
    @Accessor static Map<GameRules.Key<?>, GameRules.Type<?>> getRULE_TYPES() { throw new AssertionError(); }
}
