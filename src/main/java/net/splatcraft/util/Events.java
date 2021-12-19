package net.splatcraft.util;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.splatcraft.command.InkColorCommand;
import net.splatcraft.component.PlayerDataComponent;
import org.jetbrains.annotations.Nullable;

import static net.splatcraft.util.SplatcraftUtil.*;

@SuppressWarnings("unused")
public final class Events {
    private Events() {}

    public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated) {
        InkColorCommand.register(dispatcher);
    }

    public static void onPlayerJoin(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
        refreshSplatfestBand(handler.player);
    }

    public static void onStartTick(MinecraftServer server) {
        for (ServerPlayerEntity player : PlayerLookup.all(server)) {
            PlayerDataComponent data = PlayerDataComponent.get(player);
            data.setSubmerged(canSubmergeInInk(player));
        }
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
}
