package net.splatcraft.util;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.splatcraft.command.InkColorCommand;
import net.splatcraft.component.PlayerDataComponent;
import net.splatcraft.entity.InkEntityAccess;
import net.splatcraft.entity.InkableCaster;
import net.splatcraft.entity.PlayerEntityAccess;
import net.splatcraft.inkcolor.InkColor;
import net.splatcraft.inkcolor.Inkable;
import net.splatcraft.tag.SplatcraftBlockTags;
import net.splatcraft.world.SplatcraftGameRules;
import org.jetbrains.annotations.Nullable;

import static net.splatcraft.network.NetworkingCommon.inkSplashParticleAtPos;
import static net.splatcraft.network.NetworkingCommon.inkSquidSoulParticleAtPos;

@SuppressWarnings("unused")
public final class Events {
    private Events() {}

    public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated) {
        InkColorCommand.register(dispatcher);
    }

    public static void playerJoin(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
        ((PlayerEntityAccess) handler.player).updateSplatfestBand();
    }

    public static ActionResult useBlock(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        return getInteractActionResult(player);
    }

    public static TypedActionResult<ItemStack> useItem(PlayerEntity player, World world, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        return !canInteractWithWorld(player) ? TypedActionResult.fail(stack) : TypedActionResult.pass(stack);
    }

    public static ActionResult useEntity(PlayerEntity player, World world, Hand hand, Entity entity, @Nullable EntityHitResult hitResult) {
        return getInteractActionResult(player);
    }

    public static ActionResult attackEntity(PlayerEntity player, World world, Hand hand, Entity entity, @Nullable EntityHitResult hitResult) {
        return getInteractActionResult(player);
    }

    public static ActionResult attackBlock(PlayerEntity player, World world, Hand hand, BlockPos pos, Direction direction) {
        return getInteractActionResult(player);
    }

    public static ActionResult getInteractActionResult(PlayerEntity player) {
        return !canInteractWithWorld(player) ? ActionResult.FAIL : ActionResult.PASS;
    }

    public static boolean canInteractWithWorld(PlayerEntity player) {
        PlayerDataComponent data = PlayerDataComponent.get(player);
        return !data.isSquid();
    }

    /**
     * A movement tick for entities of {@link Inkable}. Does not run client-side.
     */
    public static <T extends Entity & Inkable> void tickMovementInkableEntity(T entity, Vec3d movementInput) {
        InkEntityAccess inkEntity = ((InkEntityAccess) entity);
        if (movementInput.length() > 0.2d) {
            Vec3d pos = inkEntity.getInkSplashParticlePos();
            float scale = 0.75f;
            if (entity instanceof PlayerEntity player) {
                PlayerDataComponent data = PlayerDataComponent.get(player);
                if (data.isSubmerged()) inkSplashParticleAtPos(entity, pos, scale);
            } else inkSplashParticleAtPos(entity, pos, scale);
        }

        if (entity.world.getGameRules().getBoolean(SplatcraftGameRules.INKWELL_CHANGES_INK_COLOR) && entity.isOnGround()) {
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
}
