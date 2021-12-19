package net.splatcraft.entity;

import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.splatcraft.Splatcraft;

public class SplatcraftAttributes {
    public static final EntityAttribute INK_SWIM_SPEED = register(
        "ink_swim_speed",
        new ClampedEntityAttribute(translation("ink_swim_speed"), 0.9f, 0.0d, 1024.0d)
            .setTracked(true)
    );
    public static final EntityAttribute INK_JUMP_FORCE = register(
        "ink_jump_force",
        new ClampedEntityAttribute(translation("ink_jump_force"), 0.9f, 0.0d, 1024.0d)
            .setTracked(true)
    );

    private static EntityAttribute register(String id, EntityAttribute entityAttribute) {
        return Registry.register(Registry.ATTRIBUTE, new Identifier(Splatcraft.MOD_ID, id), entityAttribute);
    }
    private static String translation(String id) {
        return "attribute.%s.%s".formatted(Splatcraft.MOD_ID, id);
    }
}
