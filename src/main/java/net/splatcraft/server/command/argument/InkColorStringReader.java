package net.splatcraft.server.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.tag.TagGroup;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.splatcraft.inkcolor.InkColor;
import net.splatcraft.registry.SplatcraftRegistries;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

import static net.splatcraft.util.SplatcraftConstants.*;

public class InkColorStringReader {
    public static final SimpleCommandExceptionType TAG_DISALLOWED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText(T_COMMAND_ERROR_INK_COLOR_TAG_DISALLOWED));
    public static final DynamicCommandExceptionType ID_INVALID_EXCEPTION = new DynamicCommandExceptionType(id -> new TranslatableText(T_COMMAND_ERROR_INK_COLOR_NOT_FOUND, id));
    public static final char HASH_SIGN = '#';

    private final StringReader reader;
    private final boolean allowTag;
    private InkColor inkColor;
    private Identifier id = new Identifier("");
    private int cursor;
    private BiFunction<SuggestionsBuilder, TagGroup<InkColor>, CompletableFuture<Suggestions>> suggestions = this::suggestAny;

    public InkColorStringReader(StringReader reader, boolean allowTag) {
        this.reader = reader;
        this.allowTag = allowTag;
    }

    public InkColor getInkColor() {
        return this.inkColor;
    }

    public Identifier getId() {
        return this.id;
    }

    public void readInkColor() throws CommandSyntaxException {
        int i = this.reader.getCursor();
        Identifier identifier = Identifier.fromCommandInput(this.reader);
        this.inkColor = SplatcraftRegistries.INK_COLOR.getOrEmpty(identifier).orElseThrow(() -> {
            this.reader.setCursor(i);
            return ID_INVALID_EXCEPTION.createWithContext(this.reader, identifier.toString());
        });
    }

    public void readTag() throws CommandSyntaxException {
        if (!this.allowTag) {
            throw TAG_DISALLOWED_EXCEPTION.create();
        }
        this.suggestions = this::suggestTag;
        this.reader.expect(HASH_SIGN);
        this.cursor = this.reader.getCursor();
        this.id = Identifier.fromCommandInput(this.reader);
    }

    public InkColorStringReader consume() throws CommandSyntaxException {
        this.suggestions = this::suggestAny;
        if (this.reader.canRead() && this.reader.peek() == HASH_SIGN) {
            this.readTag();
        } else {
            this.readInkColor();
            this.suggestions = this::suggestInkColor;
        }
        return this;
    }

    private CompletableFuture<Suggestions> suggestInkColor(SuggestionsBuilder builder, TagGroup<InkColor> group) {
        return builder.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestTag(SuggestionsBuilder builder, TagGroup<InkColor> group) {
        return CommandSource.suggestIdentifiers(group.getTagIds(), builder.createOffset(this.cursor));
    }

    private CompletableFuture<Suggestions> suggestAny(SuggestionsBuilder builder, TagGroup<InkColor> group) {
        if (this.allowTag) CommandSource.suggestIdentifiers(group.getTagIds(), builder, String.valueOf(HASH_SIGN));
        return CommandSource.suggestIdentifiers(SplatcraftRegistries.INK_COLOR.getIds(), builder);
    }

    public CompletableFuture<Suggestions> getSuggestions(SuggestionsBuilder builder, TagGroup<InkColor> group) {
        return this.suggestions.apply(builder.createOffset(this.reader.getCursor()), group);
    }
}
