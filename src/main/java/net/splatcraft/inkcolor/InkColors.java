package net.splatcraft.inkcolor;

import me.shedaniel.math.Color;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.splatcraft.Splatcraft;
import net.splatcraft.mixin.SplashOverlayAccessor;
import net.splatcraft.registry.SplatcraftRegistries;

@SuppressWarnings("unused")
public class InkColors {
    // dye colors
    public static final InkColor DYE_WHITE = dye(DyeColor.WHITE);
    public static final InkColor DYE_ORANGE = dye(DyeColor.ORANGE);
    public static final InkColor DYE_MAGENTA = dye(DyeColor.MAGENTA);
    public static final InkColor DYE_LIGHT_BLUE = dye(DyeColor.LIGHT_BLUE);
    public static final InkColor DYE_YELLOW = dye(DyeColor.YELLOW);
    public static final InkColor DYE_LIME = dye(DyeColor.LIME);
    public static final InkColor DYE_PINK = dye(DyeColor.PINK);
    public static final InkColor DYE_GRAY = dye(DyeColor.GRAY);
    public static final InkColor DYE_LIGHT_GRAY = dye(DyeColor.LIGHT_GRAY);
    public static final InkColor DYE_CYAN = dye(DyeColor.CYAN);
    public static final InkColor DYE_PURPLE = dye(DyeColor.PURPLE);
    public static final InkColor DYE_BLUE = dye(DyeColor.BLUE);
    public static final InkColor DYE_BROWN = dye(DyeColor.BROWN);
    public static final InkColor DYE_GREEN = dye(DyeColor.GREEN);
    public static final InkColor DYE_RED = dye(DyeColor.RED);
    public static final InkColor DYE_BLACK = dye(DyeColor.BLACK);

    // splatcraft colors
    public static final InkColor MOJANG = vanilla("mojang", SplashOverlayAccessor.getMojangRed()); // mojang logo background color
    public static final InkColor COBALT = splatcraft("cobalt", 0x005682); // Cibernet
    public static final InkColor ICE = splatcraft("ice", 0x88FFC1); // icearstorm
    public static final InkColor FLORAL = splatcraft("floral", 0xFF9BEE); // floralQuaFloral
    public static final InkColor PASTEL_YELLOW = splatcraft("pastel_yellow", 0xFDF674); // andante.

    // pure colors
    public static final InkColor PURE_WHITE  = pure("white",  1.0f, 1.0f, 1.0f);
    public static final InkColor PURE_BLACK  = pure("black",  0.0f, 0.0f, 0.0f);

    public static final InkColor PURE_RED    = pure("red",    1.0f, 0.0f, 0.0f);
    public static final InkColor PURE_YELLOW = pure("yellow", 1.0f, 1.0f, 0.0f);
    public static final InkColor PURE_PURPLE = pure("purple", 1.0f, 0.0f, 1.0f);

    public static final InkColor PURE_GREEN  = pure("green",  0.0f, 1.0f, 0.0f);
    public static final InkColor PURE_CYAN   = pure("cyan",   0.0f, 1.0f, 1.0f);

    public static final InkColor PURE_BLUE   = pure("blue",   0.0f, 0.0f, 1.0f);

    // splatoon: default
    public static final InkColor PINK = splatoon("pink", 0xFE447D);
    public static final InkColor ORANGE = splatoon("orange", 0xF78F2E);
    public static final InkColor YELLOW_ORANGE = splatoon("yellow_orange", 0xFEDC0C);
    public static final InkColor LIME_GREEN = splatoon("lime_green", 0xD1F20A);
    public static final InkColor EMERALD_GREEN = splatoon("emerald_green", 0x5CD05B);
    public static final InkColor TEAL = splatoon("teal", 0x03C1CD);
    public static final InkColor BLUE = splatoon("blue", 0x0E10E6);
    public static final InkColor VIOLET = splatoon("violet", 0x9208E7);
    public static final InkColor RED_ORANGE = splatoon("red_orange", 0xFF4900);
    public static final InkColor YELLOW = splatoon("yellow", 0xF3F354);
    public static final InkColor MINT = splatoon("mint", 0xBFF1E5);
    public static final InkColor GREEN = splatoon("green", 0x3BC335);
    public static final InkColor SEA_GREEN = splatoon("sea_green", 0x00D18A);
    public static final InkColor LIGHT_BLUE = splatoon("light_blue", 0x6565F9);
    public static final InkColor DARK_BLUE = splatoon("dark_blue", 0x101AB3);
    public static final InkColor FUCHSIA = splatoon("fuchsia", 0xD645C8);
    public static final InkColor NEON_GREEN = splatoon("neon_green", 0x9AE528);
    public static final InkColor CYAN = splatoon("cyan", 0x0ACDFE);
    public static final InkColor NEON_ORANGE = splatoon("neon_orange", 0xFF9600);
    public static final InkColor DARK_FUCHSIA = splatoon("dark_fuchsia", 0xB21CA1);
    public static final InkColor TAN = splatoon("tan", 0xE1D6B6);
    public static final InkColor DARK_ORANGE = splatoon("dark_orange", 0xE59D0D);
    public static final InkColor MAGENTA = splatoon("magenta", 0xEC0B68);
    public static final InkColor DARK_VIOLET = splatoon("dark_violet", 0x461362);
    public static final InkColor IKA_MUSUME_BLUE = splatoon("ika_musume_blue", 0x00AAE4);
    public static final InkColor FOREST_GREEN = splatoon("forest_green", 0x0EAA57);

    // splatoon: game
    public static final InkColor S2G_BLUE = splatoon("regular_blue", 0x2922B5);
    public static final InkColor S2G_GREEN = splatoon("regular_green", 0x5EB604);
    public static final InkColor S2G_BLUE_VS_GREEN_NEUTRAL = splatoon("regular_blue_vs_green_neutral", 0xCA21A5);
    public static final InkColor S2G_WINTER_GREEN = splatoon("regular_winter_green", 0x03B362);
    public static final InkColor S2G_DARK_MAGENTA = splatoon("regular_dark_magenta", 0xB1008D);
    public static final InkColor S2G_WINTER_GREEN_VS_DARK_MAGENTA_NEUTRAL = splatoon("regular_winter_green_vs_dark_magenta_neutral", 0xA8B700);
    public static final InkColor S2G_SLIMY_GREEN = splatoon("regular_slimy_green", 0x25B100);
    public static final InkColor S2G_GRAPE = splatoon("regular_grape", 0x571DB1);
    public static final InkColor S2G_SLIMY_GREEN_VS_GRAPE_NEUTRAL = splatoon("regular_slimy_green_vs_grape_neutral", 0xCD2D7E);
    public static final InkColor S2G_RICH_PURPLE = splatoon("regular_rich_purple", 0x7B0393);
    public static final InkColor S2G_GREEN_APPLE = splatoon("regular_green_apple", 0x43BA05);
    public static final InkColor S2G_RICH_PURPLE_VS_GREEN_APPLE_NEUTRAL = splatoon("regular_rich_purple_vs_green_apple_neutral", 0xDB7821);
    public static final InkColor S2G_TURQUOISE = splatoon("regular_turquoise", 0x0CAE6E);
    public static final InkColor S2G_PUMPKIN = splatoon("regular_pumpkin", 0xF75900);
    public static final InkColor S2G_TURQUOISE_VS_PUMPKIN_NEUTRAL = splatoon("regular_turquoise_vs_pumpkin_neutral", 0x6A1EC1);
    public static final InkColor S2G_YELLOW = splatoon("regular_yellow", 0xD9C100);
    public static final InkColor S2G_TRUE_BLUE = splatoon("regular_true_blue", 0x007AC9);
    public static final InkColor S2G_YELLOW_VS_TRUE_BLUE_NEUTRAL = splatoon("regular_yellow_vs_true_blue_neutral", 0xED580B);
    public static final InkColor S2G_MUSTARD = splatoon("regular_mustard", 0xCE8003);
    public static final InkColor S2G_PURPLE = splatoon("regular_purple", 0x9208B2);
    public static final InkColor S2G_MUSTARD_VS_PURPLE_NEUTRAL = splatoon("regular_mustard_vs_purple_neutral", 0x1AB46A);

    // splatoon: ranked
    public static final InkColor S2R_LEMON = splatoon("ranked_lemon", 0xBBC905);
    public static final InkColor S2R_PLUM = splatoon("ranked_plum", 0x830B9C);
    public static final InkColor S2R_SKY = splatoon("ranked_sky", 0x007EDC);
    public static final InkColor S2R_GOLD = splatoon("ranked_gold", 0xE1A307);
    public static final InkColor S2R_LIGHT_PINK = splatoon("ranked_light_pink", 0xD60E6E);
    public static final InkColor S2R_BLUE = splatoon("ranked_blue", 0x311AA8);
    public static final InkColor S2R_NEON_PINK = splatoon("ranked_neon_pink", 0xCF0466);
    public static final InkColor S2R_NEON_GREEN = splatoon("ranked_neon_green", 0x17A80D);
    public static final InkColor S2R_PINK = splatoon("ranked_pink", 0xCB0856);
    public static final InkColor S2R_LIGHT_BLUE = splatoon("ranked_light_blue", 0x0199B8);
    public static final InkColor S2R_RASPBERRY = splatoon("ranked_raspberry", 0xDE0B64);
    public static final InkColor S2R_NEON_YELLOW = splatoon("ranked_neon_yellow", 0xBFD002);
    public static final InkColor S2R_DARK_PURPLE = splatoon("ranked_dark_purple", 0x4A14AA);
    public static final InkColor S2R_ORANGE = splatoon("ranked_orange", 0xFB5C03);
    public static final InkColor S2R_GRAPE = splatoon("ranked_grape", 0x5F0FB4);
    public static final InkColor S2R_TURQUOISE = splatoon("ranked_turquoise", 0x08B672);

    // splatoon: color lock
    public static final InkColor S2CL_GOLDEN_YELLOW = splatoon("lock_golden_yellow", 0xDEA801);
    public static final InkColor S2CL_EELECTRIC_BLUE = splatoon("lock_eelectric_blue", 0x4717A9);

    // splatoon: splatfest
    public static final InkColor SPLATFEST_CATS = splatoon("splatfest_cats", 0xD84011);
    public static final InkColor SPLATFEST_DOGS = splatoon("splatfest_dogs", 0x41A782);
    public static final InkColor SPLATFEST_ROLLER_COASTERS = splatoon("splatfest_roller_coasters", 0x3BC335);
    public static final InkColor SPLATFEST_WATER_SLIDES = splatoon("splatfest_water_slides", 0xB400FF);
    public static final InkColor SPLATFEST_AUTOBOTS = splatoon("splatfest_autobots", 0xEC0B68);
    public static final InkColor SPLATFEST_DECEPTICONS = splatoon("splatfest_decepticons", 0x461362);
    public static final InkColor SPLATFEST_ART = splatoon("splatfest_art", 0x9208E7);
    public static final InkColor SPLATFEST_SCIENCE = splatoon("splatfest_science", 0xF78F2E);
    public static final InkColor SPLATFEST_MESSY = splatoon("splatfest_messy", 0xED7004);
    public static final InkColor SPLATFEST_TIDY = splatoon("splatfest_tidy", 0x0037B8);
    public static final InkColor SPLATFEST_CARS = splatoon("splatfest_cars", 0xF78F2E);
    public static final InkColor SPLATFEST_PLANES = splatoon("splatfest_planes", 0x0E10E6);
    public static final InkColor SPLATFEST_PIRATES = splatoon("splatfest_pirates", 0x4F9E00);
    public static final InkColor SPLATFEST_NINJAS = splatoon("splatfest_ninjas", 0x7D26B5);
    public static final InkColor SPLATFEST_LOVE = splatoon("splatfest_love", 0xDA3781);
    public static final InkColor SPLATFEST_MONEY = splatoon("splatfest_money", 0xFEDC0C);
    public static final InkColor SPLATFEST_MOUNTAIN_FOOD = splatoon("splatfest_mountain_food", 0xBA2763);
    public static final InkColor SPLATFEST_SEA_FOOD = splatoon("splatfest_sea_food", 0x62BA9B);
    public static final InkColor SPLATFEST_BURGERS = splatoon("splatfest_burgers", 0xF78F2E);
    public static final InkColor SPLATFEST_PIZZA = splatoon("splatfest_pizza", 0x3BC335);
    public static final InkColor SPLATFEST_NAUGHTY = splatoon("splatfest_naughty", 0xCF3350);
    public static final InkColor SPLATFEST_NICE = splatoon("splatfest_nice", 0x008040);
    public static final InkColor SPLATFEST_PAST = splatoon("splatfest_past", 0x009A82);
    public static final InkColor SPLATFEST_FUTURE = splatoon("splatfest_future", 0xA34C3B);
    public static final InkColor SPLATFEST_PERFECT_BODY = splatoon("splatfest_perfect_body", 0x32A200);
    public static final InkColor SPLATFEST_PERFECT_BRAIN = splatoon("splatfest_perfect_brain", 0xF9891B);
    public static final InkColor SPLATFEST_BARBARIAN_EUOC = splatoon("splatfest_barbarian_euoc", 0xC54D4F);
    public static final InkColor SPLATFEST_NINJA_EUOC = splatoon("splatfest_ninja_euoc", 0x003B89);
    public static final InkColor SPLATFEST_POKEMON_RED = splatoon("splatfest_pokemon_red", 0xBF586B);
    public static final InkColor SPLATFEST_POKEMON_BLUE = splatoon("splatfest_pokemon_blue", 0x003DA2);
    public static final InkColor SPLATFEST_POKEMON_GREEN = splatoon("splatfest_pokemon_green", 0x009A6E);
    public static final InkColor SPLATFEST_GO_ALL_OUT = splatoon("splatfest_go_all_out", 0xBC326D);
    public static final InkColor SPLATFEST_FOCUS_ON_HEALING = splatoon("splatfest_focus_on_healing", 0xC0AE26);
    public static final InkColor SPLATFEST_HOVERBOARD = splatoon("splatfest_hoverboard", 0x59BE9C);
    public static final InkColor SPLATFEST_JET_PACK = splatoon("splatfest_jet_pack", 0xE75A2D);
    public static final InkColor SPLATFEST_SNOWMAN = splatoon("splatfest_snowman", 0x0EB6A7);
    public static final InkColor SPLATFEST_SANDCASTLE = splatoon("splatfest_sandcastle", 0xFAB01D);
    public static final InkColor SPLATFEST_SPONGEBOB = splatoon("splatfest_spongebob", 0xDCA41D);
    public static final InkColor SPLATFEST_PATRICK = splatoon("splatfest_patrick", 0xE95F9A);
    public static final InkColor SPLATFEST_TUNA_MAYONNAISE = splatoon("splatfest_tuna_mayonnaise", 0x38C8D0);
    public static final InkColor SPLATFEST_RED_SALMON = splatoon("splatfest_red_salmon", 0xE56F87);
    public static final InkColor SPLATFEST_FANCY_PARTY = splatoon("splatfest_fancy_party", 0x0EB6A7);
    public static final InkColor SPLATFEST_COSTUME_PARTY = splatoon("splatfest_costume_party", 0xFAB01D);
    public static final InkColor SPLATFEST_WORLD_TOUR = splatoon("splatfest_world_tour", 0x88AF45);
    public static final InkColor SPLATFEST_SPACE_ADVENTURE = splatoon("splatfest_space_adventure", 0x014BD0);
    public static final InkColor SPLATFEST_MUSHROOM_MOUNTAIN = splatoon("splatfest_mushroom_mountain", 0xF0C016);
    public static final InkColor SPLATFEST_BAMBOO_SHOOT_VILLAGE = splatoon("splatfest_bamboo_shoot_village", 0x56AF45);
    public static final InkColor SPLATFEST_EARLY_BIRD = splatoon("splatfest_early_bird", 0xF9891B);
    public static final InkColor SPLATFEST_NIGHT_OWL = splatoon("splatfest_night_owl", 0x891BEC);
    public static final InkColor SPLATFEST_CALLIE = splatoon("splatfest_callie", 0xAF16AC);
    public static final InkColor SPLATFEST_MARIE = splatoon("splatfest_marie", 0x71DA0C);
    public static final InkColor SPLATFEST_MAYO = splatoon("splatfest_mayo", 0xFFE99B);
    public static final InkColor SPLATFEST_KETCHUP = splatoon("splatfest_ketchup", 0xFB321E);
    public static final InkColor SPLATFEST_MAYO_VS_KETCHUP_NEUTRAL = splatoon("splatfest_mayo_vs_ketchup_neutral", 0x1BB026);
    public static final InkColor SPLATFEST_FLIGHT = splatoon("splatfest_flight", 0x4F55ED);
    public static final InkColor SPLATFEST_INVISIBILITY = splatoon("splatfest_invisibility", 0x6BD52C);
    public static final InkColor SPLATFEST_FLIGHT_VS_INVISIBILITY_NEUTRAL = splatoon("splatfest_flight_vs_invisibility_neutral", 0xED1D9A);
    public static final InkColor SPLATFEST_FRIES = splatoon("splatfest_fries", 0xE8540A);
    public static final InkColor SPLATFEST_MCNUGGETS = splatoon("splatfest_mcnuggets", 0x6A3BE0);
    public static final InkColor SPLATFEST_FRIES_VS_MCNUGGETS_NEUTRAL = splatoon("splatfest_fries_vs_mcnuggets_neutral", 0xFFCA0E);
    public static final InkColor SPLATFEST_FRONT_ROLL = splatoon("splatfest_front_roll", 0xFF7D9A);
    public static final InkColor SPLATFEST_BACK_ROLL = splatoon("splatfest_back_roll", 0x00A0B0);
    public static final InkColor SPLATFEST_FRONT_ROLL_VS_BACK_ROLL_NEUTRAL = splatoon("splatfest_front_roll_vs_back_roll_neutral", 0xE4E567);
    public static final InkColor SPLATFEST_VAMPIRE = splatoon("splatfest_vampire", 0x6735AF);
    public static final InkColor SPLATFEST_WEREWOLF = splatoon("splatfest_werewolf", 0xFE6829);
    public static final InkColor SPLATFEST_VAMPIRE_VS_WEREWOLF_NEUTRAL = splatoon("splatfest_vampire_vs_werewolf_neutral", 0x00CA0E);
    public static final InkColor SPLATFEST_AGILITY = splatoon("splatfest_agility", 0xF3B000);
    public static final InkColor SPLATFEST_ENDURANCE = splatoon("splatfest_endurance", 0xF5498B);
    public static final InkColor SPLATFEST_AGILITY_VS_ENDURANCE_NEUTRAL = splatoon("splatfest_agility_vs_endurance_neutral", 0x13A715);
    public static final InkColor SPLATFEST_WARM = splatoon("splatfest_warm", 0xFF5E17);
    public static final InkColor SPLATFEST_COLD = splatoon("splatfest_cold", 0x00A39A);
    public static final InkColor SPLATFEST_WARM_VS_COLD_NEUTRAL = splatoon("splatfest_warm_vs_cold_neutral", 0xEDD926);
    public static final InkColor SPLATFEST_WITH_LEMON = splatoon("splatfest_with_lemon", 0xBEE600);
    public static final InkColor SPLATFEST_WITHOUT_LEMON = splatoon("splatfest_without_lemon", 0xC52ADB);
    public static final InkColor SPLATFEST_WITH_LEMON_VS_WITHOUT_LEMON_NEUTRAL = splatoon("splatfest_with_lemon_vs_without_lemon_neutral", 0x00B278);
    public static final InkColor SPLATFEST_SCI_FI = splatoon("splatfest_sci_fi", 0x1FAFE8);
    public static final InkColor SPLATFEST_FANTASY = splatoon("splatfest_fantasy", 0x8BFF06);
    public static final InkColor SPLATFEST_SCI_FI_VS_FANTASY_NEUTRAL = splatoon("splatfest_sci_fi_vs_fantasy_neutral", 0xFD636A);
    public static final InkColor SPLATFEST_FILM = splatoon("splatfest_film", 0x9030FF);
    public static final InkColor SPLATFEST_BOOK = splatoon("splatfest_book", 0x14B8A0);
    public static final InkColor SPLATFEST_FILM_VS_BOOK_NEUTRAL = splatoon("splatfest_film_vs_book_neutral", 0xEEC70E);
    public static final InkColor SPLATFEST_WARM_INNER_WEAR = splatoon("splatfest_warm_inner_wear", 0xFF7536);
    public static final InkColor SPLATFEST_WARM_OUTER_WEAR = splatoon("splatfest_warm_outer_wear", 0x9090BA);
    public static final InkColor SPLATFEST_WARM_INNER_WEAR_VS_WARM_OUTER_WEAR_NEUTRAL = splatoon("splatfest_warm_inner_wear_vs_warm_outer_wear_neutral", 0xDC1C1E);
    public static final InkColor SPLATFEST_SWEATER = splatoon("splatfest_sweater", 0xC4354D);
    public static final InkColor SPLATFEST_SOCK = splatoon("splatfest_sock", 0x0D8B51);
    public static final InkColor SPLATFEST_SWEATER_VS_SOCK_NEUTRAL = splatoon("splatfest_sweater_vs_sock_neutral", 0xCACACA);
    public static final InkColor SPLATFEST_ACTION = splatoon("splatfest_action", 0xFA8E00);
    public static final InkColor SPLATFEST_COMEDY = splatoon("splatfest_comedy", 0x1384BA);
    public static final InkColor SPLATFEST_ACTION_VS_COMEDY_NEUTRAL = splatoon("splatfest_action_vs_comedy_neutral", 0xB44AEF);
    public static final InkColor SPLATFEST_CHAMPION = splatoon("splatfest_champion", 0xA18E3B);
    public static final InkColor SPLATFEST_CHALLENGER = splatoon("splatfest_challenger", 0x1D07AC);
    public static final InkColor SPLATFEST_CHAMPION_VS_CHALLENGER_NEUTRAL = splatoon("splatfest_champion_vs_challenger_neutral", 0xF52E71);
    public static final InkColor SPLATFEST_GHERK_OUT = splatoon("splatfest_gherk_out", 0xB14A8D);
    public static final InkColor SPLATFEST_GHERK_IN = splatoon("splatfest_gherk_in", 0x83C91A);
    public static final InkColor SPLATFEST_GHERK_OUT_VS_GHERK_IN_NEUTRAL = splatoon("splatfest_gherk_out_vs_gherk_in_neutral", 0x0FBA9D);
    public static final InkColor SPLATFEST_MONEY_S2 = splatoon("splatfest_money_s2", 0xFFD624);
    public static final InkColor SPLATFEST_LOVE_S2 = splatoon("splatfest_love_s2", 0xFC6D75);
    public static final InkColor SPLATFEST_MONEY_S2_VS_LOVE_S2_NEUTRAL = splatoon("splatfest_money_s2_vs_love_s2_neutral", 0x5451EC);
    public static final InkColor SPLATFEST_FLOWER = splatoon("splatfest_flower", 0xFF8787);
    public static final InkColor SPLATFEST_DUMPLING = splatoon("splatfest_dumpling", 0x7EC27A);
    public static final InkColor SPLATFEST_FLOWER_VS_DUMPLING_NEUTRAL = splatoon("splatfest_flower_vs_dumpling_neutral", 0xECE8B1);
    public static final InkColor SPLATFEST_CHICKEN = splatoon("splatfest_chicken", 0xEFF2EB);
    public static final InkColor SPLATFEST_EGG = splatoon("splatfest_egg", 0xFABF50);
    public static final InkColor SPLATFEST_CHICKEN_VS_EGG_NEUTRAL = splatoon("splatfest_chicken_vs_egg_neutral", 0xED2635);
    public static final InkColor SPLATFEST_LATEST_MODEL = splatoon("splatfest_latest_model", 0x059E9C);
    public static final InkColor SPLATFEST_POPULAR_MODEL = splatoon("splatfest_popular_model", 0xC1CC47);
    public static final InkColor SPLATFEST_LATEST_MODEL_VS_POPULAR_MODEL_NEUTRAL = splatoon("splatfest_latest_model_vs_popular_model_neutral", 0xE2E2E2);
    public static final InkColor SPLATFEST_BASEBALL = splatoon("splatfest_baseball", 0xFA5A2A);
    public static final InkColor SPLATFEST_SOCCER = splatoon("splatfest_soccer", 0x00993A);
    public static final InkColor SPLATFEST_BASEBALL_VS_SOCCER_NEUTRAL = splatoon("splatfest_baseball_vs_soccer_neutral", 0x6642D0);
    public static final InkColor SPLATFEST_SALTY = splatoon("splatfest_salty", 0xB1DBD1);
    public static final InkColor SPLATFEST_SWEET = splatoon("splatfest_sweet", 0xE29440);
    public static final InkColor SPLATFEST_SALTY_VS_SWEET_NEUTRAL = splatoon("splatfest_salty_vs_sweet_neutral", 0xD94C79);
    public static final InkColor SPLATFEST_UNKNOWN_CREATURE = splatoon("splatfest_unknown_creature", 0xFF6D40);
    public static final InkColor SPLATFEST_ADVANCED_TECHNOLOGY = splatoon("splatfest_advanced_technology", 0x38377A);
    public static final InkColor SPLATFEST_UNKNOWN_CREATURE_VS_ADVANCED_TECHNOLOGY_NEUTRAL = splatoon("splatfest_unknown_creature_vs_advanced_technology_neutral", 0xF1E61C);
    public static final InkColor SPLATFEST_RAPH = splatoon("splatfest_raph", 0xE23253);
    public static final InkColor SPLATFEST_LEO = splatoon("splatfest_leo", 0x2A87C6);
    public static final InkColor SPLATFEST_MIKEY = splatoon("splatfest_mikey", 0xF89620);
    public static final InkColor SPLATFEST_DONNIE = splatoon("splatfest_donnie", 0x5233A8);
    public static final InkColor SPLATFEST_TMNT_NEUTRAL = splatoon("splatfest_tmnt_neutral", 0x2D7F4E);
    public static final InkColor SPLATFEST_HELLO_KITTY = splatoon("splatfest_hello_kitty", 0xFADDDD);
    public static final InkColor SPLATFEST_CINNAMOROLL = splatoon("splatfest_cinnamoroll", 0x0B7DE7);
    public static final InkColor SPLATFEST_HELLO_KITTY_VS_CINNAMOROLL_NEUTRAL = splatoon("splatfest_hello_kitty_vs_cinnamoroll_neutral", 0xF385A1);
    public static final InkColor SPLATFEST_MY_MELODY = splatoon("splatfest_my_melody", 0xFF3B8C);
    public static final InkColor SPLATFEST_POMPOMPURIN = splatoon("splatfest_pompompurin", 0xF4CC00);
    public static final InkColor SPLATFEST_MY_MELODY_VS_POMPOMPURIN_NEUTRAL = splatoon("splatfest_my_melody_vs_pompompurin_neutral", 0x0F6ED4);
    public static final InkColor SPLATFEST_HELLO_KITTY_VS_MY_MELODY_NEUTRAL = splatoon("splatfest_hello_kitty_vs_my_melody_neutral", 0x0F6ED4);
    public static final InkColor SPLATFEST_PULP = splatoon("splatfest_pulp", 0xEE8122);
    public static final InkColor SPLATFEST_NO_PULP = splatoon("splatfest_no_pulp", 0x2FCF46);
    public static final InkColor SPLATFEST_PULP_VS_NO_PULP_NEUTRAL = splatoon("splatfest_pulp_vs_no_pulp_neutral", 0x42D0EA);
    public static final InkColor SPLATFEST_SQUID = splatoon("splatfest_squid", 0x50D525);
    public static final InkColor SPLATFEST_OCTOPUS = splatoon("splatfest_octopus", 0xE900D2);
    public static final InkColor SPLATFEST_SQUID_VS_OCTOPUS_NEUTRAL = splatoon("splatfest_squid_vs_octopus_neutral", 0x4B22C8);
    public static final InkColor SPLATFEST_MUSHROOM_MOUNTAIN_S2 = splatoon("splatfest_mushroom_mountain_s2", 0xFFCE0C);
    public static final InkColor SPLATFEST_BAMBOO_SHOOT_VILLAGE_S2 = splatoon("splatfest_bamboo_shoot_village_s2", 0x058F00);
    public static final InkColor SPLATFEST_MUSHROOM_MOUNTAIN_S2_VS_BAMBOO_SHOOT_VILLAGE_S2_NEUTRAL = splatoon("splatfest_mushroom_mountain_s2_vs_bamboo_shoot_village_s2_neutral", 0xE24FE5);
    public static final InkColor SPLATFEST_ADVENTURE = splatoon("splatfest_adventure", 0xFFB600);
    public static final InkColor SPLATFEST_RELAX = splatoon("splatfest_relax", 0x00967A);
    public static final InkColor SPLATFEST_ADVENTURE_VS_RELAX_NEUTRAL = splatoon("splatfest_adventure_vs_relax_neutral", 0x4B22C8);
    public static final InkColor SPLATFEST_FORK = splatoon("splatfest_fork", 0xE36D60);
    public static final InkColor SPLATFEST_SPOON = splatoon("splatfest_spoon", 0x2FB89A);
    public static final InkColor SPLATFEST_FORK_VS_SPOON_NEUTRAL = splatoon("splatfest_fork_vs_spoon_neutral", 0xEBCD62);
    public static final InkColor SPLATFEST_RETRO = splatoon("splatfest_retro", 0x6B4221);
    public static final InkColor SPLATFEST_MODERN = splatoon("splatfest_modern", 0x596666);
    public static final InkColor SPLATFEST_RETRO_VS_MODERN_NEUTRAL = splatoon("splatfest_retro_vs_modern_neutral", 0x0118E3);
    public static final InkColor SPLATFEST_TSUBUAN = splatoon("splatfest_tsubuan", 0x655A99);
    public static final InkColor SPLATFEST_KOSHIAN = splatoon("splatfest_koshian", 0x88214D);
    public static final InkColor SPLATFEST_TSUBUAN_VS_KOSHIAN_NEUTRAL = splatoon("splatfest_tsubuan_vs_koshian_neutral", 0x78D04F);
    public static final InkColor SPLATFEST_TRICK = splatoon("splatfest_trick", 0xEC7125);
    public static final InkColor SPLATFEST_TREAT = splatoon("splatfest_treat", 0x8805CC);
    public static final InkColor SPLATFEST_TRICK_VS_TREAT_NEUTRAL = splatoon("splatfest_trick_vs_treat_neutral", 0x4EE69F);
    public static final InkColor SPLATFEST_POCKY_CHOCOLATE = splatoon("splatfest_pocky_chocolate", 0xE70F21);
    public static final InkColor SPLATFEST_POCKY_GOKUBOSO = splatoon("splatfest_pocky_gokuboso", 0xE6E6E6);
    public static final InkColor SPLATFEST_POCKY_CHOCOLATE_VS_POCKY_GOKUBOSO_NEUTRAL = splatoon("splatfest_pocky_chocolate_vs_pocky_gokuboso_neutral", 0x734024);
    public static final InkColor SPLATFEST_SALSA = splatoon("splatfest_salsa", 0x801A00);
    public static final InkColor SPLATFEST_GUAC = splatoon("splatfest_guac", 0x757A2B);
    public static final InkColor SPLATFEST_SALSA_VS_GUAC_NEUTRAL = splatoon("splatfest_salsa_vs_guac_neutral", 0xD4C2B5);
    public static final InkColor SPLATFEST_EAT_IT = splatoon("splatfest_eat_it", 0xC42138);
    public static final InkColor SPLATFEST_SAVE_IT = splatoon("splatfest_save_it", 0xA6AD8C);
    public static final InkColor SPLATFEST_EAT_IT_VS_SAVE_IT_NEUTRAL = splatoon("splatfest_eat_it_vs_save_it_neutral", 0x6363D6);
    public static final InkColor SPLATFEST_HERO = splatoon("splatfest_hero", 0xED2403);
    public static final InkColor SPLATFEST_VILLAIN = splatoon("splatfest_villain", 0x4517D1);
    public static final InkColor SPLATFEST_HERO_VS_VILLAIN_NEUTRAL = splatoon("splatfest_hero_vs_villain_neutral", 0xD1D1D1);
    public static final InkColor SPLATFEST_FAM = splatoon("splatfest_fam", 0x7D5C26);
    public static final InkColor SPLATFEST_FRIEND = splatoon("splatfest_friend", 0x82878F);
    public static final InkColor SPLATFEST_FAM_VS_FRIEND_NEUTRAL = splatoon("splatfest_fam_vs_friend_neutral", 0xC21405);
    public static final InkColor SPLATFEST_BOKE = splatoon("splatfest_boke", 0xD64703);
    public static final InkColor SPLATFEST_TSUKKOMI = splatoon("splatfest_tsukkomi", 0x127A87);
    public static final InkColor SPLATFEST_BOKE_VS_TSUKKOMI_NEUTRAL = splatoon("splatfest_boke_vs_tsukkomi_neutral", 0xC2238F);
    public static final InkColor SPLATFEST_PANCAKE = splatoon("splatfest_pancake", 0xD4B873);
    public static final InkColor SPLATFEST_WAFFLE = splatoon("splatfest_waffle", 0x4545AB);
    public static final InkColor SPLATFEST_PANCAKE_VS_WAFFLE_NEUTRAL = splatoon("splatfest_pancake_vs_waffle_neutral", 0xE64D73);
    public static final InkColor SPLATFEST_KNIGHT = splatoon("splatfest_knight", 0x788C87);
    public static final InkColor SPLATFEST_WIZARD = splatoon("splatfest_wizard", 0x6B0A29);
    public static final InkColor SPLATFEST_KNIGHT_VS_WIZARD_NEUTRAL = splatoon("splatfest_knight_vs_wizard_neutral", 0xA67308);
    public static final InkColor SPLATFEST_HARE = splatoon("splatfest_hare", 0xE869BF);
    public static final InkColor SPLATFEST_TORTOISE = splatoon("splatfest_tortoise", 0x8ACF47);
    public static final InkColor SPLATFEST_HARE_VS_TORTOISE_NEUTRAL = splatoon("splatfest_hare_vs_tortoise_neutral", 0x17CFDE);
    public static final InkColor SPLATFEST_CE = splatoon("splatfest_ce", 0x086312);
    public static final InkColor SPLATFEST_PA = splatoon("splatfest_pa", 0x307087);
    public static final InkColor SPLATFEST_CE_VS_PA_NEUTRAL = splatoon("splatfest_ce_vs_pa_neutral", 0xBA0A07);
    public static final InkColor SPLATFEST_TIME_TRAVEL = splatoon("splatfest_time_travel", 0x695240);
    public static final InkColor SPLATFEST_TELEPORTATION = splatoon("splatfest_teleportation", 0x14247F);
    public static final InkColor SPLATFEST_TIME_TRAVEL_VS_TELEPORTATION_NEUTRAL = splatoon("splatfest_time_travel_vs_teleportation_neutral", 0xCFD1C7);
    public static final InkColor SPLATFEST_UNICORN = splatoon("splatfest_unicorn", 0x6B87BF);
    public static final InkColor SPLATFEST_NARWHAL = splatoon("splatfest_narwhal", 0xAB66B8);
    public static final InkColor SPLATFEST_UNICORN_VS_NARWHAL_NEUTRAL = splatoon("splatfest_unicorn_vs_narwhal_neutral", 0xD1ED73);
    public static final InkColor SPLATFEST_KID = splatoon("splatfest_kid", 0x308766);
    public static final InkColor SPLATFEST_GROWN_UP = splatoon("splatfest_grown_up", 0x523B40);
    public static final InkColor SPLATFEST_KID_VS_GROWN_UP_NEUTRAL = splatoon("splatfest_kid_vs_grown_up_neutral", 0xD1ED73);
    public static final InkColor SPLATFEST_WITHOUT_PINEAPPLE = splatoon("splatfest_without_pineapple", 0x4A2126);
    public static final InkColor SPLATFEST_WITH_PINEAPPLE = splatoon("splatfest_with_pineapple", 0xABB012);
    public static final InkColor SPLATFEST_WITHOUT_PINEAPPLE_VS_WITH_PINEAPPLE_NEUTRAL = splatoon("splatfest_without_pineapple_vs_with_pineapple_neutral", 0x0A6E17);
    public static final InkColor SPLATFEST_CHAOS = splatoon("splatfest_chaos", 0x695C3B);
    public static final InkColor SPLATFEST_ORDER = splatoon("splatfest_order", 0x7F7F99);
    public static final InkColor SPLATFEST_CHAOS_VS_ORDER_NEUTRAL = splatoon("splatfest_chaos_vs_order_neutral", 0x990F2B);
    public static final InkColor SPLATFEST_SUPER_MUSHROOM = splatoon("splatfest_super_mushroom", 0xCC1F1F);
    public static final InkColor SPLATFEST_SUPER_STAR = splatoon("splatfest_super_star", 0xB3A10D);
    public static final InkColor SPLATFEST_SUPER_MUSHROOM_VS_SUPER_STAR_NEUTRAL = splatoon("splatfest_super_mushroom_vs_super_star_neutral", 0x0D40DE);

    private static InkColor register(Identifier id, Color color) {
        return Registry.register(SplatcraftRegistries.INK_COLOR, id, new InkColor(color));
    }

    private static InkColor vanilla(String id, int color) {
        return register(new Identifier(id), Color.ofOpaque(color));
    }
    private static InkColor splatcraft(String id, int color) {
        return register(new Identifier(Splatcraft.MOD_ID, id), Color.ofOpaque(color));
    }
    private static InkColor splatoon(String id, int color) {
        return register(new Identifier("splatoon", id), Color.ofOpaque(color));
    }

    private static InkColor pure(String id, float red, float green, float blue) {
        return splatcraft("pure_%s".formatted(id), Color.ofRGB(red, green, blue).getColor());
    }

    private static InkColor dye(DyeColor dyeColor) {
        String id = dyeColor.getName();
        float[] colorComponents = dyeColor.getColorComponents();
        Color color = Color.ofRGB(colorComponents[0], colorComponents[1], colorComponents[2]);
        return vanilla(id, color.getColor());
    }
}
