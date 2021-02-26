package com.cibernet.splatcraft.init;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.component.PlayerDataComponent;
import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import net.minecraft.util.Identifier;

public class SplatcraftComponents {
    public static final ComponentKey<PlayerDataComponent> PLAYER_DATA = register("player_data", PlayerDataComponent.class);

    public SplatcraftComponents() {}

    public static <C extends Component> ComponentKey<C> register(String id, Class<C> componentClass) {
        return ComponentRegistry.getOrCreate(new Identifier(Splatcraft.MOD_ID, id), componentClass);
    }
}
