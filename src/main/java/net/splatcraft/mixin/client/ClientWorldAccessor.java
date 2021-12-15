package net.splatcraft.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Environment(EnvType.CLIENT)
@Mixin(ClientWorld.class)
public interface ClientWorldAccessor {
    @Accessor("BLOCK_MARKER_ITEMS")
    static Set<Item> getBlockMarkerItems() {
        throw new AssertionError();
    }

    @Mutable
    @Accessor("BLOCK_MARKER_ITEMS")
    static void setBlockMarkerItems(Set<Item> items) {
        throw new AssertionError();
    }
}
