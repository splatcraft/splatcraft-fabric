package net.splatcraft.util;

import me.shedaniel.math.Color;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.splatcraft.component.PlayerDataComponent;
import net.splatcraft.tag.SplatcraftEntityTypeTags;

public class SplatcraftUtil {

    /**
     * @return if an entity can pass through a block due to ink abilities
     */
    public static boolean inkPassesBlock(Entity entity) {
        if (entity instanceof PlayerEntity player) {
            PlayerDataComponent data = PlayerDataComponent.get(player);
            return data.isSquid();
        } else if (entity != null) {
            return SplatcraftEntityTypeTags.INK_PASSABLES.contains(entity.getType());
        }

        return false;
    }

    /**
     * @return whether a player can enter squid form
     */
    public static boolean canSquid(PlayerEntity player) {
        return !player.hasVehicle();
    }

    public static float[] mathColorToRGB(Color color) {
        return new float[]{ color.getRed(), color.getGreen(), color.getBlue() };
    }
}
