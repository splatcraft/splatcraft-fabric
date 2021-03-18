package com.cibernet.splatcraft.tag;

import com.cibernet.splatcraft.Splatcraft;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

public class SplatcraftItemTags {
    public static final Tag<Item> INK_TANK_WHITELIST = register("ink_tank_whitelist");
    public static final Tag<Item> INK_TANK_BLACKLIST = register("ink_tank_blacklist");

    private static Tag<Item> register(String id) {
        return TagRegistry.item(new Identifier(Splatcraft.MOD_ID, id));
    }
}
