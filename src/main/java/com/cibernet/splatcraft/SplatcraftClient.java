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
import com.cibernet.splatcraft.item.inkable.ColorLockItemColorProvider;
import com.cibernet.splatcraft.item.remote.RemoteItem;
import com.cibernet.splatcraft.network.SplatcraftNetworkingConstants;
import com.cibernet.splatcraft.particle.InkSplashParticleEffect;
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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.LinkedHashMap;

@Environment(EnvType.CLIENT)
public class SplatcraftClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Splatcraft.log("Initializing client");

        InkColors.rebuildIfNeeded(new LinkedHashMap<>());
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
        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> world == null
            ? InkColors.NONE.getColor()
            : ColorUtils.getInkColor(world.getBlockEntity(pos)).getColorOrLocked(), SplatcraftBlocks.getInkables()
        );

        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> ColorUtils.getInkColor(stack).getColor(), SplatcraftBlocks.getInkables());
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> tintIndex > 0
            ? -1
            : stack.getItem() instanceof ColorLockItemColorProvider
                ? ColorUtils.getInkColor(stack).getColorOrLocked()
                : ColorUtils.getInkColor(stack).getColor(),
            SplatcraftItems.getInkables()
        );

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
            ClientWorld world = MinecraftClient.getInstance().world;
            if (world != null) {
                BlockPos pos = buf.readBlockPos();
                InkColor inkColor = InkColors.get(Identifier.tryParse(buf.readString()));

                client.execute(() -> {
                    BlockEntity blockEntity = world.getBlockEntity(pos);
                    ColorUtils.setInkColor(blockEntity, inkColor);
                    world.addSyncedBlockEvent(pos, blockEntity.getCachedState().getBlock(), 0, 0);
                });
            }
        });
        ClientPlayNetworking.registerGlobalReceiver(SplatcraftNetworkingConstants.SET_BLOCK_ENTITY_SAVED_STATE_PACKET_ID, (client, handler, buf, responseSender) -> {
            ClientWorld world = MinecraftClient.getInstance().world;
            if (world != null) {
                BlockState state = Block.getStateFromRawId(buf.readInt());
                InkColor inkColor = InkColors.get(Identifier.tryParse(buf.readString()));
                BlockPos pos = buf.readBlockPos();
                InkBlockUtils.InkType inkType = buf.readEnumConstant(InkBlockUtils.InkType.class);

                client.execute(() -> {
                    InkedBlockEntity blockEntity = new InkedBlockEntity();
                    blockEntity.setSavedState(state);
                    blockEntity.setInkColor(inkColor);
                    world.setBlockState(pos, inkType.asBlock().getDefaultState());
                    world.setBlockEntity(pos, blockEntity);
                });
            }
        });
        ClientPlayNetworking.registerGlobalReceiver(SplatcraftNetworkingConstants.PLAY_BLOCK_INKING_EFFECTS_PACKET_ID, (client, handler, buf, responseSender) -> {
            float[] color = ColorUtils.getColorsFromInt(InkColors.get(Identifier.tryParse(buf.readString())).getColorOrLocked());
            float scale = buf.readFloat();

            Vec3d pos = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());

            client.execute(() -> client.world.addParticle(new InkSplashParticleEffect(color[0], color[1], color[2], scale), pos.getX(), pos.getY(), pos.getZ(), 0.0D, 0.0D, 0.0D));
        });
        ClientPlayNetworking.registerGlobalReceiver(SplatcraftNetworkingConstants.SYNC_INK_COLOR_CHANGE_FOR_COLOR_LOCK_PACKET_ID, (client, handler, buf, responseSender) -> {
            ClientWorld world = MinecraftClient.getInstance().world;
            if (world != null) {
                client.execute(() -> {
                    for (BlockEntity blockEntity : world.blockEntities) {
                        BlockPos pos = blockEntity.getPos();
                        world.addSyncedBlockEvent(pos, world.getBlockState(pos).getBlock(), 0, 0);
                    }
                });
            }
        });
        ClientPlayNetworking.registerGlobalReceiver(SplatcraftNetworkingConstants.PLAY_SQUID_TRAVEL_EFFECTS_PACKET_ID, (client, handler, buf, responseSender) -> {
            ClientWorld world = MinecraftClient.getInstance().world;
            if (world != null) {
                PlayerEntity player = world.getPlayerByUuid(buf.readUuid());
                Vec3d vec3d = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
                InkColor inkColor = InkColors.get(Identifier.tryParse(buf.readString()));
                float scale = buf.readFloat();

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

                    player.world.addParticle(new InkSplashParticleEffect(ColorUtils.getColorsFromInt(inkColor.getColorOrLocked()), scale), vec3d.getX(), vec3d.getY(), vec3d.getZ(), 0.0D, 0.0D, 0.0D);
                });
            }
        });
        ClientPlayNetworking.registerGlobalReceiver(SplatcraftNetworkingConstants.PLAY_PLAYER_TOGGLE_SQUID_EFFECTS_PACKET_ID, (client, handler, buf, responseSender) -> {
            ClientWorld world = MinecraftClient.getInstance().world;
            if (world != null) {
                PlayerEntity player = world.getPlayerByUuid(buf.readUuid());
                boolean shouldBeSubmerged = buf.readBoolean();
                InkColor inkColor = InkColors.get(Identifier.tryParse(buf.readString()));

                client.execute(() -> {
                    player.world.playSound(player.getX(), player.getY(), player.getZ(), shouldBeSubmerged ? SplatcraftSoundEvents.INK_SUBMERGE : SplatcraftSoundEvents.INK_UNSUBMERGE, SoundCategory.PLAYERS, 0.23F, 0.86F, false);

                    if (inkColor.matches(ColorUtils.getInkColor(player).getColor())) {
                        for (int i = 0; i < MathHelper.nextInt(player.getRandom(), 5, 7); ++i) {
                            client.world.addParticle(new InkSplashParticleEffect(ColorUtils.getColorsFromInt(inkColor.getColorOrLocked())), player.getParticleX(0.5D), player.getRandomBodyY() - 0.25D, player.getParticleZ(0.5D), 0.0D, 0.0D, 0.0D);
                        }
                    }
                });
            }
        });
        ClientPlayNetworking.registerGlobalReceiver(SplatcraftNetworkingConstants.SYNC_INK_COLORS_REGISTRY_PACKET_ID, (client, handler, buf, responseSender) -> {
            InkColors.rebuildIfNeeded(InkColors.getCachedData());

            CompoundTag tag = buf.readCompoundTag();
            ListTag inkColors = tag.getList("InkColors", 10);
            HashMap<Identifier, InkColor> all = new LinkedHashMap<>();
            inkColors.forEach(inkColor -> {
                CompoundTag inkColorTag = (CompoundTag) inkColor;
                Identifier id = Identifier.tryParse(inkColorTag.getString("id"));
                all.put(id, new InkColor(id, inkColorTag.getInt("Color")));
            });

            Splatcraft.log("Synchronised ink colors with server");
            client.execute(() -> InkColors.setAll(all));
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
