package com.cibernet.splatcraft.command;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.command.argument.InkColorArgumentType;
import com.cibernet.splatcraft.init.SplatcraftRegistries;
import com.cibernet.splatcraft.inkcolor.ColorUtils;
import com.cibernet.splatcraft.inkcolor.InkColor;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.Collection;

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

        if (ColorUtils.setInkColor(source.getPlayer(), inkColor)) {
            source.sendFeedback(new TranslatableText("commands.splatcraft.setinkcolor.success.single", source.getPlayer().getDisplayName(), ColorUtils.getFormattedColorName(inkColor, false)), true);
        } else {
            source.sendFeedback(new TranslatableText("commands.splatcraft.setinkcolor.error.single", source.getPlayer().getDisplayName(), ColorUtils.getFormattedColorName(inkColor, false)).formatted(Formatting.RED), false);
        }

        return 1;
    }

    private static int execute(ServerCommandSource source, Identifier identifier, Collection<ServerPlayerEntity> targets) {
        InkColor inkColor = SplatcraftRegistries.INK_COLORS.get(identifier);
        targets.forEach(player -> ColorUtils.setInkColor(player, inkColor));

        if (targets.size() == 1) {
            source.sendFeedback(new TranslatableText("commands.splatcraft.setinkcolor.success.single", targets.iterator().next().getDisplayName(), ColorUtils.getFormattedColorName(inkColor, false)), true);
        } else {
            source.sendFeedback(new TranslatableText("commands.splatcraft.setinkcolor.success.multiple", targets.size(), ColorUtils.getFormattedColorName(inkColor, false)), true);
        }

        return targets.size();
    }
}
