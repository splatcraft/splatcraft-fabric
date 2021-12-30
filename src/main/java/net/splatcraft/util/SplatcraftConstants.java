package net.splatcraft.util;

import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.splatcraft.Splatcraft;

public class SplatcraftConstants {
    public static final EntityDimensions SQUID_FORM_DIMENSIONS = EntityDimensions.changing(0.6f, 0.6f);
    public static final EntityDimensions SQUID_FORM_SUBMERGED_DIMENSIONS = EntityDimensions.changing(0.6f, 0.5f);

    public static float getEyeHeight(boolean submerged) {
        return submerged ? SQUID_FORM_SUBMERGED_DIMENSIONS.height / 2.0f : SQUID_FORM_DIMENSIONS.height / 1.5f;
    }

    public static final String NBT_INK_COLOR = "InkColor";
    public static final String NBT_INK_TYPE = "InkType";
    public static final String NBT_SIZE = "Size";
    public static final String NBT_DROPS_INK = "DropsInk";
    public static final String NBT_IS_SQUID = "Squid";
    public static final String NBT_IS_SUBMERGED = "Submerged";
    public static final String NBT_HAS_SPLATFEST_BAND = "HasSplatfestBand";
    public static final String NBT_CONTAINED_INK = "ContainedInk";
    public static final String NBT_BLOCK_STATE = "BlockState";
    public static final String NBT_BLOCK_ENTITY_TAG = "BlockEntityTag";

    public static final String COMMAND_INK_COLOR = new Identifier(Splatcraft.MOD_ID, "inkcolor").toString();
    public static final String COMMAND_ARGUMENT_INK_COLOR = "ink_color";
    public static final String COMMAND_ARGUMENT_TARGETS = "targets";
    public static final String COMMAND_ARGUMENT_FROM = "from";
    public static final String COMMAND_ARGUMENT_TO = "to";

    public static final Identifier DEFAULT_INK_COLOR_IDENTIFIER = new Identifier(DyeColor.WHITE.getName());

    public static final String T_LOW_INK = text("low_ink");
    public static final String T_NOT_INSTALLED_ON_SERVER = text("not_installed_on_server.line");
    public static final String T_NOT_INSTALLED_ON_SERVER_1 = T_NOT_INSTALLED_ON_SERVER + 1;
    public static final String T_NOT_INSTALLED_ON_SERVER_2 = T_NOT_INSTALLED_ON_SERVER + 2;
    public static final String T_CONTAINED_INK = text("contained_ink");
    public static final String T_RELOADED_CONFIG = text("reloaded_config");
    public static final String T_INK_COLOR = text("ink_color");
    public static final String T_INK_COLOR_TEXT_DISPLAY = text("ink_color.icon");
    public static final String T_BLOCK_ENTITY_DESCRIPTION = text("block_entity_description");

    public static final String T_COMMAND_ERROR_INK_COLOR_NOT_FOUND = cmdInkColor("notFound");
    public static final String T_COMMAND_ERROR_INK_COLOR_TAG_DISALLOWED = cmdInkColor("tagDisallowed");
    public static final String T_COMMAND_ERROR_NO_ENTITIES_AFFECTED = cmdArg("entity.notAffected");
    public static final String T_COMMAND_ERROR_NO_INKABLES_AFFECTED = cmdArg("inkable.notAffected");
    public static final String T_COMMAND_INK_COLOR_GET = cmd("%s.get".formatted(COMMAND_INK_COLOR));
    public static final String T_COMMAND_INK_COLOR_SUCCESS_SINGLE = cmdSuc("%s.single".formatted(COMMAND_INK_COLOR));
    public static final String T_COMMAND_INK_COLOR_SUCCESS_MULTIPLE = cmdSuc("%s.multiple".formatted(COMMAND_INK_COLOR));

    private static String text(String id) { return "text.%s.%s".formatted(Splatcraft.MOD_ID, id); }

    private static String cmd         (String s) { return         "command.%s"     .formatted(s);                     }
    private static String cmdSuc      (String c) { return cmd   ( "%s.success"     .formatted(c));                    }
    private static String cmdErr      (String c) { return cmd   ( "%s.error"       .formatted(c));                    }
    private static String cmdArg      (String s) { return cmd   ( "%s.argument.%s" .formatted(Splatcraft.MOD_ID, s)); }
    private static String cmdInkColor (String s) { return cmdArg( "ink_color.%s"   .formatted(s));                    }

    public static final DynamicCommandExceptionType EXCEPTION_INK_COLOR_NOT_FOUND = new DynamicCommandExceptionType(c -> new TranslatableText(T_COMMAND_ERROR_INK_COLOR_NOT_FOUND, c));
    public static final SimpleCommandExceptionType EXCEPTION_NO_ENTITIES_AFFECTED = new SimpleCommandExceptionType(new TranslatableText(T_COMMAND_ERROR_NO_ENTITIES_AFFECTED));
    public static final SimpleCommandExceptionType EXCEPTION_NO_INKABLES_AFFECTED = new SimpleCommandExceptionType(new TranslatableText(T_COMMAND_ERROR_NO_INKABLES_AFFECTED));
}
