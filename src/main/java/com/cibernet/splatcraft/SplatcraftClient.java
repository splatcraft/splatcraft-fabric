package com.cibernet.splatcraft;

import com.cibernet.splatcraft.block.entity.InkedBlockEntity;
import com.cibernet.splatcraft.client.config.SplatcraftConfigManager;
import com.cibernet.splatcraft.client.init.SplatcraftKeyBindings;
import com.cibernet.splatcraft.client.model.ink_tank.AbstractInkTankArmorModel;
import com.cibernet.splatcraft.client.particle.InkSplashParticle;
import com.cibernet.splatcraft.client.renderer.InkProjectileEntityRenderer;
import com.cibernet.splatcraft.client.renderer.InkedBlockEntityRenderer;
import com.cibernet.splatcraft.client.renderer.StageBarrierBlockEntityRenderer;
import com.cibernet.splatcraft.client.renderer.entity.ink_squid.InkSquidEntityRenderer;
import com.cibernet.splatcraft.client.renderer.entity.squid_bumper.SquidBumperEntityRenderer;
import com.cibernet.splatcraft.component.PlayerDataComponent;
import com.cibernet.splatcraft.init.*;
import com.cibernet.splatcraft.inkcolor.ColorUtils;
import com.cibernet.splatcraft.inkcolor.InkBlockUtils;
import com.cibernet.splatcraft.inkcolor.InkColor;
import com.cibernet.splatcraft.inkcolor.InkColors;
import com.cibernet.splatcraft.item.InkTankArmorItem;
import com.cibernet.splatcraft.item.remote.RemoteItem;
import com.cibernet.splatcraft.network.SplatcraftNetworkingConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ModelPredicateProvider;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class SplatcraftClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Splatcraft.log("Initializing client");

        SplatcraftConfigManager.load();
        new SplatcraftKeyBindings();

        // block entity renderers
        BlockEntityRendererRegistry berrIntance = BlockEntityRendererRegistry.INSTANCE;
        berrIntance.register(SplatcraftBlockEntities.STAGE_BARRIER, StageBarrierBlockEntityRenderer::new);
        berrIntance.register(SplatcraftBlockEntities.INKED_BLOCK, InkedBlockEntityRenderer::new);

        // entity renderers
        EntityRendererRegistry errInstance = EntityRendererRegistry.INSTANCE;
        errInstance.register(SplatcraftEntities.INK_SQUID, InkSquidEntityRenderer::new);
        errInstance.register(SplatcraftEntities.SQUID_BUMPER, SquidBumperEntityRenderer::new);
        errInstance.register(SplatcraftEntities.INK_PROJECTILE, InkProjectileEntityRenderer::new);

        // custom armor models
        ArmorRenderingRegistry.registerModel((entity, stack, slot, defaultModel) -> ((InkTankArmorItem) stack.getItem()).getArmorModel(entity, stack, slot, defaultModel), AbstractInkTankArmorModel.ITEM_TO_MODEL_MAP.keySet());

        // color providers
        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> world == null ? InkColors.NONE.getColor() : ColorUtils.getInkColor(world.getBlockEntity(pos)).getColor(), SplatcraftBlocks.getInkableBlocks());
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> ColorUtils.getInkColor(stack).getColor(), SplatcraftBlocks.getInkableBlocks());
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> tintIndex > 0 ? -1 : ColorUtils.getInkColor(stack).getColor(), SplatcraftItems.getInkableItems());

        // model predicates
        for (Item item : new Item[] { SplatcraftItems.COLOR_CHANGER }) {
            registerModelPredicate(item, "active", (stack, world, entity) -> RemoteItem.hasCoordSet(stack) ? 1.0F : 0.0F);
            registerModelPredicate(item, "mode", (stack, world, entity) -> RemoteItem.getRemoteMode(stack));
        }
        registerUnfoldedModelPredicate(SplatcraftItems.SPLAT_ROLLER, SplatcraftItems.KRAK_ON_SPLAT_ROLLER, SplatcraftItems.COROCORO_SPLAT_ROLLER, SplatcraftItems.OCTOBRUSH, SplatcraftItems.CARBON_ROLLER, SplatcraftItems.INKBRUSH);

        // particles
        ParticleFactoryRegistry pfrInstance = ParticleFactoryRegistry.getInstance();
        pfrInstance.register(SplatcraftParticles.INK_SPLASH, InkSplashParticle.Factory::new);

        // block render layers
        BlockRenderLayerMap brlmInstance = BlockRenderLayerMap.INSTANCE;
        brlmInstance.putBlocks(RenderLayer.getTranslucent(), SplatcraftBlocks.GLOWING_INKED_BLOCK);
        brlmInstance.putBlocks(RenderLayer.getCutout(), SplatcraftBlocks.INKWELL/*, SplatcraftBlocks.EMPTY_INKWELL*/, SplatcraftBlocks.GRATE, SplatcraftBlocks.GRATE_RAMP/*, SplatcraftBlocks.CRATE, SplatcraftBlocks.SUNKEN_CRATE*/);

        ClientPlayNetworking.registerGlobalReceiver(SplatcraftNetworkingConstants.SET_BLOCK_ENTITY_INK_COLOR_PACKET_ID, (client, handler, buf, responseSender) -> {
            BlockPos pos = buf.readBlockPos();
            InkColor inkColor = SplatcraftRegistries.INK_COLORS.get(Identifier.tryParse(buf.readString()));
            BlockState state = Block.getStateFromRawId(buf.readInt());

            client.execute(() -> {
                ClientWorld world = MinecraftClient.getInstance().world;
                world.setBlockState(pos, state);

                BlockEntity blockEntity = world.getBlockEntity(pos);
                ColorUtils.setInkColor(blockEntity, inkColor);
                world.addSyncedBlockEvent(pos, blockEntity.getCachedState().getBlock(), 0, 0);
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(SplatcraftNetworkingConstants.PLAY_PLAYER_TRAVEL_EFFECTS_PACKET_ID, (client, handler, buf, responseSender) -> {
            PlayerEntity player = MinecraftClient.getInstance().world.getPlayerByUuid(buf.readUuid());
            Vec3d vec3d = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
            InkColor inkColor = SplatcraftRegistries.INK_COLORS.get(Identifier.tryParse(buf.readString()));

            client.execute(() -> {
                if (player != null && player.getRandom().nextFloat() <= 0.482F) {
                    PlayerDataComponent data = SplatcraftComponents.PLAYER_DATA.get(player);
                    if (data.isSquid()) {
                        if (!data.isSubmerged()) {
                            if (player.getRandom().nextFloat() <= 0.9F) {
                                player.world.playSound(player.getX(), player.getY(), player.getZ(), SoundEvents.BLOCK_HONEY_BLOCK_FALL, SoundCategory.PLAYERS, 0.15F, 1.0F, false);
                            }
                        } else {
                            player.world.playSound(player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_PLAYER_SWIM, SoundCategory.PLAYERS, 0.05F, 2.0F, false);
                        }
                    }
                }

                ColorUtils.addInkSplashParticle(player.world, inkColor, vec3d);
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(SplatcraftNetworkingConstants.PLAY_TOGGLE_SQUID_FORM_EFFECTS_PACKET_ID, (client, handler, buf, responseSender) -> {
            PlayerEntity player = MinecraftClient.getInstance().world.getPlayerByUuid(buf.readUuid());
            boolean shouldBeSubmerged = buf.readBoolean();
            InkColor inkColor = SplatcraftRegistries.INK_COLORS.get(Identifier.tryParse(buf.readString()));

            client.execute(() -> {
                player.world.playSound(player.getX(), player.getY(), player.getZ(), shouldBeSubmerged ? SplatcraftSoundEvents.INK_SUBMERGE : SplatcraftSoundEvents.INK_UNSUBMERGE, SoundCategory.PLAYERS, 0.23F, 0.86F, false);

                if (inkColor == ColorUtils.getInkColor(player)) {
                    for (int i = 0; i < 10; ++i) {
                        ColorUtils.addInkSplashParticle(player.world, inkColor, new Vec3d(player.getParticleX(0.5D), player.getRandomBodyY() - 0.25D, player.getParticleZ(0.5D)));
                    }
                }
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(SplatcraftNetworkingConstants.SET_BLOCK_ENTITY_SAVED_STATE_PACKET_ID, (client, handler, buf, responseSender) -> {
            BlockState state = Block.getStateFromRawId(buf.readInt());
            InkColor inkColor = SplatcraftRegistries.INK_COLORS.get(Identifier.tryParse(buf.readString()));
            BlockPos pos = buf.readBlockPos();
            InkBlockUtils.InkType inkType = buf.readEnumConstant(InkBlockUtils.InkType.class);

            client.execute(() -> {
                InkedBlockEntity blockEntity = new InkedBlockEntity();
                blockEntity.setSavedState(state);
                blockEntity.setInkColor(inkColor);
                World world = MinecraftClient.getInstance().world;
                world.setBlockState(pos, inkType.asBlock().getDefaultState());
                world.setBlockEntity(pos, blockEntity);
            });
        });

        Splatcraft.log("Initialized client");
    }

    private static void registerModelPredicate(Item item, String id, ModelPredicateProvider provider) {
        FabricModelPredicateProviderRegistry.register(item, new Identifier(Splatcraft.MOD_ID, id), provider);
    }
    private static void registerUnfoldedModelPredicate(Item... items) {
        for (Item item : items) {
            registerModelPredicate(item, "unfolded", (stack, world, entity) -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0F : 0.0F);
        }
    }
}
