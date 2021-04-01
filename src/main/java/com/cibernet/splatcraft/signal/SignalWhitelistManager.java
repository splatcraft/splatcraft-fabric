package com.cibernet.splatcraft.signal;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;

public class SignalWhitelistManager {
    @Nullable
    protected static LinkedList<Identifier> SIGNAL_WHITELIST = null;

    public static void loadWhitelist(LinkedList<Identifier> loaded) {
        if (loaded.isEmpty()) {
            SIGNAL_WHITELIST = null;
        } else {
            SIGNAL_WHITELIST = loaded;
        }
    }
    public static boolean isAllowed(Identifier id) {
        return SIGNAL_WHITELIST == null || SIGNAL_WHITELIST.contains(id);
    }
}
