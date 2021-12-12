package net.splatcraft.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.splatcraft.Splatcraft;

public class SplatcraftItems {
    public static final Item SPLATFEST_BAND = register("splatfest_band", new Item(new FabricItemSettings().maxCount(1).group(Splatcraft.ITEM_GROUP)));

    private static Item register(String id, Item item) {
        return Registry.register(Registry.ITEM, new Identifier(Splatcraft.MOD_ID, id), item);
    }
}
