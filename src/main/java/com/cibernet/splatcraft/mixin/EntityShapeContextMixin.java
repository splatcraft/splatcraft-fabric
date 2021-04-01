package com.cibernet.splatcraft.mixin;

import com.cibernet.splatcraft.entity.EntityAccessShapeContext;
import me.jellysquid.mods.lithium.common.block.LithiumEntityShapeContext;
import net.minecraft.block.EntityShapeContext;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ EntityShapeContext.class, LithiumEntityShapeContext.class })
public class EntityShapeContextMixin implements EntityAccessShapeContext {
    private Entity splatcraft_entity;

    @Inject(method = "<init>(Lnet/minecraft/entity/Entity;)V", at = @At("RETURN"))
    private void onInit(Entity entity, CallbackInfo info) {
        this.splatcraft_entity = entity;
    }

    @Override
    @Nullable
    public Entity splatcraft_getEntity() {
        return splatcraft_entity;
    }
}
