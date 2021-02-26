package com.cibernet.splatcraft.server.command;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.init.SplatcraftRegistries;
import com.cibernet.splatcraft.inkcolor.ColorUtils;
import com.cibernet.splatcraft.inkcolor.InkColor;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class InkColorCommand {
    public static final String id = "setinkcolor";

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal(new Identifier(Splatcraft.MOD_ID, id).toString()).requires(commandSource -> commandSource.hasPermissionLevel(2))
            .then(CommandManager.argument("color", InkColorArgumentType.inkColor()).executes(
                context -> InkColorCommand.execute(context.getSource(), InkColorArgumentType.getIdentifier(context, "color"))
            ).then(CommandManager.argument("targets", EntityArgumentType.players()).executes(
                context -> InkColorCommand.execute(context.getSource(), InkColorArgumentType.getIdentifier(context, "color"), EntityArgumentType.getPlayers(context, "targets"))
            ))));
    }

    private static int execute(ServerCommandSource source, Identifier identifier) throws CommandSyntaxException {
        InkColor inkColor = SplatcraftRegistries.INK_COLORS.get(identifier);

        if (ColorUtils.setPlayerColor(source.getPlayer(), inkColor)) {
            source.sendFeedback(new TranslatableText("commands.splatcraft.setinkcolor.success.single", source.getPlayer().getDisplayName(), ColorUtils.getFormattedColorName(inkColor, false)), true);
        } else {
            source.sendFeedback(new TranslatableText("commands.splatcraft.setinkcolor.error.single", source.getPlayer().getDisplayName(), ColorUtils.getFormattedColorName(inkColor, false)).formatted(Formatting.RED), false);
        }

        return 1;
    }

    private static int execute(ServerCommandSource source, Identifier identifier, Collection<ServerPlayerEntity> targets) {
        InkColor inkColor = SplatcraftRegistries.INK_COLORS.get(identifier);
        targets.forEach(player -> ColorUtils.setPlayerColor(player, inkColor));

        if (targets.size() == 1) {
            source.sendFeedback(new TranslatableText("commands.splatcraft.setinkcolor.success.single", targets.iterator().next().getDisplayName(), ColorUtils.getFormattedColorName(inkColor, false)), true);
        } else {
            source.sendFeedback(new TranslatableText("commands.splatcraft.setinkcolor.success.multiple", targets.size(), ColorUtils.getFormattedColorName(inkColor, false)), true);
        }

        return targets.size();
    }

    static class InkColorArgumentType implements ArgumentType<Identifier> {
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
}
