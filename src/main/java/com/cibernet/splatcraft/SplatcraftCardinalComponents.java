package com.cibernet.splatcraft;

import com.cibernet.splatcraft.component.PlayerDataComponent;
import com.cibernet.splatcraft.init.SplatcraftComponents;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import nerdhub.cardinal.components.api.util.RespawnCopyStrategy;

public class SplatcraftCardinalComponents implements EntityComponentInitializer {
    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        new SplatcraftComponents();
        registry.registerForPlayers(SplatcraftComponents.PLAYER_DATA, PlayerDataComponent::new, RespawnCopyStrategy.ALWAYS_COPY);
    }
}
