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
    @Accessor
    static Set<Item> getBLOCK_MARKER_ITEMS() {
        throw new AssertionError();
    }

    @Mutable @Accessor
    static void setBLOCK_MARKER_ITEMS(Set<Item> items) {
        throw new AssertionError();
    }
}
