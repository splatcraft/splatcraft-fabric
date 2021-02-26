package com.cibernet.splatcraft.init;

import com.cibernet.splatcraft.Splatcraft;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

@SuppressWarnings("unused")
public class SplatcraftSoundEvents {
    public static SoundEvent SQUID_TRANSFORM = register("squid_transform");
    public static SoundEvent SQUID_REVERT = register("squid_revert");
    public static SoundEvent INK_SUBMERGE = register("ink_submerge");
    public static SoundEvent INK_SURFACE = register("ink_surface");
    public static SoundEvent NO_INK = register("no_ink");
    public static SoundEvent NO_INK_SUB = register("no_ink_sub");
    public static SoundEvent SHOOTER_FIRING = register("shooter_firing");
    public static SoundEvent BLASTER_FIRING = register("blaster_firing");
    public static SoundEvent BLASTER_EXPLOSION = register("blaster_explosion");
    public static SoundEvent ROLLER_FLING = register("roller_fling");
    public static SoundEvent ROLLER_ROLL = register("roller_roll");
    public static SoundEvent CHARGER_CHARGE = register("charger_charge");
    public static SoundEvent CHARGER_READY = register("charger_ready");
    public static SoundEvent CHARGER_SHOT = register("charger_shot");
    public static SoundEvent DUALIE_FIRING = register("dualie_firing");
    public static SoundEvent DUALIE_DODGE = register("dualie_dodge");
    public static SoundEvent SLOSHER_SHOT = register("slosher_shot");
    public static SoundEvent SUB_THROW = register("sub_throw");
    public static SoundEvent SUB_DETONATING = register("sub_detonating");
    public static SoundEvent SUB_DETONATE = register("sub_detonate");
    public static SoundEvent REMOTE_USE = register("remote_use");

    public SplatcraftSoundEvents() {}

    private static SoundEvent register(String id) {
        Identifier identifier = new Identifier(Splatcraft.MOD_ID, id);
        return Registry.register(Registry.SOUND_EVENT, identifier, new SoundEvent(identifier));
    }
}
