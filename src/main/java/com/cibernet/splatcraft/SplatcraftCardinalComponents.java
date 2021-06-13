package com.cibernet.splatcraft;

import com.cibernet.splatcraft.component.LazyPlayerDataComponent;
import com.cibernet.splatcraft.component.PlayerDataComponent;
import com.cibernet.splatcraft.component.SplatcraftComponents;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;

public class SplatcraftCardinalComponents implements EntityComponentInitializer {
    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        new SplatcraftComponents();

        registry.registerForPlayers(SplatcraftComponents.PLAYER_DATA, PlayerDataComponent::new, RespawnCopyStrategy.ALWAYS_COPY);
        registry.registerForPlayers(SplatcraftComponents.LAZY_PLAYER_DATA, LazyPlayerDataComponent::new, RespawnCopyStrategy.NEVER_COPY);
    }
}
