package net.splatcraft.api.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.BannerPatternItem;
import net.minecraft.item.Item;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.splatcraft.api.Splatcraft;
import net.splatcraft.api.item.weapon.RollerItem;
import net.splatcraft.api.item.weapon.ShooterItem;
import net.splatcraft.api.item.weapon.settings.InkConsumptionSettings;
import net.splatcraft.api.item.weapon.settings.ShooterSettings;
import net.splatcraft.api.item.weapon.settings.WeaponWeight;
import net.splatcraft.api.tag.SplatcraftBannerPatternTags;

import static net.splatcraft.api.item.SplatcraftArmorMaterial.*;

public interface SplatcraftItems {
    /* Armor */

    Item INK_CLOTH_HELMET = armor("ink_cloth_helmet", INK_CLOTH, EquipmentSlot.HEAD, InkableArmorItem::new);
    Item INK_CLOTH_CHESTPLATE = armor("ink_cloth_chestplate", INK_CLOTH, EquipmentSlot.CHEST, InkableArmorItem::new);
    Item INK_CLOTH_LEGGINGS = armor("ink_cloth_leggings", INK_CLOTH, EquipmentSlot.LEGS, InkableArmorItem::new);
    Item INK_CLOTH_BOOTS = armor("ink_cloth_boots", INK_CLOTH, EquipmentSlot.FEET, InkableArmorItem::new);

    Item INK_TANK = register("ink_tank", 100, InkTankItem::new);

    /* Weapons */

    Item SPLAT_ROLLER = unstackable("splat_roller", RollerItem::new);

    Item SPLATTERSHOT = shooter("splattershot",
        ShooterSettings.builder()
                       .weaponWeight(WeaponWeight.MIDDLEWEIGHT)
                       .usageMobility(0.72f)
                       .build(InkConsumptionSettings.builder().consumption(0.92f).regenerationCooldown(20).build(), 6, ctx -> {
                           int e = ctx.age() - 8;
                           // 0.5625f per 1/3 of a tick
                           return Math.max(e > 0 ? 36.0f - ((0.5625f * 3) * e) : 0, 18.0f);
                       })
    );

    /* Miscellaneous */

    Item SPLATFEST_BAND = unstackable("splatfest_band", Item::new);

    Item INK_SQUID_BANNER_PATTERN = register("inkling_banner_pattern", SplatcraftBannerPatternTags.INKLING_PATTERN_ITEM);
    Item OCTOLING_BANNER_PATTERN = register("octoling_banner_pattern", SplatcraftBannerPatternTags.OCTOLING_PATTERN_ITEM);

    private static Item register(String id, Item item) {
        return Registry.register(Registry.ITEM, new Identifier(Splatcraft.MOD_ID, id), item);
    }

    private static Item register(String id, TagKey<BannerPattern> tag) {
        return unstackable(id, settings -> new BannerPatternItem(tag, settings));
    }

    private static Item register(String id, int capacity, InkTankFactory factory) {
        return unstackable(id, settings -> factory.create(capacity, settings.equipmentSlot(stack -> EquipmentSlot.CHEST)));
    }

    private static Item unstackable(String id, ItemFactory<Item> factory) {
        return register(id, factory.create(new FabricItemSettings().maxCount(1).group(SplatcraftItemGroups.ALL)));
    }

    private static Item shooter(String id, ShooterSettings settings) {
        return unstackable(id, s -> new ShooterItem(settings, s));
    }

    private static Item armor(String id, ArmorMaterial material, EquipmentSlot slot, ArmorFactory factory) {
        return unstackable(id, settings -> factory.create(material, slot, settings));
    }

    @FunctionalInterface interface ItemFactory<T extends Item> { T create(FabricItemSettings settings); }
    @FunctionalInterface interface ArmorFactory { ArmorItem create(ArmorMaterial material, EquipmentSlot slot, FabricItemSettings settings); }
    @FunctionalInterface interface InkTankFactory { InkTankItem create(int capacity, FabricItemSettings settings); }
}
