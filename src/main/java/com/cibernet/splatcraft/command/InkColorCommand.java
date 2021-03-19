package com.cibernet.splatcraft.command;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.command.argument.InkColorArgumentType;
import com.cibernet.splatcraft.inkcolor.ColorUtils;
import com.cibernet.splatcraft.inkcolor.InkColor;
import com.cibernet.splatcraft.inkcolor.InkColors;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.Collection;

public class InkColorCommand {
    public static final String id = "setinkcolor";

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal(new Identifier(Splatcraft.MOD_ID, id).toString()).requires(commandSource -> commandSource.hasPermissionLevel(2))
            .then(CommandManager.argument("color", InkColorArgumentType.inkColor()).executes(
                context -> InkColorCommand.executeSelf(context.getSource(), InkColorArgumentType.getIdentifier(context, "color"))
            ).then(CommandManager.argument("targets", EntityArgumentType.entities()).executes(
                context -> InkColorCommand.executeOthers(context.getSource(), InkColorArgumentType.getIdentifier(context, "color"), EntityArgumentType.getEntities(context, "targets"))
            ))));
    }

    private static int executeSelf(ServerCommandSource source, Identifier identifier) {
        InkColor inkColor = InkColors.get(identifier);
        Entity self = source.getEntity();

        if (self != null) {
            if (ColorUtils.setInkColor(self, inkColor)) {
                source.sendFeedback(new TranslatableText("commands.splatcraft.setinkcolor.self.success", self.getDisplayName(), ColorUtils.getFormattedColorName(inkColor, false)), true);
            } else {
                source.sendFeedback(new TranslatableText("commands.splatcraft.setinkcolor.self.failure", self.getDisplayName(), ColorUtils.getFormattedColorName(inkColor, false)).formatted(Formatting.RED), false);
            }
        }

        return 1;
    }

    private static int executeOthers(ServerCommandSource source, Identifier identifier, Collection<? extends Entity> entities) {
        InkColor inkColor = InkColors.get(identifier);

        int successes = 0;
        for (Entity entity : entities) {
            if (ColorUtils.setInkColor(entity, inkColor)) {
                successes++;
            }
        }

        if (successes == 1) {
            source.sendFeedback(new TranslatableText("commands.splatcraft.setinkcolor.self.success", entities.iterator().next().getDisplayName(), ColorUtils.getFormattedColorName(inkColor, false)), true);
        } else if (successes > 0) {
            source.sendFeedback(new TranslatableText("commands.splatcraft.setinkcolor.other.success", successes, ColorUtils.getFormattedColorName(inkColor, false)), true);
        } else {
            source.sendFeedback(new TranslatableText("commands.splatcraft.setinkcolor.other.failure").formatted(Formatting.RED), false);
        }

        return successes;
    }
}
