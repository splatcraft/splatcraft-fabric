package net.splatcraft.api.inkcolor;

import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.splatcraft.api.Splatcraft;
import net.splatcraft.api.registry.SplatcraftRegistries;
import net.splatcraft.api.util.Color;

import java.util.Random;

import static net.splatcraft.api.util.SplatcraftConstants.*;

public interface InkColors {
    /* Dye Colors */

    InkColor DYE_WHITE = dye(DyeColor.WHITE);
    InkColor DYE_ORANGE = dye(DyeColor.ORANGE);
    InkColor DYE_MAGENTA = dye(DyeColor.MAGENTA);
    InkColor DYE_LIGHT_BLUE = dye(DyeColor.LIGHT_BLUE);
    InkColor DYE_YELLOW = dye(DyeColor.YELLOW);
    InkColor DYE_LIME = dye(DyeColor.LIME);
    InkColor DYE_PINK = dye(DyeColor.PINK);
    InkColor DYE_GRAY = dye(DyeColor.GRAY);
    InkColor DYE_LIGHT_GRAY = dye(DyeColor.LIGHT_GRAY);
    InkColor DYE_CYAN = dye(DyeColor.CYAN);
    InkColor DYE_PURPLE = dye(DyeColor.PURPLE);
    InkColor DYE_BLUE = dye(DyeColor.BLUE);
    InkColor DYE_BROWN = dye(DyeColor.BROWN);
    InkColor DYE_GREEN = dye(DyeColor.GREEN);
    InkColor DYE_RED = dye(DyeColor.RED);
    InkColor DYE_BLACK = dye(DyeColor.BLACK);

    /* Splatcraft Colors */

    InkColor MOJANG = vanilla("mojang", ColorHelper.Argb.getArgb(255, 239, 50, 61)); // mojang logo background color
    InkColor COBALT = splatcraft("cobalt", 0x005682); // Cibernet
    InkColor ICE = splatcraft("ice", 0x88FFC1); // icearstorm
    InkColor FLORAL = splatcraft("floral", 0xFF9BEE); // floralQuaFloral
    InkColor PASTEL_YELLOW = splatcraft("pastel_yellow", 0xFDF674); // andante.

    /* Pure Colors */

    InkColor PURE_WHITE  = pure("white",  1.0f, 1.0f, 1.0f);
    InkColor PURE_BLACK  = pure("black",  0.0f, 0.0f, 0.0f);

    InkColor PURE_RED    = pure("red",    1.0f, 0.0f, 0.0f);
    InkColor PURE_YELLOW = pure("yellow", 1.0f, 1.0f, 0.0f);
    InkColor PURE_PURPLE = pure("purple", 1.0f, 0.0f, 1.0f);

    InkColor PURE_GREEN  = pure("green",  0.0f, 1.0f, 0.0f);
    InkColor PURE_CYAN   = pure("cyan",   0.0f, 1.0f, 1.0f);

    InkColor PURE_BLUE   = pure("blue",   0.0f, 0.0f, 1.0f);

    /* Splatoon: Default */

    InkColor PINK = splatoon("pink", 0xFE447D);
    InkColor ORANGE = splatoon("orange", 0xF78F2E);
    InkColor YELLOW_ORANGE = splatoon("yellow_orange", 0xFEDC0C);
    InkColor LIME_GREEN = splatoon("lime_green", 0xD1F20A);
    InkColor EMERALD_GREEN = splatoon("emerald_green", 0x5CD05B);
    InkColor TEAL = splatoon("teal", 0x03C1CD);
    InkColor BLUE = splatoon("blue", 0x0E10E6);
    InkColor VIOLET = splatoon("violet", 0x9208E7);
    InkColor RED_ORANGE = splatoon("red_orange", 0xFF4900);
    InkColor YELLOW = splatoon("yellow", 0xF3F354);
    InkColor MINT = splatoon("mint", 0xBFF1E5);
    InkColor GREEN = splatoon("green", 0x3BC335);
    InkColor SEA_GREEN = splatoon("sea_green", 0x00D18A);
    InkColor LIGHT_BLUE = splatoon("light_blue", 0x6565F9);
    InkColor DARK_BLUE = splatoon("dark_blue", 0x101AB3);
    InkColor FUCHSIA = splatoon("fuchsia", 0xD645C8);
    InkColor NEON_GREEN = splatoon("neon_green", 0x9AE528);
    InkColor CYAN = splatoon("cyan", 0x0ACDFE);
    InkColor NEON_ORANGE = splatoon("neon_orange", 0xFF9600);
    InkColor DARK_FUCHSIA = splatoon("dark_fuchsia", 0xB21CA1);
    InkColor TAN = splatoon("tan", 0xE1D6B6);
    InkColor DARK_ORANGE = splatoon("dark_orange", 0xE59D0D);
    InkColor MAGENTA = splatoon("magenta", 0xEC0B68);
    InkColor DARK_VIOLET = splatoon("dark_violet", 0x461362);
    InkColor IKA_MUSUME_BLUE = splatoon("ika_musume_blue", 0x00AAE4);
    InkColor FOREST_GREEN = splatoon("forest_green", 0x0EAA57);

    /* Splatoon: Game */

    InkColor S2G_BLUE = splatoon("regular_blue", 0x2922B5);
    InkColor S2G_GREEN = splatoon("regular_green", 0x5EB604);
    InkColor S2G_BLUE_VS_GREEN_NEUTRAL = splatoon("regular_blue_vs_green_neutral", 0xCA21A5);
    InkColor S2G_WINTER_GREEN = splatoon("regular_winter_green", 0x03B362);
    InkColor S2G_DARK_MAGENTA = splatoon("regular_dark_magenta", 0xB1008D);
    InkColor S2G_WINTER_GREEN_VS_DARK_MAGENTA_NEUTRAL = splatoon("regular_winter_green_vs_dark_magenta_neutral", 0xA8B700);
    InkColor S2G_SLIMY_GREEN = splatoon("regular_slimy_green", 0x25B100);
    InkColor S2G_GRAPE = splatoon("regular_grape", 0x571DB1);
    InkColor S2G_SLIMY_GREEN_VS_GRAPE_NEUTRAL = splatoon("regular_slimy_green_vs_grape_neutral", 0xCD2D7E);
    InkColor S2G_RICH_PURPLE = splatoon("regular_rich_purple", 0x7B0393);
    InkColor S2G_GREEN_APPLE = splatoon("regular_green_apple", 0x43BA05);
    InkColor S2G_RICH_PURPLE_VS_GREEN_APPLE_NEUTRAL = splatoon("regular_rich_purple_vs_green_apple_neutral", 0xDB7821);
    InkColor S2G_TURQUOISE = splatoon("regular_turquoise", 0x0CAE6E);
    InkColor S2G_PUMPKIN = splatoon("regular_pumpkin", 0xF75900);
    InkColor S2G_TURQUOISE_VS_PUMPKIN_NEUTRAL = splatoon("regular_turquoise_vs_pumpkin_neutral", 0x6A1EC1);
    InkColor S2G_YELLOW = splatoon("regular_yellow", 0xD9C100);
    InkColor S2G_TRUE_BLUE = splatoon("regular_true_blue", 0x007AC9);
    InkColor S2G_YELLOW_VS_TRUE_BLUE_NEUTRAL = splatoon("regular_yellow_vs_true_blue_neutral", 0xED580B);
    InkColor S2G_MUSTARD = splatoon("regular_mustard", 0xCE8003);
    InkColor S2G_PURPLE = splatoon("regular_purple", 0x9208B2);
    InkColor S2G_MUSTARD_VS_PURPLE_NEUTRAL = splatoon("regular_mustard_vs_purple_neutral", 0x1AB46A);

    /* Splatoon: Ranked */

    InkColor S2R_LEMON = splatoon("ranked/lemon", 0xBBC905);
    InkColor S2R_PLUM = splatoon("ranked/plum", 0x830B9C);
    InkColor S2R_SKY = splatoon("ranked/sky", 0x007EDC);
    InkColor S2R_GOLD = splatoon("ranked/gold", 0xE1A307);
    InkColor S2R_LIGHT_PINK = splatoon("ranked/light_pink", 0xD60E6E);
    InkColor S2R_BLUE = splatoon("ranked/blue", 0x311AA8);
    InkColor S2R_NEON_PINK = splatoon("ranked/neon_pink", 0xCF0466);
    InkColor S2R_NEON_GREEN = splatoon("ranked/neon_green", 0x17A80D);
    InkColor S2R_PINK = splatoon("ranked/pink", 0xCB0856);
    InkColor S2R_LIGHT_BLUE = splatoon("ranked/light_blue", 0x0199B8);
    InkColor S2R_RASPBERRY = splatoon("ranked/raspberry", 0xDE0B64);
    InkColor S2R_NEON_YELLOW = splatoon("ranked/neon_yellow", 0xBFD002);
    InkColor S2R_DARK_PURPLE = splatoon("ranked/dark_purple", 0x4A14AA);
    InkColor S2R_ORANGE = splatoon("ranked/orange", 0xFB5C03);
    InkColor S2R_GRAPE = splatoon("ranked/grape", 0x5F0FB4);
    InkColor S2R_TURQUOISE = splatoon("ranked/turquoise", 0x08B672);

    /* Splatoon: Color Lock */

    InkColor S2CL_GOLDEN_YELLOW = splatoon("color_lock/golden_yellow", 0xDEA801);
    InkColor S2CL_EELECTRIC_BLUE = splatoon("color_lock/eelectric_blue", 0x4717A9);

    /* Splatoon: Splatfest */

    InkColor SPLATFEST_CATS = splatoon("splatfest/cats", 0xD84011);
    InkColor SPLATFEST_DOGS = splatoon("splatfest/dogs", 0x41A782);
    InkColor SPLATFEST_ROLLER_COASTERS = splatoon("splatfest/roller_coasters", 0x3BC335);
    InkColor SPLATFEST_WATER_SLIDES = splatoon("splatfest/water_slides", 0xB400FF);
    InkColor SPLATFEST_AUTOBOTS = splatoon("splatfest/autobots", 0xEC0B68);
    InkColor SPLATFEST_DECEPTICONS = splatoon("splatfest/decepticons", 0x461362);
    InkColor SPLATFEST_ART = splatoon("splatfest/art", 0x9208E7);
    InkColor SPLATFEST_SCIENCE = splatoon("splatfest/science", 0xF78F2E);
    InkColor SPLATFEST_MESSY = splatoon("splatfest/messy", 0xED7004);
    InkColor SPLATFEST_TIDY = splatoon("splatfest/tidy", 0x0037B8);
    InkColor SPLATFEST_CARS = splatoon("splatfest/cars", 0xF78F2E);
    InkColor SPLATFEST_PLANES = splatoon("splatfest/planes", 0x0E10E6);
    InkColor SPLATFEST_PIRATES = splatoon("splatfest/pirates", 0x4F9E00);
    InkColor SPLATFEST_NINJAS = splatoon("splatfest/ninjas", 0x7D26B5);
    InkColor SPLATFEST_LOVE = splatoon("splatfest/love", 0xDA3781);
    InkColor SPLATFEST_MONEY = splatoon("splatfest/money", 0xFEDC0C);
    InkColor SPLATFEST_MOUNTAIN_FOOD = splatoon("splatfest/mountain_food", 0xBA2763);
    InkColor SPLATFEST_SEA_FOOD = splatoon("splatfest/sea_food", 0x62BA9B);
    InkColor SPLATFEST_BURGERS = splatoon("splatfest/burgers", 0xF78F2E);
    InkColor SPLATFEST_PIZZA = splatoon("splatfest/pizza", 0x3BC335);
    InkColor SPLATFEST_NAUGHTY = splatoon("splatfest/naughty", 0xCF3350);
    InkColor SPLATFEST_NICE = splatoon("splatfest/nice", 0x008040);
    InkColor SPLATFEST_PAST = splatoon("splatfest/past", 0x009A82);
    InkColor SPLATFEST_FUTURE = splatoon("splatfest/future", 0xA34C3B);
    InkColor SPLATFEST_PERFECT_BODY = splatoon("splatfest/perfect_body", 0x32A200);
    InkColor SPLATFEST_PERFECT_BRAIN = splatoon("splatfest/perfect_brain", 0xF9891B);
    InkColor SPLATFEST_BARBARIAN_EUOC = splatoon("splatfest/barbarian_euoc", 0xC54D4F);
    InkColor SPLATFEST_NINJA_EUOC = splatoon("splatfest/ninja_euoc", 0x003B89);
    InkColor SPLATFEST_POKEMON_RED = splatoon("splatfest/pokemon_red", 0xBF586B);
    InkColor SPLATFEST_POKEMON_BLUE = splatoon("splatfest/pokemon_blue", 0x003DA2);
    InkColor SPLATFEST_POKEMON_GREEN = splatoon("splatfest/pokemon_green", 0x009A6E);
    InkColor SPLATFEST_GO_ALL_OUT = splatoon("splatfest/go_all_out", 0xBC326D);
    InkColor SPLATFEST_FOCUS_ON_HEALING = splatoon("splatfest/focus_on_healing", 0xC0AE26);
    InkColor SPLATFEST_HOVERBOARD = splatoon("splatfest/hoverboard", 0x59BE9C);
    InkColor SPLATFEST_JET_PACK = splatoon("splatfest/jet_pack", 0xE75A2D);
    InkColor SPLATFEST_SNOWMAN = splatoon("splatfest/snowman", 0x0EB6A7);
    InkColor SPLATFEST_SANDCASTLE = splatoon("splatfest/sandcastle", 0xFAB01D);
    InkColor SPLATFEST_SPONGEBOB = splatoon("splatfest/spongebob", 0xDCA41D);
    InkColor SPLATFEST_PATRICK = splatoon("splatfest/patrick", 0xE95F9A);
    InkColor SPLATFEST_TUNA_MAYONNAISE = splatoon("splatfest/tuna_mayonnaise", 0x38C8D0);
    InkColor SPLATFEST_RED_SALMON = splatoon("splatfest/red_salmon", 0xE56F87);
    InkColor SPLATFEST_FANCY_PARTY = splatoon("splatfest/fancy_party", 0x0EB6A7);
    InkColor SPLATFEST_COSTUME_PARTY = splatoon("splatfest/costume_party", 0xFAB01D);
    InkColor SPLATFEST_WORLD_TOUR = splatoon("splatfest/world_tour", 0x88AF45);
    InkColor SPLATFEST_SPACE_ADVENTURE = splatoon("splatfest/space_adventure", 0x014BD0);
    InkColor SPLATFEST_MUSHROOM_MOUNTAIN = splatoon("splatfest/mushroom_mountain", 0xF0C016);
    InkColor SPLATFEST_BAMBOO_SHOOT_VILLAGE = splatoon("splatfest/bamboo_shoot_village", 0x56AF45);
    InkColor SPLATFEST_EARLY_BIRD = splatoon("splatfest/early_bird", 0xF9891B);
    InkColor SPLATFEST_NIGHT_OWL = splatoon("splatfest/night_owl", 0x891BEC);
    InkColor SPLATFEST_CALLIE = splatoon("splatfest/callie", 0xAF16AC);
    InkColor SPLATFEST_MARIE = splatoon("splatfest/marie", 0x71DA0C);
    InkColor SPLATFEST_MAYO = splatoon("splatfest/mayo", 0xFFE99B);
    InkColor SPLATFEST_KETCHUP = splatoon("splatfest/ketchup", 0xFB321E);
    InkColor SPLATFEST_MAYO_VS_KETCHUP_NEUTRAL = splatoon("splatfest/mayo_vs_ketchup_neutral", 0x1BB026);
    InkColor SPLATFEST_FLIGHT = splatoon("splatfest/flight", 0x4F55ED);
    InkColor SPLATFEST_INVISIBILITY = splatoon("splatfest/invisibility", 0x6BD52C);
    InkColor SPLATFEST_FLIGHT_VS_INVISIBILITY_NEUTRAL = splatoon("splatfest/flight_vs_invisibility_neutral", 0xED1D9A);
    InkColor SPLATFEST_FRIES = splatoon("splatfest/fries", 0xE8540A);
    InkColor SPLATFEST_MCNUGGETS = splatoon("splatfest/mcnuggets", 0x6A3BE0);
    InkColor SPLATFEST_FRIES_VS_MCNUGGETS_NEUTRAL = splatoon("splatfest/fries_vs_mcnuggets_neutral", 0xFFCA0E);
    InkColor SPLATFEST_FRONT_ROLL = splatoon("splatfest/front_roll", 0xFF7D9A);
    InkColor SPLATFEST_BACK_ROLL = splatoon("splatfest/back_roll", 0x00A0B0);
    InkColor SPLATFEST_FRONT_ROLL_VS_BACK_ROLL_NEUTRAL = splatoon("splatfest/front_roll_vs_back_roll_neutral", 0xE4E567);
    InkColor SPLATFEST_VAMPIRE = splatoon("splatfest/vampire", 0x6735AF);
    InkColor SPLATFEST_WEREWOLF = splatoon("splatfest/werewolf", 0xFE6829);
    InkColor SPLATFEST_VAMPIRE_VS_WEREWOLF_NEUTRAL = splatoon("splatfest/vampire_vs_werewolf_neutral", 0x00CA0E);
    InkColor SPLATFEST_AGILITY = splatoon("splatfest/agility", 0xF3B000);
    InkColor SPLATFEST_ENDURANCE = splatoon("splatfest/endurance", 0xF5498B);
    InkColor SPLATFEST_AGILITY_VS_ENDURANCE_NEUTRAL = splatoon("splatfest/agility_vs_endurance_neutral", 0x13A715);
    InkColor SPLATFEST_WARM = splatoon("splatfest/warm", 0xFF5E17);
    InkColor SPLATFEST_COLD = splatoon("splatfest/cold", 0x00A39A);
    InkColor SPLATFEST_WARM_VS_COLD_NEUTRAL = splatoon("splatfest/warm_vs_cold_neutral", 0xEDD926);
    InkColor SPLATFEST_WITH_LEMON = splatoon("splatfest/with_lemon", 0xBEE600);
    InkColor SPLATFEST_WITHOUT_LEMON = splatoon("splatfest/without_lemon", 0xC52ADB);
    InkColor SPLATFEST_WITH_LEMON_VS_WITHOUT_LEMON_NEUTRAL = splatoon("splatfest/with_lemon_vs_without_lemon_neutral", 0x00B278);
    InkColor SPLATFEST_SCI_FI = splatoon("splatfest/sci_fi", 0x1FAFE8);
    InkColor SPLATFEST_FANTASY = splatoon("splatfest/fantasy", 0x8BFF06);
    InkColor SPLATFEST_SCI_FI_VS_FANTASY_NEUTRAL = splatoon("splatfest/sci_fi_vs_fantasy_neutral", 0xFD636A);
    InkColor SPLATFEST_FILM = splatoon("splatfest/film", 0x9030FF);
    InkColor SPLATFEST_BOOK = splatoon("splatfest/book", 0x14B8A0);
    InkColor SPLATFEST_FILM_VS_BOOK_NEUTRAL = splatoon("splatfest/film_vs_book_neutral", 0xEEC70E);
    InkColor SPLATFEST_WARM_INNER_WEAR = splatoon("splatfest/warm_inner_wear", 0xFF7536);
    InkColor SPLATFEST_WARM_OUTER_WEAR = splatoon("splatfest/warm_outer_wear", 0x9090BA);
    InkColor SPLATFEST_WARM_INNER_WEAR_VS_WARM_OUTER_WEAR_NEUTRAL = splatoon("splatfest/warm_inner_wear_vs_warm_outer_wear_neutral", 0xDC1C1E);
    InkColor SPLATFEST_SWEATER = splatoon("splatfest/sweater", 0xC4354D);
    InkColor SPLATFEST_SOCK = splatoon("splatfest/sock", 0x0D8B51);
    InkColor SPLATFEST_SWEATER_VS_SOCK_NEUTRAL = splatoon("splatfest/sweater_vs_sock_neutral", 0xCACACA);
    InkColor SPLATFEST_ACTION = splatoon("splatfest/action", 0xFA8E00);
    InkColor SPLATFEST_COMEDY = splatoon("splatfest/comedy", 0x1384BA);
    InkColor SPLATFEST_ACTION_VS_COMEDY_NEUTRAL = splatoon("splatfest/action_vs_comedy_neutral", 0xB44AEF);
    InkColor SPLATFEST_CHAMPION = splatoon("splatfest/champion", 0xA18E3B);
    InkColor SPLATFEST_CHALLENGER = splatoon("splatfest/challenger", 0x1D07AC);
    InkColor SPLATFEST_CHAMPION_VS_CHALLENGER_NEUTRAL = splatoon("splatfest/champion_vs_challenger_neutral", 0xF52E71);
    InkColor SPLATFEST_GHERK_OUT = splatoon("splatfest/gherk_out", 0xB14A8D);
    InkColor SPLATFEST_GHERK_IN = splatoon("splatfest/gherk_in", 0x83C91A);
    InkColor SPLATFEST_GHERK_OUT_VS_GHERK_IN_NEUTRAL = splatoon("splatfest/gherk_out_vs_gherk_in_neutral", 0x0FBA9D);
    InkColor SPLATFEST_MONEY_S2 = splatoon("splatfest/money_s2", 0xFFD624);
    InkColor SPLATFEST_LOVE_S2 = splatoon("splatfest/love_s2", 0xFC6D75);
    InkColor SPLATFEST_MONEY_S2_VS_LOVE_S2_NEUTRAL = splatoon("splatfest/money_s2_vs_love_s2_neutral", 0x5451EC);
    InkColor SPLATFEST_FLOWER = splatoon("splatfest/flower", 0xFF8787);
    InkColor SPLATFEST_DUMPLING = splatoon("splatfest/dumpling", 0x7EC27A);
    InkColor SPLATFEST_FLOWER_VS_DUMPLING_NEUTRAL = splatoon("splatfest/flower_vs_dumpling_neutral", 0xECE8B1);
    InkColor SPLATFEST_CHICKEN = splatoon("splatfest/chicken", 0xEFF2EB);
    InkColor SPLATFEST_EGG = splatoon("splatfest/egg", 0xFABF50);
    InkColor SPLATFEST_CHICKEN_VS_EGG_NEUTRAL = splatoon("splatfest/chicken_vs_egg_neutral", 0xED2635);
    InkColor SPLATFEST_LATEST_MODEL = splatoon("splatfest/latest_model", 0x059E9C);
    InkColor SPLATFEST_POPULAR_MODEL = splatoon("splatfest/popular_model", 0xC1CC47);
    InkColor SPLATFEST_LATEST_MODEL_VS_POPULAR_MODEL_NEUTRAL = splatoon("splatfest/latest_model_vs_popular_model_neutral", 0xE2E2E2);
    InkColor SPLATFEST_BASEBALL = splatoon("splatfest/baseball", 0xFA5A2A);
    InkColor SPLATFEST_SOCCER = splatoon("splatfest/soccer", 0x00993A);
    InkColor SPLATFEST_BASEBALL_VS_SOCCER_NEUTRAL = splatoon("splatfest/baseball_vs_soccer_neutral", 0x6642D0);
    InkColor SPLATFEST_SALTY = splatoon("splatfest/salty", 0xB1DBD1);
    InkColor SPLATFEST_SWEET = splatoon("splatfest/sweet", 0xE29440);
    InkColor SPLATFEST_SALTY_VS_SWEET_NEUTRAL = splatoon("splatfest/salty_vs_sweet_neutral", 0xD94C79);
    InkColor SPLATFEST_UNKNOWN_CREATURE = splatoon("splatfest/unknown_creature", 0xFF6D40);
    InkColor SPLATFEST_ADVANCED_TECHNOLOGY = splatoon("splatfest/advanced_technology", 0x38377A);
    InkColor SPLATFEST_UNKNOWN_CREATURE_VS_ADVANCED_TECHNOLOGY_NEUTRAL = splatoon("splatfest/unknown_creature_vs_advanced_technology_neutral", 0xF1E61C);
    InkColor SPLATFEST_RAPH = splatoon("splatfest/raph", 0xE23253);
    InkColor SPLATFEST_LEO = splatoon("splatfest/leo", 0x2A87C6);
    InkColor SPLATFEST_MIKEY = splatoon("splatfest/mikey", 0xF89620);
    InkColor SPLATFEST_DONNIE = splatoon("splatfest/donnie", 0x5233A8);
    InkColor SPLATFEST_TMNT_NEUTRAL = splatoon("splatfest/tmnt_neutral", 0x2D7F4E);
    InkColor SPLATFEST_HELLO_KITTY = splatoon("splatfest/hello_kitty", 0xFADDDD);
    InkColor SPLATFEST_CINNAMOROLL = splatoon("splatfest/cinnamoroll", 0x0B7DE7);
    InkColor SPLATFEST_HELLO_KITTY_VS_CINNAMOROLL_NEUTRAL = splatoon("splatfest/hello_kitty_vs_cinnamoroll_neutral", 0xF385A1);
    InkColor SPLATFEST_MY_MELODY = splatoon("splatfest/my_melody", 0xFF3B8C);
    InkColor SPLATFEST_POMPOMPURIN = splatoon("splatfest/pompompurin", 0xF4CC00);
    InkColor SPLATFEST_MY_MELODY_VS_POMPOMPURIN_NEUTRAL = splatoon("splatfest/my_melody_vs_pompompurin_neutral", 0x0F6ED4);
    InkColor SPLATFEST_HELLO_KITTY_VS_MY_MELODY_NEUTRAL = splatoon("splatfest/hello_kitty_vs_my_melody_neutral", 0x0F6ED4);
    InkColor SPLATFEST_PULP = splatoon("splatfest/pulp", 0xEE8122);
    InkColor SPLATFEST_NO_PULP = splatoon("splatfest/no_pulp", 0x2FCF46);
    InkColor SPLATFEST_PULP_VS_NO_PULP_NEUTRAL = splatoon("splatfest/pulp_vs_no_pulp_neutral", 0x42D0EA);
    InkColor SPLATFEST_SQUID = splatoon("splatfest/squid", 0x50D525);
    InkColor SPLATFEST_OCTOPUS = splatoon("splatfest/octopus", 0xE900D2);
    InkColor SPLATFEST_SQUID_VS_OCTOPUS_NEUTRAL = splatoon("splatfest/squid_vs_octopus_neutral", 0x4B22C8);
    InkColor SPLATFEST_MUSHROOM_MOUNTAIN_S2 = splatoon("splatfest/mushroom_mountain_s2", 0xFFCE0C);
    InkColor SPLATFEST_BAMBOO_SHOOT_VILLAGE_S2 = splatoon("splatfest/bamboo_shoot_village_s2", 0x058F00);
    InkColor SPLATFEST_MUSHROOM_MOUNTAIN_S2_VS_BAMBOO_SHOOT_VILLAGE_S2_NEUTRAL = splatoon("splatfest/mushroom_mountain_s2_vs_bamboo_shoot_village_s2_neutral", 0xE24FE5);
    InkColor SPLATFEST_ADVENTURE = splatoon("splatfest/adventure", 0xFFB600);
    InkColor SPLATFEST_RELAX = splatoon("splatfest/relax", 0x00967A);
    InkColor SPLATFEST_ADVENTURE_VS_RELAX_NEUTRAL = splatoon("splatfest/adventure_vs_relax_neutral", 0x4B22C8);
    InkColor SPLATFEST_FORK = splatoon("splatfest/fork", 0xE36D60);
    InkColor SPLATFEST_SPOON = splatoon("splatfest/spoon", 0x2FB89A);
    InkColor SPLATFEST_FORK_VS_SPOON_NEUTRAL = splatoon("splatfest/fork_vs_spoon_neutral", 0xEBCD62);
    InkColor SPLATFEST_RETRO = splatoon("splatfest/retro", 0x6B4221);
    InkColor SPLATFEST_MODERN = splatoon("splatfest/modern", 0x596666);
    InkColor SPLATFEST_RETRO_VS_MODERN_NEUTRAL = splatoon("splatfest/retro_vs_modern_neutral", 0x0118E3);
    InkColor SPLATFEST_TSUBUAN = splatoon("splatfest/tsubuan", 0x655A99);
    InkColor SPLATFEST_KOSHIAN = splatoon("splatfest/koshian", 0x88214D);
    InkColor SPLATFEST_TSUBUAN_VS_KOSHIAN_NEUTRAL = splatoon("splatfest/tsubuan_vs_koshian_neutral", 0x78D04F);
    InkColor SPLATFEST_TRICK = splatoon("splatfest/trick", 0xEC7125);
    InkColor SPLATFEST_TREAT = splatoon("splatfest/treat", 0x8805CC);
    InkColor SPLATFEST_TRICK_VS_TREAT_NEUTRAL = splatoon("splatfest/trick_vs_treat_neutral", 0x4EE69F);
    InkColor SPLATFEST_POCKY_CHOCOLATE = splatoon("splatfest/pocky_chocolate", 0xE70F21);
    InkColor SPLATFEST_POCKY_GOKUBOSO = splatoon("splatfest/pocky_gokuboso", 0xE6E6E6);
    InkColor SPLATFEST_POCKY_CHOCOLATE_VS_POCKY_GOKUBOSO_NEUTRAL = splatoon("splatfest/pocky_chocolate_vs_pocky_gokuboso_neutral", 0x734024);
    InkColor SPLATFEST_SALSA = splatoon("splatfest/salsa", 0x801A00);
    InkColor SPLATFEST_GUAC = splatoon("splatfest/guac", 0x757A2B);
    InkColor SPLATFEST_SALSA_VS_GUAC_NEUTRAL = splatoon("splatfest/salsa_vs_guac_neutral", 0xD4C2B5);
    InkColor SPLATFEST_EAT_IT = splatoon("splatfest/eat_it", 0xC42138);
    InkColor SPLATFEST_SAVE_IT = splatoon("splatfest/save_it", 0xA6AD8C);
    InkColor SPLATFEST_EAT_IT_VS_SAVE_IT_NEUTRAL = splatoon("splatfest/eat_it_vs_save_it_neutral", 0x6363D6);
    InkColor SPLATFEST_HERO = splatoon("splatfest/hero", 0xED2403);
    InkColor SPLATFEST_VILLAIN = splatoon("splatfest/villain", 0x4517D1);
    InkColor SPLATFEST_HERO_VS_VILLAIN_NEUTRAL = splatoon("splatfest/hero_vs_villain_neutral", 0xD1D1D1);
    InkColor SPLATFEST_FAM = splatoon("splatfest/fam", 0x7D5C26);
    InkColor SPLATFEST_FRIEND = splatoon("splatfest/friend", 0x82878F);
    InkColor SPLATFEST_FAM_VS_FRIEND_NEUTRAL = splatoon("splatfest/fam_vs_friend_neutral", 0xC21405);
    InkColor SPLATFEST_BOKE = splatoon("splatfest/boke", 0xD64703);
    InkColor SPLATFEST_TSUKKOMI = splatoon("splatfest/tsukkomi", 0x127A87);
    InkColor SPLATFEST_BOKE_VS_TSUKKOMI_NEUTRAL = splatoon("splatfest/boke_vs_tsukkomi_neutral", 0xC2238F);
    InkColor SPLATFEST_PANCAKE = splatoon("splatfest/pancake", 0xD4B873);
    InkColor SPLATFEST_WAFFLE = splatoon("splatfest/waffle", 0x4545AB);
    InkColor SPLATFEST_PANCAKE_VS_WAFFLE_NEUTRAL = splatoon("splatfest/pancake_vs_waffle_neutral", 0xE64D73);
    InkColor SPLATFEST_KNIGHT = splatoon("splatfest/knight", 0x788C87);
    InkColor SPLATFEST_WIZARD = splatoon("splatfest/wizard", 0x6B0A29);
    InkColor SPLATFEST_KNIGHT_VS_WIZARD_NEUTRAL = splatoon("splatfest/knight_vs_wizard_neutral", 0xA67308);
    InkColor SPLATFEST_HARE = splatoon("splatfest/hare", 0xE869BF);
    InkColor SPLATFEST_TORTOISE = splatoon("splatfest/tortoise", 0x8ACF47);
    InkColor SPLATFEST_HARE_VS_TORTOISE_NEUTRAL = splatoon("splatfest/hare_vs_tortoise_neutral", 0x17CFDE);
    InkColor SPLATFEST_CE = splatoon("splatfest/ce", 0x086312);
    InkColor SPLATFEST_PA = splatoon("splatfest/pa", 0x307087);
    InkColor SPLATFEST_CE_VS_PA_NEUTRAL = splatoon("splatfest/ce_vs_pa_neutral", 0xBA0A07);
    InkColor SPLATFEST_TIME_TRAVEL = splatoon("splatfest/time_travel", 0x695240);
    InkColor SPLATFEST_TELEPORTATION = splatoon("splatfest/teleportation", 0x14247F);
    InkColor SPLATFEST_TIME_TRAVEL_VS_TELEPORTATION_NEUTRAL = splatoon("splatfest/time_travel_vs_teleportation_neutral", 0xCFD1C7);
    InkColor SPLATFEST_UNICORN = splatoon("splatfest/unicorn", 0x6B87BF);
    InkColor SPLATFEST_NARWHAL = splatoon("splatfest/narwhal", 0xAB66B8);
    InkColor SPLATFEST_UNICORN_VS_NARWHAL_NEUTRAL = splatoon("splatfest/unicorn_vs_narwhal_neutral", 0xD1ED73);
    InkColor SPLATFEST_KID = splatoon("splatfest/kid", 0x308766);
    InkColor SPLATFEST_GROWN_UP = splatoon("splatfest/grown_up", 0x523B40);
    InkColor SPLATFEST_KID_VS_GROWN_UP_NEUTRAL = splatoon("splatfest/kid_vs_grown_up_neutral", 0xD1ED73);
    InkColor SPLATFEST_WITHOUT_PINEAPPLE = splatoon("splatfest/without_pineapple", 0x4A2126);
    InkColor SPLATFEST_WITH_PINEAPPLE = splatoon("splatfest/with_pineapple", 0xABB012);
    InkColor SPLATFEST_WITHOUT_PINEAPPLE_VS_WITH_PINEAPPLE_NEUTRAL = splatoon("splatfest/without_pineapple_vs_with_pineapple_neutral", 0x0A6E17);
    InkColor SPLATFEST_CHAOS = splatoon("splatfest/chaos", 0x695C3B);
    InkColor SPLATFEST_ORDER = splatoon("splatfest/order", 0x7F7F99);
    InkColor SPLATFEST_CHAOS_VS_ORDER_NEUTRAL = splatoon("splatfest/chaos_vs_order_neutral", 0x990F2B);
    InkColor SPLATFEST_SUPER_MUSHROOM = splatoon("splatfest/super_mushroom", 0xCC1F1F);
    InkColor SPLATFEST_SUPER_STAR = splatoon("splatfest/super_star", 0xB3A10D);
    InkColor SPLATFEST_SUPER_MUSHROOM_VS_SUPER_STAR_NEUTRAL = splatoon("splatfest/super_mushroom_vs_super_star_neutral", 0x0D40DE);

    static InkColor getDefault() {
        return SplatcraftRegistries.INK_COLOR.get(DEFAULT_INK_COLOR_IDENTIFIER);
    }

    private static InkColor register(Identifier id, Color color) {
        return Registry.register(SplatcraftRegistries.INK_COLOR, id, new InkColor(color));
    }

    private static InkColor vanilla(String id, int color) {
        return register(new Identifier(id), Color.of(color));
    }

    private static InkColor splatcraft(String id, int color) {
        return register(new Identifier(Splatcraft.MOD_ID, id), Color.of(color));
    }

    private static InkColor splatoon(String id, int color) {
        return register(new Identifier("splatoon", id), Color.of(color));
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

    static InkColor random(Random random) {
        return SplatcraftRegistries.INK_COLOR.getRandom(random)
                                             .map(RegistryEntry::value)
                                             .orElse(getDefault());
    }

    static InkColor random() {
        return random(new Random());
    }
}
