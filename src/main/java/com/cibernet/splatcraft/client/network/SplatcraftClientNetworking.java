package com.cibernet.splatcraft.client.network;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.block.entity.InkedBlockEntity;
import com.cibernet.splatcraft.client.signal.Signal;
import com.cibernet.splatcraft.client.signal.SignalRegistryManager;
import com.cibernet.splatcraft.client.signal.SignalRendererManager;
import com.cibernet.splatcraft.component.PlayerDataComponent;
import com.cibernet.splatcraft.component.SplatcraftComponents;
import com.cibernet.splatcraft.init.SplatcraftSoundEvents;
import com.cibernet.splatcraft.inkcolor.ColorUtils;
import com.cibernet.splatcraft.inkcolor.InkBlockUtils;
import com.cibernet.splatcraft.inkcolor.InkColor;
import com.cibernet.splatcraft.inkcolor.InkColors;
import com.cibernet.splatcraft.network.SplatcraftNetworkingConstants;
import com.cibernet.splatcraft.particle.InkSplashParticleEffect;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.apache.logging.log4j.Level;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Random;
import java.util.UUID;

@Environment(EnvType.CLIENT)
public class SplatcraftClientNetworking {
    public static void registerReceivers() {
        //
        // INK COLORS
        //

        ClientPlayNetworking.registerGlobalReceiver(SplatcraftNetworkingConstants.SET_BLOCK_ENTITY_INK_COLOR_PACKET_ID, (client, handler, buf, responseSender) -> {
            ClientWorld world = MinecraftClient.getInstance().world;
            if (world != null) {
                BlockPos pos = buf.readBlockPos();
                InkColor inkColor = InkColors.get(buf.readIdentifier());

                client.execute(() -> {
                    BlockEntity blockEntity = world.getBlockEntity(pos);
                    if (blockEntity != null) {
                        ColorUtils.setInkColor(blockEntity, inkColor);
                        world.addSyncedBlockEvent(pos, blockEntity.getCachedState().getBlock(), 0, 0);
                    }
                });
            }
        });
        ClientPlayNetworking.registerGlobalReceiver(SplatcraftNetworkingConstants.SET_BLOCK_ENTITY_SAVED_STATE_PACKET_ID, (client, handler, buf, responseSender) -> {
            ClientWorld world = MinecraftClient.getInstance().world;
            if (world != null) {
                BlockState state = Block.getStateFromRawId(buf.readInt());
                InkColor inkColor = InkColors.get(buf.readIdentifier());
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
            float[] color = ColorUtils.getColorsFromInt(InkColors.get(buf.readIdentifier()).getColorOrLocked());
            float scale = buf.readFloat();

            Vec3d pos = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());

            client.execute(() -> {
                if (client.world != null) {
                    client.world.addParticle(new InkSplashParticleEffect(color[0], color[1], color[2], scale), pos.getX(), pos.getY(), pos.getZ(), 0.0d, 0.0d, 0.0d);
                }
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(SplatcraftNetworkingConstants.SYNC_INK_COLOR_CHANGE_FOR_COLOR_LOCK_PACKET_ID, (client, handler, buf, responseSender) -> {
            if (client.world != null) {
                client.execute(() -> {
                    for (BlockEntity blockEntity : client.world.blockEntities) {
                        BlockPos pos = blockEntity.getPos();
                        client.world.addSyncedBlockEvent(pos, client.world.getBlockState(pos).getBlock(), 0, 0);
                    }
                });
            }
        });
        ClientPlayNetworking.registerGlobalReceiver(SplatcraftNetworkingConstants.SYNC_INK_COLORS_REGISTRY_PACKET_ID, (client, handler, buf, responseSender) -> {
            InkColors.rebuildIfNeeded(InkColors.getCachedData());

            CompoundTag tag = buf.readCompoundTag();
            if (tag != null) {
                ListTag inkColors = tag.getList("InkColors", 10);
                HashMap<Identifier, InkColor> all = new LinkedHashMap<>();
                inkColors.forEach(inkColor -> {
                    CompoundTag inkColorTag = (CompoundTag) inkColor;
                    Identifier id = Identifier.tryParse(inkColorTag.getString("id"));
                    all.put(id, new InkColor(id, inkColorTag.getInt("Color")));
                });

                Splatcraft.log("Synchronised ink colors with server");
                client.execute(() -> InkColors.setAll(all));
            } else {
                Splatcraft.log(Level.ERROR, "Received ink color list was null!");
            }
        });

        //
        // PLAYER SQUID FORM
        //

        ClientPlayNetworking.registerGlobalReceiver(SplatcraftNetworkingConstants.PLAY_SQUID_TRAVEL_EFFECTS_PACKET_ID, (client, handler, buf, responseSender) -> {
            ClientWorld world = MinecraftClient.getInstance().world;
            if (world != null) {
                PlayerEntity player = world.getPlayerByUuid(buf.readUuid());
                Vec3d vec3d = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
                InkColor inkColor = InkColors.get(buf.readIdentifier());
                float scale = buf.readFloat();

                client.execute(() -> {
                    if (player != null) {
                        Random random = player.getRandom();
                        if (random.nextFloat() <= 0.482f) {
                            PlayerDataComponent data = SplatcraftComponents.PLAYER_DATA.get(player);
                            if (data.isSquid()) {
                                if (!data.isSubmerged()) {
                                    if (random.nextFloat() <= 0.9f) {
                                        player.world.playSound(player.getX(), player.getY(), player.getZ(), SoundEvents.BLOCK_HONEY_BLOCK_FALL, SoundCategory.PLAYERS, 0.15f, 1.0f, false);
                                    }
                                } else {
                                    player.world.playSound(player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_PLAYER_SWIM, SoundCategory.PLAYERS, 0.05f, 2.0f, false);
                                }
                            }
                        }

                        player.world.addParticle(new InkSplashParticleEffect(ColorUtils.getColorsFromInt(inkColor.getColorOrLocked()), scale), vec3d.getX(), vec3d.getY(), vec3d.getZ(), 0.0d, 0.0d, 0.0d);
                    }
                });
            }
        });
        ClientPlayNetworking.registerGlobalReceiver(SplatcraftNetworkingConstants.PLAY_PLAYER_TOGGLE_SQUID_EFFECTS_PACKET_ID, (client, handler, buf, responseSender) -> {
            ClientWorld world = MinecraftClient.getInstance().world;
            if (world != null) {
                PlayerEntity player = world.getPlayerByUuid(buf.readUuid());
                boolean shouldBeSubmerged = buf.readBoolean();
                InkColor inkColor = InkColors.get(buf.readIdentifier());

                client.execute(() -> {
                    if (player != null && client.world != null) {
                        player.world.playSound(player.getX(), player.getY(), player.getZ(), shouldBeSubmerged ? SplatcraftSoundEvents.INK_SUBMERGE : SplatcraftSoundEvents.INK_UNSUBMERGE, SoundCategory.PLAYERS, 0.23f, 0.86f, false);

                        if (inkColor.matches(ColorUtils.getInkColor(player).color)) {
                            for (int i = 0; i < MathHelper.nextInt(player.getRandom(), 5, 7); ++i) {
                                client.world.addParticle(new InkSplashParticleEffect(ColorUtils.getColorsFromInt(inkColor.getColorOrLocked())), player.getParticleX(0.5d), player.getRandomBodyY() - 0.25d, player.getParticleZ(0.5d), 0.0d, 0.0d, 0.0d);
                            }
                        }
                    }
                });
            }
        });

        //
        // SIGNALS
        //

        ClientPlayNetworking.registerGlobalReceiver(SplatcraftNetworkingConstants.PLAY_PLAYER_SIGNAL_PACKET_ID, (client, handler, buf, responseSender) -> {
            UUID uuid = buf.readUuid();
            Signal signal = SignalRegistryManager.get(buf.readIdentifier());
            if (signal != null && client.world != null) {
                client.execute(() -> SignalRendererManager.playSignal((AbstractClientPlayerEntity) client.world.getPlayerByUuid(uuid), signal));
            }
        });
        ClientPlayNetworking.registerGlobalReceiver(SplatcraftNetworkingConstants.RESET_PLAYER_SIGNAL_PACKET_ID, (client, handler, buf, responseSender) -> {
            UUID uuid = buf.readUuid();
            if (client.world != null) {
                client.execute(() -> SignalRendererManager.reset(client.world.getPlayerByUuid(uuid)));
            }
        });
        ClientPlayNetworking.registerGlobalReceiver(SplatcraftNetworkingConstants.DISALLOWED_PLAYER_SIGNAL_PACKET_ID, (client, handler, buf, responseSender) -> {
            UUID uuid = buf.readUuid();
            Identifier id = buf.readIdentifier();

            if (client.world != null) {
                client.execute(() -> {
                    PlayerEntity player = client.world.getPlayerByUuid(uuid);
                    if (player != null) {
                        player.sendMessage(new TranslatableText("text." + Splatcraft.MOD_ID + ".signal.disallowed", SignalRegistryManager.get(id).text).formatted(Formatting.RED), true);
                    }
                });
            }
        });
    }

    public static void toggleSquidForm(ClientPlayerEntity player) {
        ClientPlayNetworking.send(SplatcraftNetworkingConstants.PLAYER_TOGGLE_SQUID_PACKET_ID, PacketByteBufs.empty());
        if (PlayerDataComponent.toggleSquidForm(player)) {
            SignalRendererManager.reset(player);
            player.setSprinting(false);
        }
    }
}
