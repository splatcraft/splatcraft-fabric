package net.splatcraft.impl.network;

import net.minecraft.util.Identifier;
import net.splatcraft.api.Splatcraft;

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
     * A packet for sending the client's input to the server.
     */
    public static final Identifier CLIENT_INPUT = create("client_input"); // C2S

    /**
     * A packet for notifying the client that Splatcraft is installed on the server.
     */
    public static final Identifier S2C_INIT = create("s2c_init"); // S2C

    private static Identifier create(String id) {
        return new Identifier(Splatcraft.MOD_ID, id);
    }
}
