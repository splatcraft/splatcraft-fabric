package net.splatcraft.client.util;

import com.google.common.collect.Lists;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3f;
import net.splatcraft.Splatcraft;
import net.splatcraft.client.config.ClientConfig;
import net.splatcraft.component.PlayerDataComponent;
import net.splatcraft.config.option.ColorOption;
import net.splatcraft.inkcolor.InkColor;
import net.splatcraft.inkcolor.InkColors;
import net.splatcraft.inkcolor.Inkable;
import net.splatcraft.util.Color;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static net.splatcraft.util.SplatcraftConstants.DEFAULT_INK_COLOR_DYE;

@Environment(EnvType.CLIENT)
public class ClientUtil {
    public static final List<Integer> WHITE_COLORS = Util.make(() -> {
        float[] dyeWhiteColors = DEFAULT_INK_COLOR_DYE.getColorComponents();
        int dyeWhite = Color.ofRGB(dyeWhiteColors[0], dyeWhiteColors[1], dyeWhiteColors[2]).getColor();
        int pureWhite = InkColors.PURE_WHITE.getDecimalColor();
        return Lists.newArrayList(dyeWhite, pureWhite);
    });

    /**
     * @return an {@link Optional} of {@link ColorOption},
     *         containing the friendly or hostile instance
     *         dependent on the client's ink color, or
     *         empty if color lock is disabled
     */
    public static Optional<ColorOption> getColorOption(InkColor clientInkColor) {
        if (WHITE_COLORS.contains(clientInkColor.getDecimalColor()) || !ClientConfig.INSTANCE.colorLock.getValue()) {
            return Optional.empty();
        }

        MinecraftClient client = MinecraftClient.getInstance();
        PlayerDataComponent data = PlayerDataComponent.get(client.player);

        ColorOption colorOption = data.getInkColor().equals(clientInkColor)
            ? ClientConfig.INSTANCE.colorLockFriendly
            : ClientConfig.INSTANCE.colorLockHostile;

        return Optional.of(colorOption);
    }

    // cache for getVectorColor
    public static final Function<ColorOption, Vec3f> COLOR_OPTION_TO_VEC3F = Util.memoize(o -> decimalToRGB(o.getValue()));

    /**
     * @return a {@link Vec3f} containing colors, dependent
     *         on color lock
     */
    public static Vec3f getVectorColor(InkColor clientInkColor) {
        Optional<ColorOption> colorOption = getColorOption(clientInkColor);
        return colorOption.isPresent() ? COLOR_OPTION_TO_VEC3F.apply(colorOption.get()) : clientInkColor.getVectorColor();
    }

    /**
     * @return a decimal color dependent on color lock
     */
    public static int getDecimalColor(InkColor clientInkColor) {
        Optional<ColorOption> colorOption = getColorOption(clientInkColor);
        return colorOption.isPresent() ? colorOption.get().getValue() : clientInkColor.getDecimalColor();
    }

    /**
     * @return a {@link Color} dependent on color lock
     */
    public static Color getColor(InkColor clientInkColor) {
        return Color.of(getDecimalColor(clientInkColor));
    }

    @Nullable
    public static InkColor getTargetedBlockInkColor() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.crosshairTarget != null && client.world != null) {
            HitResult target = client.crosshairTarget;
            if (target instanceof BlockHitResult hit) {
                BlockPos pos = new BlockPos(hit.getPos()).offset(hit.getSide(), -1);
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
        return new EntityRendererFactory.Context(
            client.getEntityRenderDispatcher(),
            client.getItemRenderer(),
            client.getResourceManager(),
            client.getEntityModelLoader(),
            client.textRenderer
        );
    }
}
