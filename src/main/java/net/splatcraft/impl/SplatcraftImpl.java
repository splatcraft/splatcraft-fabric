package net.splatcraft.impl;

import com.google.common.reflect.Reflection;
import com.mojang.brigadier.CommandDispatcher;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
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
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.World;
import net.moddingplayground.frame.api.util.InitializationLogger;
import net.splatcraft.api.Splatcraft;
import net.splatcraft.api.block.SplatcraftBlocks;
import net.splatcraft.api.block.entity.SplatcraftBannerPatterns;
import net.splatcraft.api.block.entity.SplatcraftBlockEntityType;
import net.splatcraft.api.component.PlayerDataComponent;
import net.splatcraft.api.component.SplatcraftComponents;
import net.splatcraft.api.entity.SplatcraftEntityType;
import net.splatcraft.api.inkcolor.InkColors;
import net.splatcraft.api.item.SplatcraftItems;
import net.splatcraft.api.particle.SplatcraftParticleType;
import net.splatcraft.api.registry.SplatcraftRegistries;
import net.splatcraft.api.sound.SplatcraftSoundEvents;
import net.splatcraft.api.tag.InkColorTags;
import net.splatcraft.api.world.SplatcraftGameRules;
import net.splatcraft.impl.command.InkColorCommand;
import net.splatcraft.impl.entity.access.InkEntityAccess;
import net.splatcraft.impl.entity.access.PlayerEntityAccess;
import org.jetbrains.annotations.Nullable;

import static net.splatcraft.impl.network.NetworkingCommon.*;

public final class SplatcraftImpl implements Splatcraft, ModInitializer, EntityComponentInitializer {
    private final InitializationLogger initializer;

    public SplatcraftImpl() {
        this.initializer = new InitializationLogger(LOGGER, MOD_NAME);
        new Events();
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public void onInitialize() {
        this.initializer.start();

        Reflection.initialize(
            SplatcraftRegistries.class,
            InkColors.class,

            SplatcraftSoundEvents.class,
            SplatcraftParticleType.class,
            SplatcraftGameRules.class,

            SplatcraftBannerPatterns.class,
            SplatcraftBlocks.class,
            SplatcraftBlockEntityType.class,

            SplatcraftItems.class,
            SplatcraftEntityType.class
        );

        FabricLoader loader = FabricLoader.getInstance();
        if (loader.isDevelopmentEnvironment()) this.onInitializeDev();

        this.initializer.finish();
    }

    public void onInitializeDev() {
        InitializationLogger logger = new InitializationLogger(LOGGER, MOD_NAME + "-dev");
        logger.start();

        for (Registry<?> registry : Registry.REGISTRIES) {
            Identifier registryId = registry.getKey().getValue();
            if (registryId.getNamespace().equals(MOD_ID)) {
                LOGGER.info("registry {}: {}", registryId.getPath(), registry.size());
            }
        }

        logger.finish();
    }

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(SplatcraftComponents.PLAYER_DATA, PlayerDataComponent::new, RespawnCopyStrategy.ALWAYS_COPY);
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

        public void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registry, CommandManager.RegistrationEnvironment environment) {
            InkColorCommand.register(dispatcher);
        }

        public void playerInit(ServerPlayNetworkHandler handler, MinecraftServer server) {
            s2cInit(handler.player);
        }

        public void playerJoin(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
            PlayerDataComponent data = PlayerDataComponent.get(handler.player);

            ((PlayerEntityAccess) handler.player).checkSplatfestBand();
            if (data.tryInitialize()) {
                Random random = handler.player.getRandom();
                SplatcraftRegistries.INK_COLOR.getOrCreateEntryList(InkColorTags.STARTER_COLORS)
                                              .getRandom(random)
                                              .map(RegistryEntry::value)
                                              .ifPresent(data::setInkColor);
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
