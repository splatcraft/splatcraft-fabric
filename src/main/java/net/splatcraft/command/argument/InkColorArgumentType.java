package net.splatcraft.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
import net.splatcraft.inkcolor.InkColor;
import net.splatcraft.inkcolor.InkColors;
import net.splatcraft.registry.SplatcraftRegistries;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import static net.splatcraft.util.SplatcraftConstants.*;

public final class InkColorArgumentType implements ArgumentType<Identifier> {
    private static final Collection<String> EXAMPLES = Arrays.asList(InkColors.COBALT.toString(), InkColors.DYE_WHITE.toString(), InkColors.DYE_RED.getId().getPath());

    private InkColorArgumentType() {}

    public static InkColorArgumentType inkColor() {
        return new InkColorArgumentType();
    }

    public static Identifier getIdentifier(CommandContext<ServerCommandSource> ctx, String name) {
        return ctx.getArgument(name, Identifier.class);
    }

    public static InkColor getInkColor(CommandContext<ServerCommandSource> ctx, String name) {
        return SplatcraftRegistries.INK_COLOR.get(getIdentifier(ctx, name));
    }

    @Override
    public Identifier parse(StringReader reader) throws CommandSyntaxException {
        Identifier id = Identifier.fromCommandInput(reader);
        if (!SplatcraftRegistries.INK_COLOR.containsId(id)) throw EXCEPTION_INK_COLOR_NOT_FOUND.createWithContext(reader, id);
        return id;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> ctx, SuggestionsBuilder builder) {
        return ctx.getSource() instanceof CommandSource ? CommandSource.suggestIdentifiers(SplatcraftRegistries.INK_COLOR.getIds(), builder) : Suggestions.empty();
    }

    @Override
    public Collection<String> getExamples() {
        return InkColorArgumentType.EXAMPLES;
    }
}
