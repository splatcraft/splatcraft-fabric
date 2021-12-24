package net.splatcraft.item;

import io.github.fablabsmc.fablabs.api.bannerpattern.v1.LoomPattern;
import io.github.fablabsmc.fablabs.api.bannerpattern.v1.LoomPatternItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.splatcraft.Splatcraft;
import net.splatcraft.block.entity.SplatcraftBannerPatterns;

import static net.splatcraft.item.SplatcraftArmorMaterial.*;

public class SplatcraftItems {
    public static final Item INK_CLOTH_HELMET = armor("ink_cloth_helmet", INK_CLOTH, EquipmentSlot.HEAD, InkableArmorItem::new);
    public static final Item INK_CLOTH_CHESTPLATE = armor("ink_cloth_chestplate", INK_CLOTH, EquipmentSlot.CHEST, InkableArmorItem::new);
    public static final Item INK_CLOTH_LEGGINGS = armor("ink_cloth_leggings", INK_CLOTH, EquipmentSlot.LEGS, InkableArmorItem::new);
    public static final Item INK_CLOTH_BOOTS = armor("ink_cloth_boots", INK_CLOTH, EquipmentSlot.FEET, InkableArmorItem::new);

    public static final Item SPLAT_ROLLER = unstackable("splat_roller", RollerItem::new);

    public static final Item SPLATFEST_BAND = unstackable("splatfest_band", Item::new);

    public static final Item INK_SQUID_BANNER_PATTERN = register("inkling_banner_pattern", SplatcraftBannerPatterns.INKLING);
    public static final Item OCTOLING_BANNER_PATTERN = register("octoling_banner_pattern", SplatcraftBannerPatterns.OCTOLING);

    private static Item register(String id, Item item) {
        return Registry.register(Registry.ITEM, new Identifier(Splatcraft.MOD_ID, id), item);
    }

    private static Item register(String id, LoomPattern pattern) {
        return register(id, new LoomPatternItem(pattern, new FabricItemSettings().maxCount(1).group(SplatcraftItemGroups.ALL)));
    }

    private static Item unstackable(String id, ItemFactory<Item> factory) {
        return register(id, factory.create(new FabricItemSettings().maxCount(1).group(SplatcraftItemGroups.ALL)));
    }

    private static Item armor(String id, ArmorMaterial material, EquipmentSlot slot, ArmorFactory factory) {
        return register(id, factory.create(material, slot, new FabricItemSettings().maxCount(1).group(SplatcraftItemGroups.ALL)));
    }

    @FunctionalInterface
    private interface ItemFactory<T extends Item> {
        T create(Item.Settings settings);
    }

    @FunctionalInterface
    private interface ArmorFactory {
        ArmorItem create(ArmorMaterial material, EquipmentSlot slot, Item.Settings settings);
    }
}
