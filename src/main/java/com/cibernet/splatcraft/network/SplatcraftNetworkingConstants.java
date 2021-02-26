package com.cibernet.splatcraft.network;

import com.cibernet.splatcraft.Splatcraft;
import net.minecraft.util.Identifier;

public class SplatcraftNetworkingConstants {
    public static final Identifier SET_PLAYER_INK_COLOR_PACKET_ID = new Identifier(Splatcraft.MOD_ID, "set_player_ink_color");
    public static final Identifier SET_BLOCK_ENTITY_INK_COLOR_PACKET_ID = new Identifier(Splatcraft.MOD_ID, "set_block_entity_ink_color");
    public static final Identifier TOGGLE_SQUID_PACKET_ID = new Identifier(Splatcraft.MOD_ID, "toggle_squid");
}
