package net.splatcraft.api.sound;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.splatcraft.api.Splatcraft;

public interface SplatcraftSoundEvents {
    SoundEvent ITEM_SHOOTER_SHOOT = shooter("shoot");
    private static SoundEvent shooter(String id) { return item("shooter.%s".formatted(id)); }

    private static SoundEvent item(String id) { return register("item.%s".formatted(id)); }

    private static SoundEvent register(String id) {
        Identifier identifier = new Identifier(Splatcraft.MOD_ID, id);
        return Registry.register(Registry.SOUND_EVENT, identifier, new SoundEvent(identifier));
    }
}
