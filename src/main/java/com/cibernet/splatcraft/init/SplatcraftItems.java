package com.cibernet.splatcraft.init;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.block.CanvasBlock;
import com.cibernet.splatcraft.block.InkedBlock;
import com.cibernet.splatcraft.block.InkwellBlock;
import com.cibernet.splatcraft.item.ColorChangerItem;
import com.cibernet.splatcraft.item.ColorableBlockItem;
import com.cibernet.splatcraft.item.InkClothArmorItem;
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

    public static final Item INKWELL = register(InkwellBlock.id, new ColorableBlockItem(SplatcraftBlocks.INKWELL, new FabricItemSettings().group(Splatcraft.ItemGroups.COLORED_BLOCKS)));
    public static final Item CANVAS = register(CanvasBlock.id, new ColorableBlockItem(SplatcraftBlocks.CANVAS, new FabricItemSettings().group(Splatcraft.ItemGroups.COLORED_BLOCKS)));
    public static final Item INKED_BLOCK = register(InkedBlock.id, new BlockItem(SplatcraftBlocks.INKED_BLOCK, new FabricItemSettings()));

    /*public static final Item POWER_EGG = new Item().setUnlocalizedName("powerEgg").setRegistryName("power_egg").setCreativeTab(TabSplatcraft.main);
    public static final Item POWER_EGG_CAN = new PowerEggCanItem();
    public static final Item SARDINIUM = new Item().setUnlocalizedName("sardinium").setRegistryName("sardinium").setCreativeTab(TabSplatcraft.main);

    public static final Item SPLATTERSHOT = new ShooterBaseItem("splattershot", "splattershot", 1.05f, 0.65f, 12f, 4, 8f, 0.9f);
    public static final Item TENTATEK_SPLATTERSHOT = new ShooterBaseItem("tentatekSplattershot", "tentatek_splattershot", SPLATTERSHOT);
    public static final Item WASABI_SPLATTERSHOT = new ShooterBaseItem("wasabiSplattershot", "wasabi_splattershot", SPLATTERSHOT);
    public static final Item SPLAT_ROLLER = new RollerBaseItem("splatRoller", "splat_roller", -2.5d, 0.4f, 12f, 0.8f, 9f,1.15d, 3, 20, 0.15f, false);
    public static final Item KRAK_ON_SPLAT_ROLLER = new RollerBaseItem("krakOnSplatRoller", "krak_on_splat_roller", SPLAT_ROLLER);
    public static final Item CORO_CORO_SPLAT_ROLLER = new RollerBaseItem("coroCoroSplatRoller", "corocoro_splat_roller", SPLAT_ROLLER);
    public static final Item SPLAT_CHARGER = new ChargerBaseItem("splatCharger", "splat_charger", 0.85f, 1.8f, 13, 20, 40, 32f, 2.25f, 18f, 0.4);
    public static final Item BENTO_SPLAT_CHARGER = new ChargerBaseItem("bentoSplatCharger", "bento_splat_charger", SPLAT_CHARGER);
    public static final Item SPLATTERSHOT_JR = new ShooterBaseItem("splattershotJr", "splattershot_jr", 1f, 0.35f, 13.5f, 4, 6.5f, 0.5f);
    public static final Item INKBRUSH = new RollerBaseItem("inkbrush", "inkbrush", 8D, 0.35f, 6f, 0.85f, 2f, 1.3d,1, 5, 0.135f,true);
    public static final Item AEROSPRAY_MG = new ShooterBaseItem("aerosprayMG", "aerospray_mg", 1.3f, 0.35f, 26f, 2, 4.8f, 0.5f);
    public static final Item AEROSPRAY_RG = new ShooterBaseItem("aerosprayRG", "aerospray_rg", AEROSPRAY_MG);
    //public static final Item clashBlaster = new ShooterBaseItem("clashBlaster", "clash_blaster", 2f, 0.95f, 10f, 12, 12, false);
    public static final Item CLASH_BLASTER = new BlasterBaseItem("clashBlaster", "clash_blaster", 1.8f, 1.2f, 5f, 1, 10, 12f, 6f, 4, 4);
    public static final Item CLASH_BLASTER_NEO = new BlasterBaseItem("clashBlasterNeo", "clash_blaster_neo", CLASH_BLASTER);
    public static final Item OCTOBRUSH = new RollerBaseItem("octobrush", "octobrush", -0.1D, 0.5f, 8f, 0.95f, 3.2f, 1.2d, 2, 4, 0.18f, true);
    public static final Item E_LITER_4K = new ChargerBaseItem("eLiter4K", "e_liter_4k", 0.95f, 2.4f, 16, 35, 40, 36f, 2.25f, 25f, 0.15);
    public static final Item BLASTER = new BlasterBaseItem("blaster", "blaster", 2.3f, 1f, 5f, 4, 20, 25f, 10f, 10f, 6);
    public static final Item GRIM_BLASTER = new BlasterBaseItem("grimBlaster", "grim_blaster", BLASTER);
    public static final Item SPLAT_DUALIE = new DualieBaseItem("splatDualies", "splat_dualies", 1f, 0.55f, 10, 8, 6, 0.75f, 1, 0.7f, 9, 8, 30);
    public static final Item ENPERRY_SPLAT_DUALIE = new DualieBaseItem("enperrySplatDualies", "enperry_splat_dualies", SPLAT_DUALIE);
    public static final Item CARBON_ROLLER = new RollerBaseItem("carbonRoller", "carbon_roller", -1.5d, 0.38f, 8f, 0.7f, 3.5f, 1.25d, 2, 14, 0.12f, false);
    public static final Item GAL_52 = new ShooterBaseItem("52Gal", "52_gal", 1.2f, 0.68f, 16f, 9, 10.4f, 1.3f);
    public static final Item GAL_52_DECO = new ShooterBaseItem("52GalDeco", "52_gal_deco", GAL_52);
    public static final Item GAL_96 = new ShooterBaseItem("96Gal", "96_gal", 1.3f, 0.75f, 12.5f, 11, 12.4f, 2.5f);
    public static final Item GAL_96_DECO = new ShooterBaseItem("96GalDeco", "96_gal_deco", GAL_52);
    public static final Item DUALIE_SQUELCHERS = new DualieBaseItem("dualieSquelchers", "dualie_squelchers", 0.9f, 0.64f, 11.5f, 12, 4.4f, 1.2f, 1, 1f, 5, 6, 12);
    public static final Item SLOSHER = new SlosherBaseItem("slosher", "slosher", 1.6f, 0.4f, 2, 8,14, 4, 7f);
    public static final Item CLASSIC_SLOSHER = new SlosherBaseItem("classicSlosher", "classic_slosher", SLOSHER);
    public static final Item SODA_SLOSHER = new SlosherBaseItem("sodaSlosher", "soda_slosher", SLOSHER);
    public static final Item TRI_SLOSHER = new SlosherBaseItem("triSlosher", "tri_slosher", 1.65f, 0.37f, 3, 20,12.4f, 3, 6f);

    public static final InkTankItem INK_TANK = new InkTankItem("inkTank", "ink_tank", 100);
    public static final InkTankItem CLASSIC_INK_TANK = new InkTankItem("classicInkTank", "ink_tank_classic", INK_TANK);
    public static final InkTankItem INK_TANK_JR = new InkTankItem("inkTankJr", "ink_tank_jr", 110).addAllowedWeapons(SPLATTERSHOT_JR);
    public static final InkTankItem ARMORED_INK_TANK = new InkTankItem("armoredInkTank", "ink_tank_armored", 85, 3);

    public static final Item SQUID_BUMPER = new SquidBumperItem("squidBumper", "squid_bumper");*/

    public static final Item INK_CLOTH_HELMET = register("ink_cloth_helmet", new InkClothArmorItem(EquipmentSlot.HEAD, new FabricItemSettings().maxCount(1).group(Splatcraft.ItemGroups.ITEM_GROUP)));
    public static final Item INK_CLOTH_CHESTPLATE = register("ink_cloth_chestplate", new InkClothArmorItem(EquipmentSlot.CHEST, new FabricItemSettings().maxCount(1).group(Splatcraft.ItemGroups.ITEM_GROUP)));
    public static final Item INK_CLOTH_LEGGINGS = register("ink_cloth_leggings", new InkClothArmorItem(EquipmentSlot.LEGS, new FabricItemSettings().maxCount(1).group(Splatcraft.ItemGroups.ITEM_GROUP)));
    public static final Item INK_CLOTH_BOOTS = register("ink_cloth_boots", new InkClothArmorItem(EquipmentSlot.FEET, new FabricItemSettings().maxCount(1).group(Splatcraft.ItemGroups.ITEM_GROUP)));

    /*public static final FilterItem FILTER_EMPTY = new FilterItem("filterEmpty", "filter_empty", false);
    public static final FilterItem FILTER_NEON = new FilterItem("filterNeon", "filter_neon", false);
    public static final FilterItem FILTER_DYE = new FilterItem("filterDye", "filter_dye", false);
    public static final FilterItem FILTER_PASTEL = new FilterItem("filterPastel", "filter_pastel", false);
    public static final FilterItem FILTER_ENCHANTED = new FilterItem("filterEnchanted", "filter_enchanted", true);
    public static final FilterItem FILTER_CREATIVE = new FilterItem("filterCreative", "filter_creative", true);

    public static final Item INK_DISRUPTOR = new InkDisruptorItem();
    public static final Item TURF_SCANNER = new TurfScannerItem();*/
    public static final Item COLOR_CHANGER = register("color_changer", new ColorChangerItem(new FabricItemSettings().rarity(Rarity.UNCOMMON).maxCount(1).group(Splatcraft.ItemGroups.ITEM_GROUP)));

    public static final Item SPLATFEST_BAND = register("splatfest_band", new Item(new FabricItemSettings().maxCount(1).group(Splatcraft.ItemGroups.ITEM_GROUP)));

    public SplatcraftItems() {}

    public static Item register(String id, Item item) {
        return Registry.register(Registry.ITEM, new Identifier(Splatcraft.MOD_ID, id), item);
    }

    public static Item[] getInkableItems() {
        return new Item[]{ INK_CLOTH_HELMET, INK_CLOTH_CHESTPLATE, INK_CLOTH_LEGGINGS, INK_CLOTH_BOOTS, COLOR_CHANGER };
    }
}
