package net.splatcraft.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.sound.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LivingEntity.class)
public interface LivingEntityInvoker {
    @Invoker("updatePotionVisibility")
    void invoke_updatePotionVisibility();

    @Invoker("getFallSound")
    SoundEvent invoke_getFallSound(int distance);
}
