package net.splatcraft.item;

import io.github.fablabsmc.fablabs.api.bannerpattern.v1.LoomPattern;
import io.github.fablabsmc.fablabs.api.bannerpattern.v1.LoomPatternItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.splatcraft.Splatcraft;
import net.splatcraft.block.entity.SplatcraftBannerPatterns;

import java.util.function.Function;

public class SplatcraftItems {
    public static final Item SPLAT_ROLLER = register("splat_roller", RollerItem::new);

    public static final Item SPLATFEST_BAND = register("splatfest_band", new Item(new FabricItemSettings().maxCount(1).group(SplatcraftItemGroups.ALL)));

    public static final Item INK_SQUID_BANNER_PATTERN = register("inkling_banner_pattern", SplatcraftBannerPatterns.INKLING);
    public static final Item OCTOLING_BANNER_PATTERN = register("octoling_banner_pattern", SplatcraftBannerPatterns.OCTOLING);

    private static Item register(String id, Item item) {
        return Registry.register(Registry.ITEM, new Identifier(Splatcraft.MOD_ID, id), item);
    }

    private static Item register(String id, LoomPattern pattern) {
        return register(id, new LoomPatternItem(pattern, new FabricItemSettings().maxCount(1).group(SplatcraftItemGroups.ALL)));
    }

    private static Item register(String id, Function<Item.Settings, AbstractWeaponItem> factory) {
        return register(id, factory.apply(new FabricItemSettings().maxCount(1).group(SplatcraftItemGroups.ALL)));
    }
}
