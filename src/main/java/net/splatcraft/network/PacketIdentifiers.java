package net.splatcraft.network;

import net.minecraft.util.Identifier;
import net.splatcraft.Splatcraft;

public class PacketIdentifiers {
    /**
     * A packet for whenever the client presses the change squid form key.
     */
    public static final Identifier KEY_CHANGE_SQUID_FORM = create("key_change_squid_form");

    /**
     * A packet returned by the server when receiving {@link PacketIdentifiers#KEY_CHANGE_SQUID_FORM}.
     */
    public static final Identifier KEY_CHANGE_SQUID_FORM_RESPONSE = create("key_change_squid_form_response");

    /**
     * A packet for playing squid travel particles and sounds.
     */
    public static final Identifier PLAY_SQUID_TRAVEL_EFFECTS = create("play_squid_travel_effects");

    private static Identifier create(String id) {
        return new Identifier(Splatcraft.MOD_ID, id);
    }
}
