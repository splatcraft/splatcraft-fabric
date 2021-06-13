package com.cibernet.splatcraft.text;

import com.cibernet.splatcraft.inkcolor.InkColor;
import com.cibernet.splatcraft.inkcolor.InkColors;
import com.google.common.annotations.Beta;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.*;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

/**
 * A text component that takes in an ink color id and displays its translated text, respecting color lock.
 * Not currently implemented.
 */
@SuppressWarnings("unused")
@Beta
public class InkColorText extends BaseText implements ParsableText {
    private final String input;
    @Nullable private final InkColor inkColor;

    public InkColorText(String id) {
        this.input = id;
        this.inkColor = InkColors.get(Identifier.tryParse(id));
    }

    public String getInput() {
        return this.input;
    }

    @Override
    public MutableText parse(@Nullable ServerCommandSource source, @Nullable Entity sender, int depth) {
        return source != null && this.inkColor != null ? new TranslatableText(this.inkColor.getTranslationKey()) : new LiteralText("");
    }

    @Override
    public String asString() {
        return this.input;
    }

    @Override
    public InkColorText copy() {
        return new InkColorText(this.input);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (!(object instanceof InkColorText text)) {
            return false;
        } else {
            return this.input.equals(text.input) && super.equals(object);
        }
    }

    @Override
    public String toString() {
        return "InkColorComponent{pattern='" + this.input + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
    }
}
