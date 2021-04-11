package com.cibernet.splatcraft.command;

import com.cibernet.splatcraft.util.StringConstants;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.text.TranslatableText;

public class SplatcraftCommandExceptions {
    /**
     * Thrown when no ink is found.
     */
    public static final SimpleCommandExceptionType NO_INK = new SimpleCommandExceptionType(new TranslatableText(StringConstants.TEXT_NO_INK));
    /**
     * Thrown when ink is found but none is set.
     */
    public static final SimpleCommandExceptionType NO_INK_SET = new SimpleCommandExceptionType(new TranslatableText(StringConstants.TEXT_NO_INK_SET));
}
