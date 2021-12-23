package net.splatcraft.network;

import net.minecraft.util.Identifier;
import net.splatcraft.Splatcraft;

public class PacketIdentifiers {
    /**
     * A packet for whenever the client presses the change squid form key.
     */
    public static final Identifier KEY_CHANGE_SQUID_FORM = create("key_change_squid_form"); // C2S

    /**
     * A packet returned by the server when receiving {@link PacketIdentifiers#KEY_CHANGE_SQUID_FORM}.
     */
    public static final Identifier KEY_CHANGE_SQUID_FORM_RESPONSE = create("key_change_squid_form_response"); // S2C

    /**
     * A packet for playing an ink splash particle at a position.
     */
    public static final Identifier INK_SPLASH_PARTICLE_AT_POS = create("ink_splash_particle_at_pos"); // S2C

    /**
     * A packet for playing an ink squid soul particle at a position.
     */
    public static final Identifier INK_SQUID_SOUL_PARTICLE_AT_POS = create("ink_squid_soul_particle_at_pos"); // S2C

    /**
     * A packet for sending the client's input to the server.
     */
    public static final Identifier CLIENT_INPUT = create("client_input"); // C2S

    private static Identifier create(String id) {
        return new Identifier(Splatcraft.MOD_ID, id);
    }
}
