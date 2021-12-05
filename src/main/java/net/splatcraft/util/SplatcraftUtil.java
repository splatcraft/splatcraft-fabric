package net.splatcraft.util;

import me.shedaniel.math.Color;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.splatcraft.Splatcraft;
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

    /**
     * @return whether a player can submerge in ink
     */
    @SuppressWarnings({ "PointlessBooleanExpression", "ConstantConditions" })
    public static boolean canSubmerge(PlayerEntity player) {
        PlayerDataComponent data = PlayerDataComponent.get(player);
        return !player.isSpectator() && (data.isSquid() && false /*(canSwim(player) || canClimb(player))*/);
    }

    public static float[] mathColorToRGB(Color color) {
        return new float[]{ color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f };
    }

    @Environment(EnvType.CLIENT)
    public static Identifier texture(String s) {
        return new Identifier(Splatcraft.MOD_ID, "textures/%s.png".formatted(s));
    }

    @Environment(EnvType.CLIENT)
    public static Identifier entityTexture(String s) {
        return texture("entity/%s".formatted(s));
    }

    @Environment(EnvType.CLIENT)
    public static EntityRendererFactory.Context entityRendererFactoryContext() {
        MinecraftClient client = MinecraftClient.getInstance();
        return new EntityRendererFactory.Context(
            client.getEntityRenderDispatcher(),
            client.getItemRenderer(),
            client.getResourceManager(),
            client.getEntityModelLoader(),
            client.textRenderer
        );
    }
}
