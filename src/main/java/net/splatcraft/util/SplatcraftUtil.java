package net.splatcraft.util;

import me.shedaniel.math.Color;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.Entity;
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
import net.splatcraft.Splatcraft;
import net.splatcraft.component.PlayerDataComponent;
import net.splatcraft.config.CommonConfig;
import net.splatcraft.entity.SplatcraftAttributes;
import net.splatcraft.inkcolor.InkColor;
import net.splatcraft.inkcolor.InkColors;
import net.splatcraft.inkcolor.InkType;
import net.splatcraft.inkcolor.Inkable;
import net.splatcraft.item.SplatcraftItems;
import net.splatcraft.tag.SplatcraftEntityTypeTags;

import java.util.Set;
import java.util.function.Function;

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
        PlayerDataComponent data = PlayerDataComponent.get(player);
        return player.world.getBlockEntity(player.getLandingPos()) instanceof Inkable inkable
            && inkable.getInkColor().equals(data.getInkColor());
    }

    public static float getMovementSpeed(PlayerEntity player, float base) {
        return canSwimInInk(player)
            ? base * (1.0f + (float) player.getAttributeValue(SplatcraftAttributes.INK_SWIM_SPEED) * 100)
            : -1.0f;
    }

    public static final Function<ServerPlayerEntity, ScreenHandlerListener> SPLATFEST_BAND_REFRESH_LISTENER = (player) -> new ScreenHandlerListener() {
        @Override
        public void onSlotUpdate(ScreenHandler handler, int slotId, ItemStack stack) {
            refreshSplatfestBand(player);
        }

        @Override
        public void onPropertyUpdate(ScreenHandler handler, int property, int value) {}
    };

    private static final Hand[] HANDS = Hand.values();
    private static final Set<Item> SPLATFEST_BAND_SET = Set.of(SplatcraftItems.SPLATFEST_BAND);
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

    public static InkType getInkType(PlayerEntity player) {
        PlayerDataComponent data = PlayerDataComponent.get(player);
        return data.hasSplatfestBand() ? InkType.GLOWING : InkType.NORMAL;
    }

    public static InkColor getInkColorFromStack(ItemStack stack) {
        NbtCompound nbt = stack.getItem() instanceof BlockItem
            ? stack.getSubNbt(NBT_BLOCK_ENTITY_TAG)
            : stack.getNbt();
        if (nbt == null) return InkColors._DEFAULT;
        return InkColor.fromString(nbt.getString(NBT_INK_COLOR));
    }

    public static ItemStack setInkColorOnStack(ItemStack stack, InkColor inkColor) {
        NbtCompound nbt = stack.getItem() instanceof BlockItem
            ? stack.getOrCreateSubNbt(NBT_BLOCK_ENTITY_TAG)
            : stack.getOrCreateNbt();
        if (nbt == null) return stack;
        nbt.putString(NBT_INK_COLOR, inkColor.getId().toString());
        return stack;
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
