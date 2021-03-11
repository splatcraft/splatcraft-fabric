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
import com.cibernet.splatcraft.item.remote.ColorChangerItem;
import com.cibernet.splatcraft.item.remote.InkDisruptorItem;
import com.cibernet.splatcraft.item.remote.TurfScannerItem;
import com.cibernet.splatcraft.item.weapon.RollerItem;
import com.cibernet.splatcraft.item.weapon.ShooterItem;
import com.cibernet.splatcraft.item.weapon.component.RollerComponent;
import com.cibernet.splatcraft.item.weapon.component.ShooterComponent;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;

@SuppressWarnings("unused")
public class SplatcraftItems {
    //
    // COLORABLES
    //

    public static final Item INKWELL = register(InkwellBlock.id, new InkableBlockItem(SplatcraftBlocks.INKWELL, new FabricItemSettings().group(Splatcraft.ItemGroups.COLORED_BLOCKS)));
    public static final Item CANVAS = register(CanvasBlock.id, new InkableBlockItem(SplatcraftBlocks.CANVAS, new FabricItemSettings().group(Splatcraft.ItemGroups.COLORED_BLOCKS)));
    public static final Item SQUID_BUMPER = register("squid_bumper", new SquidBumperItem(new FabricItemSettings().maxCount(16).group(Splatcraft.ItemGroups.COLORED_BLOCKS)));

    public static final Item INKED_BLOCK = register(InkedBlock.id, new BlockItem(SplatcraftBlocks.INKED_BLOCK, new FabricItemSettings()));

    //
    // WEAPONS
    //

    public static final Item.Settings WEAPON_SETTINGS = new FabricItemSettings().maxCount(1).group(Splatcraft.ItemGroups.WEAPONS);

    public static final Item SPLAT_ROLLER = register("splat_roller", new RollerItem(WEAPON_SETTINGS, new RollerComponent(0.1F, 2, 25.0F, 0.45F, false, new RollerComponent.Fling(9.0F, 0.8F, 8.0F, 0.4F))));
    public static final Item KRAK_ON_SPLAT_ROLLER = register("krak_on_splat_roller", new RollerItem((RollerItem) SPLAT_ROLLER));
    public static final Item COROCORO_SPLAT_ROLLER = register("corocoro_splat_roller", new RollerItem((RollerItem) SPLAT_ROLLER, RollerComponent.copy((RollerItem) SPLAT_ROLLER).setSpeed(0.5F).setRadius(2)));
    public static final Item CARBON_ROLLER = register("carbon_roller", new RollerItem((RollerItem) SPLAT_ROLLER, RollerComponent.copy((RollerItem) SPLAT_ROLLER).setDamage(14.0F).setFlingComponent(new RollerComponent.Fling(4.0F, 0.7F, 8.0F, 0.63F))));
    public static final Item INKBRUSH = register("inkbrush", new RollerItem((RollerItem) SPLAT_ROLLER, new RollerComponent(0.135F, 1, 4.0F, 1.0F, true, new RollerComponent.Fling(2.0F, 1.0F, 6.0F, 0.5F))));
    public static final Item OCTOBRUSH = register("octobrush", new RollerItem((RollerItem) SPLAT_ROLLER, new RollerComponent(0.18F, 1, 5.0F, 0.8F, true, RollerComponent.Fling.copy((RollerItem) INKBRUSH))));

    public static final Item SPLATTERSHOT = register("splattershot", new ShooterItem(WEAPON_SETTINGS, new ShooterComponent(0.9F, 1.05F, 8.0F, 4.0F, 0.75F, 12.0F)));
    public static final Item TENTATEK_SPLATTERSHOT = register("tentatek_splattershot", new ShooterItem((ShooterItem) SPLATTERSHOT));
    public static final Item WASABI_SPLATTERSHOT = register("wasabi_splattershot", new ShooterItem((ShooterItem) SPLATTERSHOT));
    public static final Item ANCIENT_SPLATTERSHOT = register("ancient_splattershot", new ShooterItem((ShooterItem) SPLATTERSHOT).setSecret());
    public static final Item SPLATTERSHOT_JR = register("splattershot_jr", new ShooterItem((ShooterItem) SPLATTERSHOT, new ShooterComponent(0.5F, 1.0F, 6.5F, 4.0F, 0.55F, 13.5F)));
    public static final Item AEROSPRAY_MG = register("aerospray_mg", new ShooterItem(WEAPON_SETTINGS, new ShooterComponent(0.5F, 1.3F, 4.8F, 2.0F, 0.45F, 26.0F)));
    public static final Item AEROSPRAY_RG = register("aerospray_rg", new ShooterItem((ShooterItem) AEROSPRAY_MG));
    public static final Item GAL_52 = register("52_gal", new ShooterItem(WEAPON_SETTINGS, new ShooterComponent(1.3F, 1.2F, 10.4F, 9.0F, 0.78F, 16.0F)));
    public static final Item GAL_52_DECO = register("52_gal_deco", new ShooterItem((ShooterItem) GAL_52));
    public static final Item GAL_96 = register("96_gal", new ShooterItem(WEAPON_SETTINGS, new ShooterComponent(2.5F, 1.3F, 12.4F, 11.0F, 0.85F, 12.5F)));
    public static final Item GAL_96_DECO = register("96_gal_deco", new ShooterItem((ShooterItem) GAL_96));

    //
    // WEARABLES
    //

    public static final Item.Settings WEARABLE_SETTINGS = new FabricItemSettings().maxCount(1).group(Splatcraft.ItemGroups.ITEM_GROUP);

    public static final Item INK_TANK = register(InkTankArmorItem.id, new InkTankArmorItem(100, WEARABLE_SETTINGS));
    public static final Item CLASSIC_INK_TANK = register("classic_" + InkTankArmorItem.id, new InkTankArmorItem((InkTankArmorItem) INK_TANK));
    public static final Item INK_TANK_JR = register(InkTankArmorItem.id + "_jr", new InkTankArmorItem(110, WEARABLE_SETTINGS)/*.addAllowedWeapons(SPLATTERSHOT_JR) TODO */);
    public static final Item ARMORED_INK_TANK = register("armored_" + InkTankArmorItem.id, new InkTankArmorItem(85,  SplatcraftArmorMaterials.ARMORED_INK_TANK, WEARABLE_SETTINGS));

    public static final Item INK_CLOTH_HELMET = register("ink_cloth_helmet", new InkableArmorItem(SplatcraftArmorMaterials.INK_CLOTH, EquipmentSlot.HEAD, WEARABLE_SETTINGS));
    public static final Item INK_CLOTH_CHESTPLATE = register("ink_cloth_chestplate", new InkableArmorItem(SplatcraftArmorMaterials.INK_CLOTH, EquipmentSlot.CHEST, WEARABLE_SETTINGS));
    public static final Item INK_CLOTH_LEGGINGS = register("ink_cloth_leggings", new InkableArmorItem(SplatcraftArmorMaterials.INK_CLOTH, EquipmentSlot.LEGS, WEARABLE_SETTINGS));
    public static final Item INK_CLOTH_BOOTS = register("ink_cloth_boots", new InkableArmorItem(SplatcraftArmorMaterials.INK_CLOTH, EquipmentSlot.FEET, WEARABLE_SETTINGS));

    public static final Item SPLATFEST_BAND = register("splatfest_band", new Item(WEARABLE_SETTINGS));

    //
    // INK
    //

    public static final Item.Settings REMOTE_SETTINGS = new FabricItemSettings().rarity(Rarity.UNCOMMON).maxCount(1).group(Splatcraft.ItemGroups.ITEM_GROUP);

    public static final Item COLOR_CHANGER = register("color_changer", new ColorChangerItem(REMOTE_SETTINGS));
    public static final Item INK_DISRUPTOR = register("ink_disruptor", new InkDisruptorItem(REMOTE_SETTINGS));
    public static final Item TURF_SCANNER = register("turf_scanner", new TurfScannerItem(REMOTE_SETTINGS));

    public SplatcraftItems() {}

    public static Item register(String id, Item item) {
        return Registry.register(Registry.ITEM, new Identifier(Splatcraft.MOD_ID, id), item);
    }

    public static Item[] getInkableItems() {
        return new Item[]{ SPLAT_ROLLER, KRAK_ON_SPLAT_ROLLER, COROCORO_SPLAT_ROLLER, CARBON_ROLLER, INKBRUSH, OCTOBRUSH, SPLATTERSHOT, TENTATEK_SPLATTERSHOT, WASABI_SPLATTERSHOT, ANCIENT_SPLATTERSHOT, SPLATTERSHOT_JR, AEROSPRAY_MG, AEROSPRAY_RG, GAL_52, GAL_52_DECO, GAL_96, GAL_96_DECO, INK_TANK, CLASSIC_INK_TANK, INK_TANK_JR, ARMORED_INK_TANK, SQUID_BUMPER, INK_CLOTH_HELMET, INK_CLOTH_CHESTPLATE, INK_CLOTH_LEGGINGS, INK_CLOTH_BOOTS, COLOR_CHANGER };
    }
}
