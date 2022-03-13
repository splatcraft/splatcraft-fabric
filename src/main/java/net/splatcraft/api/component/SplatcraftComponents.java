package net.splatcraft.api.component;

import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import net.minecraft.util.Identifier;
import net.splatcraft.api.Splatcraft;

public interface SplatcraftComponents {
    ComponentKey<PlayerDataComponent> PLAYER_DATA = register("player_data", PlayerDataComponent.class);

    private static <C extends Component> ComponentKey<C> register(String id, Class<C> clazz) {
        return ComponentRegistry.getOrCreate(new Identifier(Splatcraft.MOD_ID, id), clazz);
    }
}
