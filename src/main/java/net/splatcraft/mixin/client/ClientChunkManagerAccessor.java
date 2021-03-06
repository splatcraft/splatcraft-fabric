package net.splatcraft.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientChunkManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Environment(EnvType.CLIENT)
@Mixin(ClientChunkManager.class)
public interface ClientChunkManagerAccessor {
    @Accessor ClientChunkManager.ClientChunkMap getChunks();
}
