package net.splatcraft;

import com.google.common.reflect.Reflection;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.EntityElytraEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.moddingplayground.frame.api.util.InitializationLogger;
import net.splatcraft.block.SplatcraftBlocks;
import net.splatcraft.block.entity.SplatcraftBannerPatterns;
import net.splatcraft.block.entity.SplatcraftBlockEntities;
import net.splatcraft.command.InkColorCommand;
import net.splatcraft.component.PlayerDataComponent;
import net.splatcraft.entity.SplatcraftEntities;
import net.splatcraft.entity.access.InkEntityAccess;
import net.splatcraft.entity.access.PlayerEntityAccess;
import net.splatcraft.inkcolor.InkColor;
import net.splatcraft.inkcolor.InkColors;
import net.splatcraft.item.SplatcraftItems;
import net.splatcraft.network.NetworkingCommon;
import net.splatcraft.particle.SplatcraftParticles;
import net.splatcraft.registry.SplatcraftRegistries;
import net.splatcraft.tag.InkColorTags;
import net.splatcraft.world.SplatcraftGameRules;
import net.splatcraft.world.SynchronizedBooleanGameRuleRegistry;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

import static net.splatcraft.network.NetworkingCommon.*;

public class Splatcraft implements ModInitializer {
    public static final String MOD_ID   = "splatcraft";
    public static final String MOD_NAME = "Splatcraft";

    private static Splatcraft instance = null;

    protected final Logger logger;
    protected final InitializationLogger initializer;
    protected final Events events;

    public Splatcraft() {
        this.logger = LoggerFactory.getLogger(MOD_ID);
        this.initializer = new InitializationLogger(this.logger, MOD_NAME);
        this.events = new Events();
        instance = this;
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public void onInitialize() {
        this.initializer.start();

        Reflection.initialize(
            SplatcraftRegistries.class,
            InkColors.class,

            SplatcraftParticles.class,
            SplatcraftGameRules.class,

            SplatcraftBannerPatterns.class,
            SplatcraftBlocks.class,
            SplatcraftBlockEntities.class,

            SplatcraftItems.class,
            SplatcraftEntities.class,

            NetworkingCommon.class
        );

        FabricLoader loader = FabricLoader.getInstance();
        if (loader.isDevelopmentEnvironment()) this.onInitializeDev();

        this.initializer.finish();
    }

    public void onInitializeDev() {
        InitializationLogger logger = new InitializationLogger(this.logger, Splatcraft.MOD_NAME + "-dev");
        logger.start();

        for (Registry<?> registry : Registry.REGISTRIES) {
            Identifier registryId = registry.getKey().getValue();
            if (registryId.getNamespace().equals(MOD_ID)) {
                this.logger.info("registry {}: {}", registryId.getPath(), registry.getEntries().size());
            }
        }

        logger.finish();
    }

    public Logger getLogger() {
        return this.logger;
    }

    public Events getEvents() {
        return this.events;
    }

    public static Splatcraft getInstance() {
        return instance;
    }

    public static class Events {
        protected Events() {
            CommandRegistrationCallback.EVENT.register(this::registerCommands);

            ServerPlayConnectionEvents.INIT.register(this::playerInit);
            ServerPlayConnectionEvents.JOIN.register(this::playerJoin);

            UseBlockCallback.EVENT.register(this::useBlock);
            UseItemCallback.EVENT.register(this::useItem);
            UseEntityCallback.EVENT.register(this::useEntity);
            AttackEntityCallback.EVENT.register(this::attackEntity);
            AttackBlockCallback.EVENT.register(this::attackBlock);
            PlayerBlockBreakEvents.BEFORE.register(this::beforeBlockBreak);
            PlayerBlockBreakEvents.CANCELED.register(this::onBlockBreakCanceled);
            EntityElytraEvents.ALLOW.register(this::allowElytraFlight);
        }

        public void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated) {
            InkColorCommand.register(dispatcher);
        }

        public void playerInit(ServerPlayNetworkHandler handler, MinecraftServer server) {
            ServerPlayerEntity player = handler.player;
            s2cInit(player);
            SynchronizedBooleanGameRuleRegistry.updateAll(player);
        }

        public void playerJoin(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
            PlayerDataComponent data = PlayerDataComponent.get(handler.player);

            ((PlayerEntityAccess) handler.player).checkSplatfestBand();
            if (data.tryInitialize()) {
                if (!InkColorTags.STARTER_COLORS.values().isEmpty()) {
                    Random random = handler.player.getRandom();
                    InkColor inkColor = InkColorTags.STARTER_COLORS.getRandom(random);
                    data.setInkColor(inkColor);
                }
            }
        }

        public ActionResult useBlock(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
            return getInteractActionResult(player);
        }

        public TypedActionResult<ItemStack> useItem(PlayerEntity player, World world, Hand hand) {
            ItemStack stack = player.getStackInHand(hand);
            return !canInteractWithWorld(player) ? TypedActionResult.fail(stack) : TypedActionResult.pass(stack);
        }

        public ActionResult useEntity(PlayerEntity player, World world, Hand hand, Entity entity, @Nullable EntityHitResult hitResult) {
            return getInteractActionResult(player);
        }

        public ActionResult attackEntity(PlayerEntity player, World world, Hand hand, Entity entity, @Nullable EntityHitResult hitResult) {
            return getInteractActionResult(player);
        }

        public ActionResult attackBlock(PlayerEntity player, World world, Hand hand, BlockPos pos, Direction direction) {
            return getInteractActionResult(player);
        }

        public boolean beforeBlockBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity) {
            return canInteractWithWorld(player);
        }

        public void onBlockBreakCanceled(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity) {
            if (!world.isClient) world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
        }

        public boolean allowElytraFlight(LivingEntity entity) {
            return entity instanceof InkEntityAccess access && !access.isInSquidForm();
        }

        public ActionResult getInteractActionResult(PlayerEntity player) {
            return !canInteractWithWorld(player) ? ActionResult.FAIL : ActionResult.PASS;
        }

        public boolean canInteractWithWorld(PlayerEntity player) {
            return !((InkEntityAccess) player).isInSquidForm();
        }
    }
}
