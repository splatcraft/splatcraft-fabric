package com.cibernet.splatcraft.init;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.block.CanvasBlock;
import com.cibernet.splatcraft.block.InkedBlock;
import com.cibernet.splatcraft.block.InkwellBlock;
import com.cibernet.splatcraft.item.InkTankArmorItem;
import com.cibernet.splatcraft.item.SplatcraftArmorMaterials;
import com.cibernet.splatcraft.item.SquidBumperItem;
import com.cibernet.splatcraft.item.inkable.InkableArmorItem;
import com.cibernet.splatcraft.item.inkable.InkableBlockItem;
import com.cibernet.splatcraft.item.inkable.InkedBlockItem;
import com.cibernet.splatcraft.item.remote.ColorChangerItem;
import com.cibernet.splatcraft.item.remote.InkDisruptorItem;
import com.cibernet.splatcraft.item.remote.TurfScannerItem;
import com.cibernet.splatcraft.item.weapon.ChargerItem;
import com.cibernet.splatcraft.item.weapon.RollerItem;
import com.cibernet.splatcraft.item.weapon.ShooterItem;
import com.cibernet.splatcraft.item.weapon.component.ChargerComponent;
import com.cibernet.splatcraft.item.weapon.component.RollerComponent;
import com.cibernet.splatcraft.item.weapon.component.ShooterComponent;
import io.github.fablabsmc.fablabs.api.bannerpattern.v1.LoomPattern;
import io.github.fablabsmc.fablabs.api.bannerpattern.v1.LoomPatternItem;
import io.github.fablabsmc.fablabs.api.bannerpattern.v1.LoomPatterns;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unused")
public class SplatcraftItems {
    private static final List<Item> INKABLES = new LinkedList<>();
    /*
     * WEAPONS
     */

    public static final Item.Settings WEAPON_SETTINGS = new FabricItemSettings().maxCount(1).group(Splatcraft.ITEM_GROUP);

    public static final Item SPLAT_ROLLER = register("splat_roller", new RollerItem(WEAPON_SETTINGS, 1.08f, new RollerComponent(0.1f, 2, 25.0f, 0.45f, false, new RollerComponent.Fling(9.0f, 0.8f, 8.0f, 0.4f))));
    public static final Item KRAK_ON_SPLAT_ROLLER = register("krak_on_splat_roller", new RollerItem((RollerItem) SPLAT_ROLLER));
    public static final Item COROCORO_SPLAT_ROLLER = register("corocoro_splat_roller", new RollerItem((RollerItem) SPLAT_ROLLER, RollerComponent.copy((RollerItem) SPLAT_ROLLER).setSpeed(0.5f).setRadius(2)));
    public static final Item CARBON_ROLLER = register("carbon_roller", new RollerItem((RollerItem) SPLAT_ROLLER, RollerComponent.copy((RollerItem) SPLAT_ROLLER).setDamage(14.0f).setFlingComponent(new RollerComponent.Fling(4.0f, 0.7f, 8.0f, 0.63f))));
    public static final Item INKBRUSH = register("inkbrush", new RollerItem((RollerItem) SPLAT_ROLLER, new RollerComponent(0.135f, 1, 4.0f, 1.0f, true, new RollerComponent.Fling(2.0f, 1.0f, 6.0f, 0.5f))));
    public static final Item OCTOBRUSH = register("octobrush", new RollerItem((RollerItem) SPLAT_ROLLER, new RollerComponent(0.18f, 1, 5.0f, 0.8f, true, RollerComponent.Fling.copy((RollerItem) INKBRUSH))));

    public static final Item SPLATTERSHOT = register("splattershot", new ShooterItem(WEAPON_SETTINGS, new ShooterComponent(0.9f, 1.05f, 8.0f, 4.0f, 0.75f, 12.0f)));
    public static final Item TENTATEK_SPLATTERSHOT = register("tentatek_splattershot", new ShooterItem((ShooterItem) SPLATTERSHOT));
    public static final Item WASABI_SPLATTERSHOT = register("wasabi_splattershot", new ShooterItem((ShooterItem) SPLATTERSHOT));
    public static final Item ANCIENT_SPLATTERSHOT = register("ancient_splattershot", new ShooterItem((ShooterItem) SPLATTERSHOT).setSecret());
    public static final Item SPLATTERSHOT_JR = register("splattershot_jr", new ShooterItem((ShooterItem) SPLATTERSHOT, new ShooterComponent(0.43f, 1.0f, 5.6f, 4.0f, 0.55f, 13.5f)).setJunior());
    public static final Item AEROSPRAY_MG = register("aerospray_mg", new ShooterItem(WEAPON_SETTINGS, new ShooterComponent(0.5f, 1.3f, 4.8f, 2.0f, 0.45f, 26.0f)));
    public static final Item AEROSPRAY_RG = register("aerospray_rg", new ShooterItem((ShooterItem) AEROSPRAY_MG));
    public static final Item GAL_52 = register("52_gal", new ShooterItem(WEAPON_SETTINGS, new ShooterComponent(1.3f, 1.2f, 10.4f, 9.0f, 0.78f, 16.0f)));
    public static final Item GAL_52_DECO = register("52_gal_deco", new ShooterItem((ShooterItem) GAL_52));
    public static final Item GAL_96 = register("96_gal", new ShooterItem(WEAPON_SETTINGS, new ShooterComponent(2.5f, 1.3f, 12.4f, 11.0f, 0.85f, 12.5f)));
    public static final Item GAL_96_DECO = register("96_gal_deco", new ShooterItem((ShooterItem) GAL_96));

    public static final Item SPLAT_CHARGER = register("splat_charger", new ChargerItem(WEAPON_SETTINGS, 0.4f, new ChargerComponent(2.25f, 18.0f, 20, 40, 10, false, 1.1f, new ChargerComponent.Projectile(32.0f, 0.87f, 48.0f, 3))));
    public static final Item BENTO_SPLAT_CHARGER = register("bento_splat_charger", new ChargerItem((ChargerItem) SPLAT_CHARGER));
    public static final Item KELP_SPLAT_CHARGER = register("kelp_splat_charger", new ChargerItem((ChargerItem) SPLAT_CHARGER));
    public static final Item E_LITER_4K = register("e_liter_4k", new ChargerItem(WEAPON_SETTINGS, 0.15f, new ChargerComponent(2.25f, 25.0f, 35, 40, 10, false, 1.0f, new ChargerComponent.Projectile(36.0f, 0.95f, 96.0f, 16))));
    public static final Item BAMBOOZLER_14_MK1 = register("bamboozler_14_mk1", new ChargerItem(WEAPON_SETTINGS, 0.8f, new ChargerComponent(2.8f, 7.0f, 4, 10, 0, true, 1.1f, new ChargerComponent.Projectile(16.0f, 0.86f, 40.0f, 2, false))));
    public static final Item BAMBOOZLER_14_MK2 = register("bamboozler_14_mk2", new ChargerItem((ChargerItem) BAMBOOZLER_14_MK1));

    /*
     * WEARABLES
     */

    public static final Item.Settings WEARABLE_SETTINGS = new FabricItemSettings().maxCount(1).group(Splatcraft.ITEM_GROUP);

    public static final Item INK_TANK = register(InkTankArmorItem.id, new InkTankArmorItem(100, WEARABLE_SETTINGS));
    public static final Item CLASSIC_INK_TANK = register("classic_" + InkTankArmorItem.id, new InkTankArmorItem((InkTankArmorItem) INK_TANK));
    public static final Item INK_TANK_JR = register(InkTankArmorItem.id + "_jr", new InkTankArmorItem(110, WEARABLE_SETTINGS).allowedWeapons(SPLATTERSHOT_JR));
    public static final Item ARMORED_INK_TANK = register("armored_" + InkTankArmorItem.id, new InkTankArmorItem(85,  SplatcraftArmorMaterials.ARMORED_INK_TANK, WEARABLE_SETTINGS));

    public static final Item INK_CLOTH_HELMET = register("ink_cloth_helmet", new InkableArmorItem(SplatcraftArmorMaterials.INK_CLOTH, EquipmentSlot.HEAD, WEARABLE_SETTINGS));
    public static final Item INK_CLOTH_CHESTPLATE = register("ink_cloth_chestplate", new InkableArmorItem(SplatcraftArmorMaterials.INK_CLOTH, EquipmentSlot.CHEST, WEARABLE_SETTINGS));
    public static final Item INK_CLOTH_LEGGINGS = register("ink_cloth_leggings", new InkableArmorItem(SplatcraftArmorMaterials.INK_CLOTH, EquipmentSlot.LEGS, WEARABLE_SETTINGS));
    public static final Item INK_CLOTH_BOOTS = register("ink_cloth_boots", new InkableArmorItem(SplatcraftArmorMaterials.INK_CLOTH, EquipmentSlot.FEET, WEARABLE_SETTINGS));

    public static final Item SPLATFEST_BAND = register("splatfest_band", new Item(WEARABLE_SETTINGS));

    /*
     * INK
     */

    public static final Item.Settings REMOTE_SETTINGS = new FabricItemSettings().rarity(Rarity.UNCOMMON).maxCount(1).group(Splatcraft.ITEM_GROUP);

    public static final Item COLOR_CHANGER = register("color_changer", new ColorChangerItem(REMOTE_SETTINGS));
    public static final Item INK_DISRUPTOR = register("ink_disruptor", new InkDisruptorItem(REMOTE_SETTINGS));
    public static final Item TURF_SCANNER = register("turf_scanner", new TurfScannerItem(REMOTE_SETTINGS));

    /*
     * BANNER PATTERNS
     */

    public static final Item INK_SQUID_BANNER_PATTERN = register("inkling_banner_pattern", new LoomPatternItem(SplatcraftLoomPatterns.INKLING, new FabricItemSettings().maxCount(1).group(Splatcraft.ITEM_GROUP)));
    public static final Item OCTOLING_BANNER_PATTERN = register("octoling_banner_pattern", new LoomPatternItem(SplatcraftLoomPatterns.OCTOLING, new FabricItemSettings().maxCount(1).group(Splatcraft.ITEM_GROUP)));

    /*
     * COLORABLES
     */

    public static final Item INKWELL = register(InkwellBlock.id, new InkableBlockItem(SplatcraftBlocks.INKWELL, new FabricItemSettings().group(Splatcraft.ITEM_GROUP)));
    public static final Item CANVAS = register(CanvasBlock.id, new InkableBlockItem(SplatcraftBlocks.CANVAS, new FabricItemSettings().group(Splatcraft.ITEM_GROUP)));
    public static final Item SQUID_BUMPER = register("squid_bumper", new SquidBumperItem(new FabricItemSettings().maxCount(16).group(Splatcraft.ITEM_GROUP)));

    public static final Item INKED_BLOCK = register(InkedBlock.id, new InkedBlockItem(SplatcraftBlocks.INKED_BLOCK, new FabricItemSettings()));

    public static Item register(String id, Item item) {
        return Registry.register(Registry.ITEM, new Identifier(Splatcraft.MOD_ID, id), item);
    }
    public static Item register(LoomPattern loomPattern) {
        return register(Objects.requireNonNull(LoomPatterns.REGISTRY.getId(loomPattern)).getPath() + "_banner_pattern", new LoomPatternItem(loomPattern, new FabricItemSettings().maxCount(1).group(Splatcraft.ITEM_GROUP)));
    }

    public static Item[] getInkables() {
        return INKABLES.toArray(new Item[]{});
    }
    public static void addToInkables(Item item) {
        INKABLES.add(item);
    }
}
