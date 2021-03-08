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
import com.cibernet.splatcraft.item.weapon.FlingComponent;
import com.cibernet.splatcraft.item.weapon.RollerComponent;
import com.cibernet.splatcraft.item.weapon.RollerItem;
import com.cibernet.splatcraft.util.ClientUtils;
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
    // BLOCK ITEMS
    //

    public static final Item INKWELL = register(InkwellBlock.id, new InkableBlockItem(SplatcraftBlocks.INKWELL, new FabricItemSettings().group(Splatcraft.ItemGroups.COLORED_BLOCKS)));
    public static final Item CANVAS = register(CanvasBlock.id, new InkableBlockItem(SplatcraftBlocks.CANVAS, new FabricItemSettings().group(Splatcraft.ItemGroups.COLORED_BLOCKS)));
    public static final Item INKED_BLOCK = register(InkedBlock.id, new BlockItem(SplatcraftBlocks.INKED_BLOCK, new FabricItemSettings()));

    //
    // WEAPONS
    //

    /*public static final Item POWER_EGG = new Item().setUnlocalizedName("powerEgg").setRegistryName("power_egg").setCreativeTab(TabSplatcraft.main);
    public static final Item POWER_EGG_CAN = new PowerEggCanItem();
    public static final Item SARDINIUM = new Item().setUnlocalizedName("sardinium").setRegistryName("sardinium").setCreativeTab(TabSplatcraft.main);

    public static final Item SPLATTERSHOT = new ShooterBaseItem("splattershot", "splattershot", 1.05f, 0.65f, 12f, 4, 8f, 0.9f);
    public static final Item TENTATEK_SPLATTERSHOT = new ShooterBaseItem("tentatekSplattershot", "tentatek_splattershot", SPLATTERSHOT);
    public static final Item WASABI_SPLATTERSHOT = new ShooterBaseItem("wasabiSplattershot", "wasabi_splattershot", SPLATTERSHOT);*/
    public static final Item SPLAT_ROLLER = register("splat_roller", new RollerItem(new FabricItemSettings().customDamage((stack, amount, entity, breakCallback) -> ClientUtils.getInkTankDisplayDurability(stack)).maxCount(1).group(Splatcraft.ItemGroups.WEAPONS), new RollerComponent(0.1F, 2, 25.0F, 0.45F, false, new FlingComponent(9.0F, 0.8F, 8.0F, 0.4F))));
    public static final Item KRAK_ON_SPLAT_ROLLER = register("krak_on_splat_roller", new RollerItem((RollerItem) SPLAT_ROLLER));
    public static final Item COROCORO_SPLAT_ROLLER = register("corocoro_splat_roller", new RollerItem((RollerItem) SPLAT_ROLLER, RollerComponent.copy((RollerItem) SPLAT_ROLLER).setSpeed(0.5F).setRadius(2)));
    public static final Item CARBON_ROLLER = register("carbon_roller", new RollerItem((RollerItem) SPLAT_ROLLER, RollerComponent.copy((RollerItem) SPLAT_ROLLER).setDamage(14.0F).setFlingComponent(new FlingComponent(4.0F, 0.7F, 8.0F, 0.63F))));
    public static final Item INKBRUSH = register("inkbrush", new RollerItem((RollerItem) SPLAT_ROLLER, new RollerComponent(0.135F, 1, 4.0F, 1.0F, true, new FlingComponent(2.0F, 1.0F, 6.0F, 0.5F))));
    public static final Item OCTOBRUSH = register("octobrush", new RollerItem((RollerItem) SPLAT_ROLLER, new RollerComponent(0.18F, 1, 5.0F, 0.8F, true, FlingComponent.copy((RollerItem) INKBRUSH))));
    /*public static final Item SPLAT_CHARGER = new ChargerBaseItem("splatCharger", "splat_charger", 0.85f, 1.8f, 13, 20, 40, 32f, 2.25f, 18f, 0.4);
    public static final Item BENTO_SPLAT_CHARGER = new ChargerBaseItem("bentoSplatCharger", "bento_splat_charger", SPLAT_CHARGER);
    public static final Item SPLATTERSHOT_JR = new ShooterBaseItem("splattershotJr", "splattershot_jr", 1f, 0.35f, 13.5f, 4, 6.5f, 0.5f);*/
    /*public static final Item AEROSPRAY_MG = new ShooterBaseItem("aerosprayMG", "aerospray_mg", 1.3f, 0.35f, 26f, 2, 4.8f, 0.5f);
    public static final Item AEROSPRAY_RG = new ShooterBaseItem("aerosprayRG", "aerospray_rg", AEROSPRAY_MG);
    //public static final Item CLASH_BLASTER = new ShooterBaseItem("clashBlaster", "clash_blaster", 2f, 0.95f, 10f, 12, 12, false);
    public static final Item CLASH_BLASTER = new BlasterBaseItem("clashBlaster", "clash_blaster", 1.8f, 1.2f, 5f, 1, 10, 12f, 6f, 4, 4);
    public static final Item CLASH_BLASTER_NEO = new BlasterBaseItem("clashBlasterNeo", "clash_blaster_neo", CLASH_BLASTER);*/
    /*public static final Item E_LITER_4K = new ChargerBaseItem("eLiter4K", "e_liter_4k", 0.95f, 2.4f, 16, 35, 40, 36f, 2.25f, 25f, 0.15);
    public static final Item BLASTER = new BlasterBaseItem("blaster", "blaster", 2.3f, 1f, 5f, 4, 20, 25f, 10f, 10f, 6);
    public static final Item GRIM_BLASTER = new BlasterBaseItem("grimBlaster", "grim_blaster", BLASTER);
    public static final Item SPLAT_DUALIE = new DualieBaseItem("splatDualies", "splat_dualies", 1f, 0.55f, 10, 8, 6, 0.75f, 1, 0.7f, 9, 8, 30);
    public static final Item ENPERRY_SPLAT_DUALIE = new DualieBaseItem("enperrySplatDualies", "enperry_splat_dualies", SPLAT_DUALIE);*/
    /*public static final Item GAL_52 = new ShooterBaseItem("52Gal", "52_gal", 1.2f, 0.68f, 16f, 9, 10.4f, 1.3f);
    public static final Item GAL_52_DECO = new ShooterBaseItem("52GalDeco", "52_gal_deco", GAL_52);
    public static final Item GAL_96 = new ShooterBaseItem("96Gal", "96_gal", 1.3f, 0.75f, 12.5f, 11, 12.4f, 2.5f);
    public static final Item GAL_96_DECO = new ShooterBaseItem("96GalDeco", "96_gal_deco", GAL_52);
    public static final Item DUALIE_SQUELCHERS = new DualieBaseItem("dualieSquelchers", "dualie_squelchers", 0.9f, 0.64f, 11.5f, 12, 4.4f, 1.2f, 1, 1f, 5, 6, 12);
    public static final Item SLOSHER = new SlosherBaseItem("slosher", "slosher", 1.6f, 0.4f, 2, 8,14, 4, 7f);
    public static final Item CLASSIC_SLOSHER = new SlosherBaseItem("classicSlosher", "classic_slosher", SLOSHER);
    public static final Item SODA_SLOSHER = new SlosherBaseItem("sodaSlosher", "soda_slosher", SLOSHER);
    public static final Item TRI_SLOSHER = new SlosherBaseItem("triSlosher", "tri_slosher", 1.65f, 0.37f, 3, 20,12.4f, 3, 6f);*/

    public static final Item INK_TANK = register(InkTankArmorItem.id, new InkTankArmorItem(100));
    public static final Item CLASSIC_INK_TANK = register("classic_" + InkTankArmorItem.id, new InkTankArmorItem((InkTankArmorItem) INK_TANK));
    public static final Item INK_TANK_JR = register(InkTankArmorItem.id + "_jr", new InkTankArmorItem(110)/*.addAllowedWeapons(SPLATTERSHOT_JR) TODO */);
    public static final Item ARMORED_INK_TANK = register("armored_" + InkTankArmorItem.id, new InkTankArmorItem(85,  SplatcraftArmorMaterials.ARMORED_INK_TANK));

    public static final Item SQUID_BUMPER = register("squid_bumper", new SquidBumperItem(new FabricItemSettings().maxCount(16).group(Splatcraft.ItemGroups.COLORED_BLOCKS)));

    public static final Item INK_CLOTH_HELMET = register("ink_cloth_helmet", new InkableArmorItem(SplatcraftArmorMaterials.INK_CLOTH, EquipmentSlot.HEAD, new FabricItemSettings().maxCount(1).group(Splatcraft.ItemGroups.ITEM_GROUP)));
    public static final Item INK_CLOTH_CHESTPLATE = register("ink_cloth_chestplate", new InkableArmorItem(SplatcraftArmorMaterials.INK_CLOTH, EquipmentSlot.CHEST, new FabricItemSettings().maxCount(1).group(Splatcraft.ItemGroups.ITEM_GROUP)));
    public static final Item INK_CLOTH_LEGGINGS = register("ink_cloth_leggings", new InkableArmorItem(SplatcraftArmorMaterials.INK_CLOTH, EquipmentSlot.LEGS, new FabricItemSettings().maxCount(1).group(Splatcraft.ItemGroups.ITEM_GROUP)));
    public static final Item INK_CLOTH_BOOTS = register("ink_cloth_boots", new InkableArmorItem(SplatcraftArmorMaterials.INK_CLOTH, EquipmentSlot.FEET, new FabricItemSettings().maxCount(1).group(Splatcraft.ItemGroups.ITEM_GROUP)));

    /*public static final FilterItem FILTER_EMPTY = new FilterItem("filterEmpty", "filter_empty", false);
    public static final FilterItem FILTER_NEON = new FilterItem("filterNeon", "filter_neon", false);
    public static final FilterItem FILTER_DYE = new FilterItem("filterDye", "filter_dye", false);
    public static final FilterItem FILTER_PASTEL = new FilterItem("filterPastel", "filter_pastel", false);
    public static final FilterItem FILTER_ENCHANTED = new FilterItem("filterEnchanted", "filter_enchanted", true);
    public static final FilterItem FILTER_CREATIVE = new FilterItem("filterCreative", "filter_creative", true);*/

    public static final Item INK_DISRUPTOR = register("ink_disruptor", new InkDisruptorItem(new FabricItemSettings().rarity(Rarity.UNCOMMON).maxCount(1).group(Splatcraft.ItemGroups.ITEM_GROUP)));
    public static final Item TURF_SCANNER = register("turf_scanner", new TurfScannerItem(new FabricItemSettings().rarity(Rarity.UNCOMMON).maxCount(1).group(Splatcraft.ItemGroups.ITEM_GROUP)));
    public static final Item COLOR_CHANGER = register("color_changer", new ColorChangerItem(new FabricItemSettings().rarity(Rarity.UNCOMMON).maxCount(1).group(Splatcraft.ItemGroups.ITEM_GROUP)));

    public static final Item SPLATFEST_BAND = register("splatfest_band", new Item(new FabricItemSettings().maxCount(1).group(Splatcraft.ItemGroups.ITEM_GROUP)));

    public SplatcraftItems() {}

    public static Item register(String id, Item item) {
        return Registry.register(Registry.ITEM, new Identifier(Splatcraft.MOD_ID, id), item);
    }

    public static Item[] getInkableItems() {
        return new Item[]{ SPLAT_ROLLER, KRAK_ON_SPLAT_ROLLER, COROCORO_SPLAT_ROLLER, OCTOBRUSH, CARBON_ROLLER, INKBRUSH, INK_TANK, CLASSIC_INK_TANK, INK_TANK_JR, ARMORED_INK_TANK, SQUID_BUMPER, INK_CLOTH_HELMET, INK_CLOTH_CHESTPLATE, INK_CLOTH_LEGGINGS, INK_CLOTH_BOOTS, COLOR_CHANGER };
    }
}
