package net.splatcraft.api.client.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;
import net.moddingplayground.frame.api.config.v0.option.ColorOption;
import net.splatcraft.api.Splatcraft;
import net.splatcraft.api.component.PlayerDataComponent;
import net.splatcraft.api.inkcolor.InkColor;
import net.splatcraft.api.inkcolor.Inkable;
import net.splatcraft.api.util.Color;
import net.splatcraft.impl.client.config.ClientConfig;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@Environment(EnvType.CLIENT)
public class ClientUtil {
    /**
     * @return an {@link Optional} of {@link ColorOption},
     *         containing the friendly or hostile instance
     *         dependent on the client's ink color, or
     *         empty if color lock is disabled
     */
    public static Optional<ColorOption> getColorOption(InkColor toMatch) {
        if (!ClientConfig.INSTANCE.colorLock.getValue()) return Optional.empty();

        MinecraftClient client = MinecraftClient.getInstance();
        PlayerDataComponent data = PlayerDataComponent.get(client.player);

        ColorOption colorOption = data.getInkColor().equals(toMatch)
            ? ClientConfig.INSTANCE.colorLockFriendly
            : ClientConfig.INSTANCE.colorLockHostile;

        return Optional.of(colorOption);
    }

    /**
     * @return a {@link Vec3f} containing colors, dependent
     *         on color lock
     */
    public static Vec3f getVectorColor(InkColor toMatch) {
        Optional<ColorOption> colorOption = getColorOption(toMatch);
        return colorOption.map(o -> decimalToRGB(o.getValue())).orElseGet(toMatch::getVectorColor);
    }

    /**
     * @return a decimal color dependent on color lock
     */
    public static int getDecimalColor(InkColor toMatch) {
        Optional<ColorOption> colorOption = getColorOption(toMatch);
        return colorOption.isPresent() ? colorOption.get().getValue() : toMatch.getDecimalColor();
    }

    /**
     * @return a {@link Color} dependent on color lock
     */
    public static Color getColor(InkColor toMatch) {
        return Color.of(getDecimalColor(toMatch));
    }

    @Nullable
    public static InkColor getTargetedInkColor() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.crosshairTarget != null && client.world != null) {
            HitResult target = client.crosshairTarget;
            if (target instanceof BlockHitResult hit) {
                BlockPos hitPos = new BlockPos(hit.getPos());
                Direction side = hit.getSide();
                BlockPos pos = side != Direction.DOWN && side != Direction.NORTH ? hitPos.offset(side, -1) : hitPos;
                if (client.world.getBlockEntity(pos) instanceof Inkable inkable) return inkable.getInkColor();
            } else if (target instanceof EntityHitResult hit) {
                if (hit.getEntity() instanceof Inkable inkable) return inkable.getInkColor();
            }
        }

        return null;
    }

    public static Vec3f decimalToRGB(int color) {
        float red   = ((color >> 16) & 0xFF) / 255.0f;
        float green = ((color >> 8)  & 0xFF) / 255.0f;
        float blue  = ( color        & 0xFF) / 255.0f;
        return new Vec3f(red, green, blue);
    }

    public static Identifier texture(String s) {
        return new Identifier(Splatcraft.MOD_ID, "textures/%s.png".formatted(s));
    }

    public static Identifier entityTexture(String s) {
        return texture("entity/%s".formatted(s));
    }

    public static EntityRendererFactory.Context entityRendererFactoryContext() {
        MinecraftClient client = MinecraftClient.getInstance();
        EntityRenderDispatcher entityRenderDispatcher = client.getEntityRenderDispatcher();
        return new EntityRendererFactory.Context(
            entityRenderDispatcher,
            client.getItemRenderer(),
            client.getBlockRenderManager(),
            entityRenderDispatcher.getHeldItemRenderer(),
            client.getResourceManager(),
            client.getEntityModelLoader(),
            client.textRenderer
        );
    }
}
