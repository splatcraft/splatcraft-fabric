package com.cibernet.splatcraft.network;

import com.cibernet.splatcraft.Splatcraft;
import net.minecraft.util.Identifier;

public class SplatcraftNetworkingConstants {
    /*
        Inked/inking blocks.
     */
    public static final Identifier SET_BLOCK_ENTITY_INK_COLOR_PACKET_ID = new Identifier(Splatcraft.MOD_ID, "set_block_entity_ink_color"); // S2C
    public static final Identifier SET_BLOCK_ENTITY_SAVED_STATE_PACKET_ID = new Identifier(Splatcraft.MOD_ID, "set_block_entity_saved_state"); // S2C
    public static final Identifier PLAY_BLOCK_INKING_EFFECTS_PACKET_ID = new Identifier(Splatcraft.MOD_ID, "play_block_inking_effects"); // S2C
    public static final Identifier SYNC_INK_COLOR_CHANGE_FOR_COLOR_LOCK_PACKET_ID = new Identifier(Splatcraft.MOD_ID, "sync_ink_color_change_for_color_lock"); // S2C

    /*
        Player squid form.
     */
    public static final Identifier PLAY_SQUID_TRAVEL_EFFECTS_PACKET_ID = new Identifier(Splatcraft.MOD_ID, "play_squid_travel_effects"); // S2C
    public static final Identifier PLAY_PLAYER_TOGGLE_SQUID_EFFECTS_PACKET_ID = new Identifier(Splatcraft.MOD_ID, "play_player_toggle_squid_effects"); // S2C
    public static final Identifier PLAYER_TOGGLE_SQUID_PACKET_ID = new Identifier(Splatcraft.MOD_ID, "player_toggle_squid"); // C2S
}
