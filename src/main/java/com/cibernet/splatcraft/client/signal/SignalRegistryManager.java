package com.cibernet.splatcraft.client.signal;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class SignalRegistryManager {
    public static List<Identifier> SIGNALS = new LinkedList<>();
    protected static LinkedHashMap<Identifier, Signal> SIGNAL_MAP = new LinkedHashMap<>();

    public static Signal register(Identifier id) {
        SignalRegistryManager.SIGNALS.add(id);
        return SignalRegistryManager.SIGNAL_MAP.put(id, new Signal(id));
    }

    public static Signal fromIndex(int index) {
        return SignalRegistryManager.get(SignalRegistryManager.SIGNALS.get(index));
    }

    public static void loadFromAssets(HashMap<Identifier, Signal> loaded) {
        SIGNALS = new LinkedList<>();
        SIGNAL_MAP = new LinkedHashMap<>();

        loaded.forEach(
            (identifier, signal) -> {
                SIGNALS.add(identifier);
                SIGNAL_MAP.put(identifier, signal);
            }
        );
    }

    public static Signal get(Identifier id) {
        return SignalRegistryManager.SIGNAL_MAP.get(id);
    }
}
