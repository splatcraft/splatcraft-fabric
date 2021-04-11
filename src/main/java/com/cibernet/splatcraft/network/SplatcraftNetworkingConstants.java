package com.cibernet.splatcraft.network;

import com.cibernet.splatcraft.Splatcraft;
import net.minecraft.util.Identifier;

public class SplatcraftNetworkingConstants {
    /*
        Ink colors.
     */
    public static final Identifier PLAY_BLOCK_INKING_EFFECTS_PACKET_ID = new Identifier(Splatcraft.MOD_ID, "play_block_inking_effects"); // S2C
    public static final Identifier SYNC_INK_COLOR_CHANGE_FOR_COLOR_LOCK_PACKET_ID = new Identifier(Splatcraft.MOD_ID, "sync_ink_color_change_for_color_lock"); // S2C
    public static final Identifier SYNC_INK_COLORS_REGISTRY_PACKET_ID = new Identifier(Splatcraft.MOD_ID, "sync_ink_colors_registry"); // S2C

    /*
        Player squid form.
     */
    public static final Identifier PLAY_SQUID_TRAVEL_EFFECTS_PACKET_ID = new Identifier(Splatcraft.MOD_ID, "play_squid_travel_effects"); // S2C
    public static final Identifier PLAY_PLAYER_TOGGLE_SQUID_EFFECTS_PACKET_ID = new Identifier(Splatcraft.MOD_ID, "play_player_toggle_squid_effects"); // S2C
    public static final Identifier SET_SQUID_FORM_PACKET_ID = new Identifier(Splatcraft.MOD_ID, "set_squid_form"); // C2S

    /*
        SplatcraftSignals.
     */
    public static final Identifier PLAY_PLAYER_SIGNAL_PACKET_ID = new Identifier(Splatcraft.MOD_ID, "play_player_signal"); // C2S --> S2C
    public static final Identifier RESET_PLAYER_SIGNAL_PACKET_ID = new Identifier(Splatcraft.MOD_ID, "reset_player_signal"); // S2C
    public static final Identifier DISALLOWED_PLAYER_SIGNAL_PACKET_ID = new Identifier(Splatcraft.MOD_ID, "disallowed_player_signal"); // S2C
}
