package net.splatcraft.api.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.splatcraft.api.inkcolor.InkColor;
import net.splatcraft.api.registry.SplatcraftRegistry;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class InkColorArgumentType implements ArgumentType<Identifier> {
    public static final List<String> EXAMPLES = List.of("splatcraft:cobalt", "minecraft:white", "red");

    private InkColorArgumentType() {}

    public static InkColorArgumentType inkColor() {
        return new InkColorArgumentType();
    }

    public static Identifier getIdentifier(CommandContext<ServerCommandSource> context, String name) {
        return context.getArgument(name, Identifier.class);
    }

    public static InkColor getInkColor(CommandContext<ServerCommandSource> context, String name) {
        DynamicRegistryManager registryManager = context.getSource().getRegistryManager();
        return registryManager.get(SplatcraftRegistry.INK_COLOR_KEY).get(getIdentifier(context, name));
    }

    @Override
    public Identifier parse(StringReader reader) throws CommandSyntaxException {
        return Identifier.fromCommandInput(reader);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return context.getSource() instanceof CommandSource source ? CommandSource.suggestIdentifiers(source.getRegistryManager().get(SplatcraftRegistry.INK_COLOR_KEY).getIds(), builder) : Suggestions.empty();
    }

    @Override
    public Collection<String> getExamples() {
        return InkColorArgumentType.EXAMPLES;
    }
}
