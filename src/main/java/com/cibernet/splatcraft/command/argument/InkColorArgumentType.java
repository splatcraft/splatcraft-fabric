package com.cibernet.splatcraft.command.argument;

import com.cibernet.splatcraft.init.SplatcraftRegistries;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class InkColorArgumentType implements ArgumentType<Identifier> {
    private static final Collection<String> EXAMPLES = Arrays.asList("splatcraft:hero_yellow", "red");
    public static final DynamicCommandExceptionType NOT_FOUND_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("splatcraft.inkColor.notFound", object));

    public static InkColorArgumentType inkColor() {
        return new InkColorArgumentType();
    }

    public static Identifier getIdentifier(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        return InkColorArgumentType.validate(context.getArgument(name, Identifier.class));
    }

    private static Identifier validate(Identifier identifier) throws CommandSyntaxException {
        SplatcraftRegistries.INK_COLORS.getOrEmpty(identifier).orElseThrow(() -> NOT_FOUND_EXCEPTION.create(identifier));
        return identifier;
    }

    @Override
    public Identifier parse(StringReader stringReader) throws CommandSyntaxException {
        return InkColorArgumentType.validate(Identifier.fromCommandInput(stringReader));
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return context.getSource() instanceof CommandSource ? CommandSource.suggestIdentifiers(SplatcraftRegistries.INK_COLORS.getIds().stream(), builder) : Suggestions.empty();
    }

    @Override
    public Collection<String> getExamples() {
        return InkColorArgumentType.EXAMPLES;
    }
}
