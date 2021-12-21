package net.splatcraft.util;

import com.google.common.collect.Lists;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.splatcraft.Splatcraft;
import net.splatcraft.client.config.ClientConfig;
import net.splatcraft.component.PlayerDataComponent;
import net.splatcraft.config.CommonConfig;
import net.splatcraft.config.option.ColorOption;
import net.splatcraft.entity.InkableCaster;
import net.splatcraft.entity.SplatcraftAttributes;
import net.splatcraft.inkcolor.InkColor;
import net.splatcraft.inkcolor.InkColors;
import net.splatcraft.inkcolor.InkType;
import net.splatcraft.inkcolor.Inkable;
import net.splatcraft.item.SplatcraftItems;
import net.splatcraft.tag.SplatcraftBlockTags;
import net.splatcraft.tag.SplatcraftEntityTypeTags;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import static net.splatcraft.network.NetworkingCommon.inkSplashParticleAtPos;
import static net.splatcraft.network.NetworkingCommon.inkSquidSoulParticleAtPos;
import static net.splatcraft.util.SplatcraftConstants.*;

public class SplatcraftUtil {
    /**
     * @return if an entity can pass through a block due to ink abilities
     */
    public static boolean doesInkPassBlock(Entity entity) {
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
    public static boolean canEnterSquidForm(PlayerEntity player) {
        return !player.hasVehicle();
    }

    /**
     * @return whether a player can submerge in ink
     */
    public static boolean canSubmergeInInk(PlayerEntity player) {
        PlayerDataComponent data = PlayerDataComponent.get(player);
        if (!data.isSquid()) return false;
        return !player.isSpectator() && (canSwimInInk(player) /*|| canClimb(player)*/);
    }

    public static boolean canSwimInInk(PlayerEntity player) {
        return isOnOwnInk(player);
    }

    public static boolean isOnOwnInk(Entity entity) {
        if (!(entity instanceof Inkable inkable)) return false;
        return entity.world.getBlockEntity(entity.getLandingPos()) instanceof Inkable inkableBlock
            && inkableBlock.getInkColor().equals(inkable.getInkColor());
    }

    public static boolean isOnEnemyInk(Entity entity) {
        if (!(entity instanceof Inkable inkableEntity)) return false;

        BlockPos pos = entity.getLandingPos();
        if (CommonConfig.INSTANCE.inkwellChangesInkColor.getValue()) {
            BlockState state = entity.world.getBlockState(pos);
            if (SplatcraftBlockTags.INK_COLOR_CHANGERS.contains(state.getBlock())) return false;
        }
        return entity.world.getBlockEntity(pos) instanceof Inkable inkable && !inkable.getInkColor().equals(inkableEntity.getInkColor());
    }

    public static boolean isOnInk(Entity entity) {
        return entity.world.getBlockEntity(entity.getLandingPos()) instanceof Inkable;
    }

    public static Optional<Float> getModifiedMovementSpeed(PlayerEntity player, float base) {
        if (canSwimInInk(player)) {
            return Optional.of(base * ((float) player.getAttributeValue(SplatcraftAttributes.INK_SWIM_SPEED) * 10));
        }

        return Optional.empty();
    }

    public static boolean canInteractWithWorld(PlayerEntity player) {
        PlayerDataComponent data = PlayerDataComponent.get(player);
        return !data.isSquid();
    }

    private static final Hand[] HANDS = Hand.values();
    private static final Set<Item> SPLATFEST_BAND_SET = Set.of(SplatcraftItems.SPLATFEST_BAND);

    /**
     * Checks a {@link PlayerEntity}'s inventory for a
     * {@link SplatcraftItems#SPLATFEST_BAND}, with
     * respect to configuration.
     *
     * @return whether or not the method had any effect
     */
    public static boolean refreshSplatfestBand(PlayerEntity player) {
        PlayerDataComponent data = PlayerDataComponent.get(player);

        if (CommonConfig.INSTANCE.splatfestBandMustBeHeld.getValue()) {
            for (Hand hand : HANDS) {
                if (player.getStackInHand(hand).isOf(SplatcraftItems.SPLATFEST_BAND)) {
                    return data.setHasSplatfestBand(true);
                }
            }
        } else {
            if (player.getInventory().containsAny(SPLATFEST_BAND_SET)) {
                return data.setHasSplatfestBand(true);
            }
        }

        return data.setHasSplatfestBand(false);
    }

    public static ScreenHandlerListener createSplatfestBandRefreshScreenHandlerListener(ServerPlayerEntity player) {
        return new ScreenHandlerListener() {
            @Override
            public void onSlotUpdate(ScreenHandler handler, int slotId, ItemStack stack) {
                refreshSplatfestBand(player);
            }

            @Override
            public void onPropertyUpdate(ScreenHandler handler, int property, int value) {}
        };
    }

    public static InkType getInkType(PlayerEntity player) {
        PlayerDataComponent data = PlayerDataComponent.get(player);
        return data.hasSplatfestBand() ? InkType.GLOWING : InkType.NORMAL;
    }

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
    @Environment(EnvType.CLIENT)
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
    @Environment(EnvType.CLIENT)
    public static Vec3f getVectorColor(InkColor clientInkColor) {
        Optional<ColorOption> colorOption = getColorOption(clientInkColor);
        return colorOption.isPresent() ? COLOR_OPTION_TO_VEC3F.apply(colorOption.get()) : clientInkColor.getVectorColor();
    }

    /**
     * @return a decimal color dependent on color lock
     */
    @Environment(EnvType.CLIENT)
    public static int getDecimalColor(InkColor clientInkColor) {
        Optional<ColorOption> colorOption = getColorOption(clientInkColor);
        return colorOption.isPresent() ? colorOption.get().getValue() : clientInkColor.getDecimalColor();
    }

    /**
     * @return a {@link Color} dependent on color lock
     */
    @Environment(EnvType.CLIENT)
    public static Color getColor(InkColor clientInkColor) {
        return Color.of(getDecimalColor(clientInkColor));
    }

    @Environment(EnvType.CLIENT)
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

    public static InkColor getInkColorFromStack(ItemStack stack) {
        NbtCompound nbt = stack.getItem() instanceof BlockItem
            ? stack.getSubNbt(NBT_BLOCK_ENTITY_TAG)
            : stack.getNbt();
        if (nbt == null) return InkColors._DEFAULT;
        return InkColor.fromString(nbt.getString(NBT_INK_COLOR));
    }

    public static ItemStack setInkColorOnStack(ItemStack stack, InkColor inkColor) {
        if (inkColor.equals(getInkColorFromStack(stack))) return stack;
        NbtCompound nbt = stack.getItem() instanceof BlockItem
            ? stack.getOrCreateSubNbt(NBT_BLOCK_ENTITY_TAG)
            : stack.getOrCreateNbt();
        nbt.putString(NBT_INK_COLOR, inkColor.getId().toString());
        return stack;
    }

    /**
     * A movement tick for entities of {@link Inkable}. Does not run client-side.
     */
    public static <T extends Entity & Inkable> void tickMovementInkableEntity(T entity, Vec3d movementInput) {
        if (movementInput.length() > 0.08d) {
            Vec3d pos = new Vec3d(entity.getX(), entity.getLandingPos().getY() + 1, entity.getZ());
            float scale = 0.75f;
            if (entity instanceof PlayerEntity player) {
                PlayerDataComponent data = PlayerDataComponent.get(player);
                if (data.isSubmerged()) inkSplashParticleAtPos(entity, pos, scale);
            } else if (isOnOwnInk(entity)) inkSplashParticleAtPos(entity, pos, scale);
        }

        if (CommonConfig.INSTANCE.inkwellChangesInkColor.getValue() && entity.isOnGround()) {
            BlockEntity blockEntity = entity.world.getBlockEntity(entity.getLandingPos());
            if (blockEntity != null && SplatcraftBlockTags.INK_COLOR_CHANGERS.contains(blockEntity.getCachedState().getBlock())) {
                if (blockEntity instanceof Inkable inkable) {
                    InkColor inkColor = inkable.getInkColor();
                    if (entity instanceof PlayerEntity player) {
                        PlayerDataComponent data = PlayerDataComponent.get(player);
                        if (data.isSquid()) entity.setInkColor(inkColor);
                    } else entity.setInkColor(inkColor);
                }
            }
        }
    }

    /**
     * Runs on death for entities of {@link Inkable}. Does not run client-side.
     */
    public static <T extends Entity & Inkable> void deathInkableEntity(T entity) {
        EntityDimensions dimensions = entity.getDimensions(entity.getPose());
        Vec3d pos = entity.getPos().add(dimensions.width / 2, dimensions.height + 0.5f, dimensions.width / 2);
        inkSquidSoulParticleAtPos(((InkableCaster) entity).toInkable(), pos, 1.0f);
    }

    public static Vec3f decimalToRGB(int color) {
        float red   = ((color >> 16) & 0xFF) / 255.0f;
        float green = ((color >> 8)  & 0xFF) / 255.0f;
        float blue  = ( color        & 0xFF) / 255.0f;
        return new Vec3f(red, green, blue);
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
