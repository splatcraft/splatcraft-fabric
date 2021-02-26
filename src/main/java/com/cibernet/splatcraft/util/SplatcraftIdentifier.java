package com.cibernet.splatcraft.util;

import com.cibernet.splatcraft.Splatcraft;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;

public class SplatcraftIdentifier extends Identifier {
    protected SplatcraftIdentifier(String[] identifier) {
        super(identifier);
    }

    public SplatcraftIdentifier(String identifier) {
        this(SplatcraftIdentifier.decompose(identifier, ':'));
    }

    public SplatcraftIdentifier(String namespace, String path) {
        super(namespace, path);
    }

    public static Identifier read(StringReader reader) throws CommandSyntaxException {
        int cursor = reader.getCursor();

        while(reader.canRead() && Identifier.isCharValid(reader.peek())) {
            reader.skip();
        }

        String identifier = reader.getString().substring(cursor, reader.getCursor());

        try {
            return new SplatcraftIdentifier(identifier);
        } catch (InvalidIdentifierException resourcelocationexception) {
            reader.setCursor(cursor);
            throw new SimpleCommandExceptionType(new TranslatableText("argument.id.invalid")).createWithContext(reader);
        }
    }

    protected static String[] decompose(String identifier, char splitOn) {
        String[] astring = new String[]{ Splatcraft.MOD_ID, identifier };
        int i = identifier.indexOf(splitOn);
        if (i >= 0) {
            astring[1] = identifier.substring(i + 1);
            if (i >= 1) {
                astring[0] = identifier.substring(0, i);
            }
        }

        return astring;
    }
}
