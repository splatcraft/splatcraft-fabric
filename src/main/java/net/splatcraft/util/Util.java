package net.splatcraft.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.player.PlayerEntity;
import net.splatcraft.component.PlayerDataComponent;
import net.splatcraft.tag.SplatcraftEntityTypeTags;

public class Util {
    public static final EntityDimensions SQUID_FORM_SUBMERGED_DIMENSIONS = EntityDimensions.fixed(0.5f, 0.5f);
    public static final EntityDimensions SQUID_FORM_DIMENSIONS = EntityDimensions.fixed(0.5f, 1.0f);

    public static boolean inkPassesBlock(Entity entity) {
        if (entity instanceof PlayerEntity player) {
            PlayerDataComponent data = PlayerDataComponent.get(player);
            return data.isSquid();
        } else if (entity != null) {
            return SplatcraftEntityTypeTags.INK_PASSABLES.contains(entity.getType());
        }

        return false;
    }

    public static boolean canSquid(PlayerEntity player) {
        return !player.hasVehicle();
    }
}
