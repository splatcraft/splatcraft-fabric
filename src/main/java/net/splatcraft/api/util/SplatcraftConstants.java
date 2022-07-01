package net.splatcraft.api.util;

import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.splatcraft.api.Splatcraft;

public interface SplatcraftConstants {
    EntityDimensions SQUID_FORM_DIMENSIONS = EntityDimensions.changing(0.6f, 0.6f);
    EntityDimensions SQUID_FORM_SUBMERGED_DIMENSIONS = EntityDimensions.changing(0.6f, 0.5f);

    static float getEyeHeight(boolean submerged) {
        return submerged ? SQUID_FORM_SUBMERGED_DIMENSIONS.height / 2.0f : SQUID_FORM_DIMENSIONS.height / 1.5f;
    }

    int MAX_INK_OVERLAYS = 3;

    String NBT_INK_COLOR = "InkColor";
    String NBT_INK_TYPE = "InkType";
    String NBT_SOURCE = "Source";
    String NBT_DROPS_INK = "DropsInk";
    String NBT_IS_SQUID = "Squid";
    String NBT_IS_SUBMERGED = "Submerged";
    String NBT_HAS_SPLATFEST_BAND = "HasSplatfestBand";
    String NBT_INITIALIZED = "Initialized";
    String NBT_CONTAINED_INK = "ContainedInk";
    String NBT_BLOCK_STATE = "BlockState";
    String NBT_BLOCK_ENTITY_TAG = "BlockEntityTag";

    String COMMAND_INK_COLOR = new Identifier(Splatcraft.MOD_ID, "inkcolor").toString();
    String COMMAND_ARGUMENT_INK_COLOR = "ink_color";
    String COMMAND_ARGUMENT_TARGETS = "targets";
    String COMMAND_ARGUMENT_FROM = "from";
    String COMMAND_ARGUMENT_TO = "to";

    Identifier DEFAULT_INK_COLOR_IDENTIFIER = new Identifier(DyeColor.WHITE.getName());

    String T_LOW_INK = text("low_ink");
    String T_NOT_INSTALLED_ON_SERVER = text("not_installed_on_server.line");
    String T_NOT_INSTALLED_ON_SERVER_1 = T_NOT_INSTALLED_ON_SERVER + 1;
    String T_NOT_INSTALLED_ON_SERVER_2 = T_NOT_INSTALLED_ON_SERVER + 2;
    String T_CONTAINED_INK = text("contained_ink");
    String T_RELOADED_CONFIG = text("reloaded_config");
    String T_INK_COLOR = text("ink_color");
    String T_INK_COLOR_TEXT_DISPLAY = text("ink_color.icon");
    String T_BLOCK_ENTITY_DESCRIPTION = text("block_entity_description");

    String T_COMMAND_ERROR_INK_COLOR_NOT_FOUND = cmdInkColor("notFound");
    String T_COMMAND_ERROR_INK_COLOR_TAG_DISALLOWED = cmdInkColor("tagDisallowed");
    String T_COMMAND_ERROR_NO_ENTITIES_AFFECTED = cmdArg("entity.notAffected");
    String T_COMMAND_ERROR_NO_INKABLES_AFFECTED = cmdArg("inkable.notAffected");
    String T_COMMAND_INK_COLOR_GET = cmd("%s.get".formatted(COMMAND_INK_COLOR));
    String T_COMMAND_INK_COLOR_SUCCESS_SINGLE = cmdSuc("%s.single".formatted(COMMAND_INK_COLOR));
    String T_COMMAND_INK_COLOR_SUCCESS_MULTIPLE = cmdSuc("%s.multiple".formatted(COMMAND_INK_COLOR));

    private static String text(String id) { return "text.%s.%s".formatted(Splatcraft.MOD_ID, id); }

    private static String cmd         (String s) { return         "command.%s"     .formatted(s);                     }
    private static String cmdSuc      (String c) { return cmd   ( "%s.success"     .formatted(c));                    }
    private static String cmdErr      (String c) { return cmd   ( "%s.error"       .formatted(c));                    }
    private static String cmdArg      (String s) { return cmd   ( "%s.argument.%s" .formatted(Splatcraft.MOD_ID, s)); }
    private static String cmdInkColor (String s) { return cmdArg( "ink_color.%s"   .formatted(s));                    }

    DynamicCommandExceptionType EXCEPTION_INK_COLOR_NOT_FOUND = new DynamicCommandExceptionType(c -> Text.translatable(T_COMMAND_ERROR_INK_COLOR_NOT_FOUND, c));
    SimpleCommandExceptionType EXCEPTION_NO_ENTITIES_AFFECTED = new SimpleCommandExceptionType(Text.translatable(T_COMMAND_ERROR_NO_ENTITIES_AFFECTED));
    SimpleCommandExceptionType EXCEPTION_NO_INKABLES_AFFECTED = new SimpleCommandExceptionType(Text.translatable(T_COMMAND_ERROR_NO_INKABLES_AFFECTED));
}
