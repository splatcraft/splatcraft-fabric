package net.splatcraft.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.splatcraft.component.PlayerDataComponent;
import net.splatcraft.tag.SplatcraftEntityTypeTags;

public class Util {
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
