package com.cibernet.splatcraft.network;

import com.cibernet.splatcraft.Splatcraft;
import net.minecraft.util.Identifier;

public class SplatcraftNetworkingConstants {
    public static final Identifier SET_BLOCK_ENTITY_INK_COLOR_PACKET_ID = new Identifier(Splatcraft.MOD_ID, "set_block_entity_ink_color");
    public static final Identifier SPAWN_PLAYER_TRAVEL_PARTICLE_PACKET_ID = new Identifier(Splatcraft.MOD_ID, "spawn_player_travel_particle");
    public static final Identifier TOGGLE_SQUID_PACKET_ID = new Identifier(Splatcraft.MOD_ID, "toggle_squid");
}
