package net.splatcraft.component;

import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;

public class SplatcraftCardinalComponents implements EntityComponentInitializer {
    private static SplatcraftCardinalComponents instance = null;

    public SplatcraftCardinalComponents() {
        instance = this;
    }

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(SplatcraftComponents.PLAYER_DATA, PlayerDataComponent::new, RespawnCopyStrategy.ALWAYS_COPY);
    }

    public static SplatcraftCardinalComponents getInstance() {
        return instance;
    }
}
