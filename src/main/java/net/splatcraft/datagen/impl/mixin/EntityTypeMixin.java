package net.splatcraft.datagen.impl.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import net.splatcraft.datagen.impl.ObjectLootTableAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EntityType.class)
public abstract class EntityTypeMixin implements ObjectLootTableAccess {
    @Shadow public abstract Identifier getLootTableId();

    @Override
    public Identifier access_getLootTableId() {
        return this.getLootTableId();
    }
}
