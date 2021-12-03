package net.splatcraft.network;

import net.minecraft.util.Identifier;
import net.splatcraft.Splatcraft;

public class PacketIdentifiers {
    public static final Identifier SET_SQUID_FORM = create("set_squid_form");

    private static Identifier create(String id) {
        return new Identifier(Splatcraft.MOD_ID, id);
    }
}
