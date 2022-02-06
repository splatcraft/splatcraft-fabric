package net.splatcraft.world;

import com.google.common.base.CaseFormat;
import com.google.common.base.Suppliers;
import com.google.common.collect.Maps;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.splatcraft.mixin.GameRulesAccessor;
import net.splatcraft.mixin.GameRulesRuleAccessor;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static net.minecraft.world.GameRules.*;
import static net.splatcraft.world.SplatcraftGameRules.*;

public final class SynchronizedBooleanGameRuleRegistry {
    private static final Map<Key<BooleanRule>, Boolean> REGISTRY = Maps.newHashMap();

    private static final Function<Key<BooleanRule>, Identifier> PACKET_IDS = Util.memoize(rule -> {
        String id = rule.getName();
        String[] parts = id.split(":");
        String namespace = parts[0];
        String path = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, parts[1]);
        return new Identifier(namespace, "update_%s".formatted(path));
    });

    /**
     * Cached conversion from rule type to key.
     */
    @SuppressWarnings("unchecked")
    private static final Supplier<Map<Type<BooleanRule>, Key<BooleanRule>>> TYPE_TO_KEY = Suppliers.memoize(() -> {
        Map<Key<?>, Type<?>> map = GameRulesAccessor.getRULE_TYPES();
        return Util.make(Maps.newHashMap(), m -> map.forEach((key, type) -> m.put((Type<BooleanRule>) type, (Key<BooleanRule>) key)));
    });

    /**
     * Cached conversion from game rule to key.
     */
    private static final Function<Rule<?>, Key<BooleanRule>> RULE_TO_KEY = Util.memoize(rule -> {
        return TYPE_TO_KEY.get().get(((GameRulesRuleAccessor) rule).getType());
    });

    private SynchronizedBooleanGameRuleRegistry() {}

    @Environment(EnvType.CLIENT)
    public static void onInitializeClient() {
        // register modification receivers
        for (Key<BooleanRule> key : REGISTRY.keySet()) {
            ClientPlayNetworking.registerGlobalReceiver(PACKET_IDS.apply(key), (client, handler, buf, sender) -> {
                boolean value = buf.readBoolean();
                REGISTRY.put(key, value);
            });
        }
    }

    /**
     * Syncs a game rule to a player.
     */
    private static void updateBoolean(ServerPlayerEntity player, Key<BooleanRule> key) {
        GameRules rules = player.world.getGameRules();
        boolean value = rules.getBoolean(key);

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBoolean(value);
        ServerPlayNetworking.send(player, PACKET_IDS.apply(key), buf);
    }

    private static void updateBoolean(ServerPlayerEntity player, BooleanRule rule) {
        updateBoolean(player, RULE_TO_KEY.apply(rule));
    }

    public static void syncWithClient(MinecraftServer server, BooleanRule rule) {
        for (ServerPlayerEntity player : PlayerLookup.all(server)) updateBoolean(player, rule);
    }

    /**
     * Syncs all registered game rules to a player.
     */
    public static void updateAll(ServerPlayerEntity player) {
        for (Key<BooleanRule> key : REGISTRY.keySet()) updateBoolean(player, key);
    }

    /**
     * Returns the stored synchronised game rule value or the live
     * game rule value, dependning if the world is client or not
     * respectively.
     */
    public static boolean syncedGameRule(World world, Key<BooleanRule> key) {
        if (!REGISTRY.containsKey(key)) throw new IllegalArgumentException("Accessing a non-synchronizable game rule from the synchronized registry: " + key);
        return world.isClient ? REGISTRY.get(key) : gameRule(world, key);
    }

    public static Key<BooleanRule> register(Key<BooleanRule> key, boolean defaultValue) {
        REGISTRY.put(key, defaultValue);
        return key;
    }

    /**
     * Creates a synced boolean rule type.
     *
     * @param defaultValue the default value of the game rule
     * @param changedCallback a callback that is invoked when the value of a game rule has changed
     * @return a synced boolean rule type
     */
    public static GameRules.Type<GameRules.BooleanRule> createBooleanRule(boolean defaultValue, BiConsumer<MinecraftServer, BooleanRule> changedCallback) {
        return GameRuleFactory.createBooleanRule(defaultValue, (server, rule) -> {
            syncWithClient(server, rule);
            changedCallback.accept(server, rule);
        });
    }

    /**
     * Creates a synced boolean rule type.
     *
     * @param defaultValue the default value of the game rule
     * @return a synced boolean rule type
     */
    public static GameRules.Type<GameRules.BooleanRule> createBooleanRule(boolean defaultValue) {
        return GameRuleFactory.createBooleanRule(defaultValue, SynchronizedBooleanGameRuleRegistry::syncWithClient);
    }
}
