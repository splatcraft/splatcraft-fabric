package net.splatcraft.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.moddingplayground.frame.api.banner.FrameBannerPattern;
import net.moddingplayground.frame.api.banner.FrameBannerPatternItem;
import net.splatcraft.Splatcraft;
import net.splatcraft.block.entity.SplatcraftBannerPatterns;
import net.splatcraft.item.weapon.ShooterSettings;

import static net.splatcraft.item.SplatcraftArmorMaterial.*;

public class SplatcraftItems {
    // armor
    public static final Item INK_CLOTH_HELMET = armor("ink_cloth_helmet", INK_CLOTH, EquipmentSlot.HEAD, InkableArmorItem::new);
    public static final Item INK_CLOTH_CHESTPLATE = armor("ink_cloth_chestplate", INK_CLOTH, EquipmentSlot.CHEST, InkableArmorItem::new);
    public static final Item INK_CLOTH_LEGGINGS = armor("ink_cloth_leggings", INK_CLOTH, EquipmentSlot.LEGS, InkableArmorItem::new);
    public static final Item INK_CLOTH_BOOTS = armor("ink_cloth_boots", INK_CLOTH, EquipmentSlot.FEET, InkableArmorItem::new);

    public static final Item INK_TANK = register("ink_tank", 100, InkTankItem::new);

    // weapons
    public static final Item SPLAT_ROLLER = unstackable("splat_roller", RollerItem::new);

    public static final Item SPLATTERSHOT = shooter("splattershot", new ShooterSettings(36, 50, 47, 10.0f, 0.92f, 0.72f, 1.0f));

    // misc
    public static final Item SPLATFEST_BAND = unstackable("splatfest_band", Item::new);

    public static final Item INK_SQUID_BANNER_PATTERN = register("inkling_banner_pattern", SplatcraftBannerPatterns.INKLING);
    public static final Item OCTOLING_BANNER_PATTERN = register("octoling_banner_pattern", SplatcraftBannerPatterns.OCTOLING);

    private static Item register(String id, Item item) {
        return Registry.register(Registry.ITEM, new Identifier(Splatcraft.MOD_ID, id), item);
    }

    private static Item register(String id, FrameBannerPattern pattern) {
        return register(id, new FrameBannerPatternItem(pattern, new FabricItemSettings().maxCount(1).group(SplatcraftItemGroups.ALL)));
    }

    private static Item register(String id, int capacity, InkTankFactory factory) {
        return register(id, factory.create(capacity, new FabricItemSettings().equipmentSlot(stack -> EquipmentSlot.CHEST)
                                                                             .maxCount(1)
                                                                             .group(SplatcraftItemGroups.ALL)));
    }

    private static Item unstackable(String id, ItemFactory<Item> factory) {
        return register(id, factory.create(new FabricItemSettings().maxCount(1).group(SplatcraftItemGroups.ALL)));
    }

    private static Item shooter(String id, ShooterSettings settings) {
        return unstackable(id, s -> new ShooterItem(settings, s));
    }

    private static Item armor(String id, ArmorMaterial material, EquipmentSlot slot, ArmorFactory factory) {
        return register(id, factory.create(material, slot, new FabricItemSettings().maxCount(1).group(SplatcraftItemGroups.ALL)));
    }

    @FunctionalInterface
    private interface ItemFactory<T extends Item> {
        T create(FabricItemSettings settings);
    }

    @FunctionalInterface
    private interface ArmorFactory {
        ArmorItem create(ArmorMaterial material, EquipmentSlot slot, FabricItemSettings settings);
    }

    @FunctionalInterface
    private interface InkTankFactory {
        InkTankItem create(int capacity, FabricItemSettings settings);
    }
}
