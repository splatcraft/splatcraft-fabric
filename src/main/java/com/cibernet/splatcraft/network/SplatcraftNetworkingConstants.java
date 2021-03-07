package com.cibernet.splatcraft.network;

import com.cibernet.splatcraft.Splatcraft;
import net.minecraft.util.Identifier;

public class SplatcraftNetworkingConstants {
    public static final Identifier SET_BLOCK_ENTITY_INK_COLOR_PACKET_ID = new Identifier(Splatcraft.MOD_ID, "set_block_entity_ink_color");
    public static final Identifier SET_BLOCK_ENTITY_SAVED_STATE_PACKET_ID = new Identifier(Splatcraft.MOD_ID, "set_block_entity_saved_state");
    public static final Identifier PLAY_PLAYER_TRAVEL_EFFECTS_PACKET_ID = new Identifier(Splatcraft.MOD_ID, "play_player_travel_effects_packet_id");
    public static final Identifier PLAY_TOGGLE_SQUID_FORM_EFFECTS_PACKET_ID = new Identifier(Splatcraft.MOD_ID, "play_toggle_squid_form_effects");
    public static final Identifier TOGGLE_SQUID_PACKET_ID = new Identifier(Splatcraft.MOD_ID, "toggle_squid");
}
