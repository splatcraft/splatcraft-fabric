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
    public static final String NBT_IS_SQUID = "Squid";
    public static final String NBT_IS_SUBMERGED = "Submerged";
    public static final String NBT_BLOCK_STATE = "BlockState";
    public static final String NBT_BLOCK_ENTITY_TAG = "BlockEntityTag";

    public static final Identifier COMMAND_INK_COLOR = new Identifier(Splatcraft.MOD_ID, "inkcolor");
    public static final String COMMAND_ARGUMENT_INK_COLOR = "ink_color";
    public static final String COMMAND_ARGUMENT_TARGETS = "targets";
    public static final String COMMAND_ARGUMENT_FROM = "from";
    public static final String COMMAND_ARGUMENT_TO = "to";

    public static final Identifier IDENTIFIER_DEFAULT_INK_COLOR = new Identifier(DyeColor.WHITE.getName());

    public static final String T_RELOADED_CONFIG = "text.%s.reloaded_config".formatted(Splatcraft.MOD_ID);
    public static final String T_RELOADED_CONFIG_FAILED = "%s.failed".formatted(T_RELOADED_CONFIG);
    public static final String T_INK_COLOR_TEXT_DISPLAY = "text.%s.ink_color_display".formatted(Splatcraft.MOD_ID);
    public static final String T_INK_COLOR_TEXT_DISPLAY_ICON = "%s.icon".formatted(T_INK_COLOR_TEXT_DISPLAY);
    public static final String T_BLOCK_ENTITY_DESCRIPTION = "text.%s.block_entity_description".formatted(Splatcraft.MOD_ID);
    public static final String T_COMMAND_ERROR_INK_COLOR_NOT_FOUND = cmdInkColor("notFound");
    public static final String T_COMMAND_ERROR_NO_ENTITIES_AFFECTED = cmdEntity("notAffected");
    public static final String T_COMMAND_INK_COLOR_GET = cmd("%s.get".formatted(COMMAND_INK_COLOR));
    public static final String T_COMMAND_INK_COLOR_SUCCESS_SINGLE = cmdSuc("%s.single".formatted(COMMAND_INK_COLOR));
    public static final String T_COMMAND_INK_COLOR_SUCCESS_MULTIPLE = cmdSuc("%s.multiple".formatted(COMMAND_INK_COLOR));

    private static String cmd         (String s) { return         "command.%s"     .formatted(s);                     }
    private static String cmdSuc      (String c) { return cmd   ( "%s.success"     .formatted(c));                    }
    private static String cmdErr      (String c) { return cmd   ( "%s.error"       .formatted(c));                    }
    private static String cmdArg      (String s) { return cmd   ( "%s.argument.%s" .formatted(Splatcraft.MOD_ID, s)); }
    private static String cmdInkColor (String s) { return cmdArg( "ink_color.%s"   .formatted(s));                    }
    private static String cmdEntity   (String s) { return cmdArg( "entity.%s"      .formatted(s));                    }

    public static final DynamicCommandExceptionType EXCEPTION_INK_COLOR_NOT_FOUND = new DynamicCommandExceptionType(c -> new TranslatableText(T_COMMAND_ERROR_INK_COLOR_NOT_FOUND, c));
    public static final SimpleCommandExceptionType EXCEPTION_NO_ENTITIES_AFFECTED = new SimpleCommandExceptionType(new TranslatableText(T_COMMAND_ERROR_NO_ENTITIES_AFFECTED));
}
