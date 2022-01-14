package net.splatcraft.mixin;

import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.util.collection.Int2ObjectBiMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TrackedDataHandlerRegistry.class)
public interface TrackedDataHandlerRegistryAccessor {
    @Accessor
    static Int2ObjectBiMap<TrackedDataHandler<?>> getDATA_HANDLERS() {
        throw new AssertionError();
    }
}
