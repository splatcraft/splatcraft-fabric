package net.splatcraft.util;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
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
import net.splatcraft.client.config.ClientConfig;
import net.splatcraft.command.InkColorCommand;
import net.splatcraft.component.PlayerDataComponent;
import net.splatcraft.entity.InkEntityAccess;
import net.splatcraft.entity.PlayerEntityAccess;
import net.splatcraft.inkcolor.Inkable;
import net.splatcraft.item.InkTankItem;
import net.splatcraft.tag.SplatcraftBlockTags;
import net.splatcraft.world.SplatcraftGameRules;
import org.jetbrains.annotations.Nullable;

import static net.splatcraft.particle.SplatcraftParticles.inkSplash;

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

    public static boolean beforeBlockBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity) {
        return canInteractWithWorld(player);
    }

    public static void onBlockBreakCanceled(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity) {
        if (!world.isClient) world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
    }

    public static ActionResult getInteractActionResult(PlayerEntity player) {
        return !canInteractWithWorld(player) ? ActionResult.FAIL : ActionResult.PASS;
    }

    public static boolean canInteractWithWorld(PlayerEntity player) {
        PlayerDataComponent data = PlayerDataComponent.get(player);
        return !data.isSquid();
    }

    public static <T extends Entity & Inkable> void tickInkable(T entity, Vec3d movementInput) {
        PlayerDataComponent data = entity instanceof PlayerEntity player ? PlayerDataComponent.get(player) : null;
        if (entity.world.isClient) {
            clientTickInkable(entity, movementInput, data);
        } else {
            if (entity.isOnGround() && entity.world.getGameRules().getBoolean(SplatcraftGameRules.INKWELL_CHANGES_INK_COLOR)) {
                BlockEntity blockEntity = entity.world.getBlockEntity(entity.getLandingPos());
                if (blockEntity != null && SplatcraftBlockTags.INK_COLOR_CHANGERS.contains(blockEntity.getCachedState().getBlock())) {
                    if (blockEntity instanceof Inkable inkable) {
                        if (!(entity instanceof PlayerEntity player) || data.isSquid()) entity.setInkColor(inkable.getInkColor());
                    }
                }
            }
        }
    }

    @Environment(EnvType.CLIENT)
    private static <T extends Entity & Inkable> void clientTickInkable(T entity, Vec3d movementInput, PlayerDataComponent data) {
        if (ClientConfig.INSTANCE.inkSplashParticleOnTravel.getValue()) {
            InkEntityAccess access = (InkEntityAccess) entity;
            if (movementInput.length() > 0.2d) {
                if ((entity instanceof PlayerEntity player && data.isSubmerged()) || (!(entity instanceof PlayerEntity) && access.isOnInk())) {
                    inkSplash(entity.world, entity, access.getInkSplashParticlePos(), 0.75f);
                }
            }
        }
    }

    @Environment(EnvType.CLIENT)
    public static boolean allowCapeRender(AbstractClientPlayerEntity player) {
        return !ClientConfig.INSTANCE.cancelCapeRenderWithInkTank.getValue() || !(player.getEquippedStack(EquipmentSlot.CHEST).getItem() instanceof InkTankItem);
    }
}
