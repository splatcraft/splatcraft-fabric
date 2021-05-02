package com.cibernet.splatcraft.command;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.command.argument.InkColorArgumentType;
import com.cibernet.splatcraft.inkcolor.ColorUtil;
import com.cibernet.splatcraft.inkcolor.InkColor;
import com.cibernet.splatcraft.inkcolor.InkColors;
import com.cibernet.splatcraft.util.StringConstants;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import java.util.Collection;

/**
 * Sets one or more entities' ink colors.
 */
public class SetInkColorCommand {
    public static final Identifier ID = new Identifier(Splatcraft.MOD_ID, "setinkcolor");

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal(ID.toString()).requires(commandSource -> commandSource.hasPermissionLevel(2))
            .then(CommandManager.argument("color", InkColorArgumentType.inkColor()).executes(
                context -> SetInkColorCommand.executeSelf(context.getSource(), InkColorArgumentType.getIdentifier(context, "color"))
            ).then(CommandManager.argument("targets", EntityArgumentType.entities()).executes(
                context -> SetInkColorCommand.executeOthers(context.getSource(), InkColorArgumentType.getIdentifier(context, "color"), EntityArgumentType.getEntities(context, "targets"))
            ))));
    }

    private static int executeSelf(ServerCommandSource source, Identifier identifier) throws CommandSyntaxException {
        InkColor inkColor = InkColors.getNonNull(identifier);
        Entity self = source.getEntity();

        if (self != null) {
            if (ColorUtil.setInkColor(self, inkColor)) {
                source.sendFeedback(new TranslatableText(StringConstants.COMMAND_SETINKCOLOR_SUCCESS_SELF, self.getDisplayName(), ColorUtil.getFormattedColorName(inkColor, false)), true);
            } else {
                throw SplatcraftCommandExceptions.NO_INK_SET.create();
            }
        }

        return 1;
    }

    private static int executeOthers(ServerCommandSource source, Identifier identifier, Collection<? extends Entity> entities) throws CommandSyntaxException {
        InkColor inkColor = InkColors.getNonNull(identifier);

        int affected = 0;
        Entity lastAffected = null;
        for (Entity entity : entities) {
            if (ColorUtil.setInkColor(entity, inkColor)) {
                affected++;
                lastAffected = entity;
            }
        }

        if (affected == 1) {
            source.sendFeedback(new TranslatableText(StringConstants.COMMAND_SETINKCOLOR_SUCCESS_SELF, lastAffected.getDisplayName(), ColorUtil.getFormattedColorName(inkColor, false)), true);
        } else if (affected > 0) {
            source.sendFeedback(new TranslatableText(StringConstants.COMMAND_SETINKCOLOR_SUCCESS_OTHER, affected, ColorUtil.getFormattedColorName(inkColor, false)), true);
        } else {
            throw SplatcraftCommandExceptions.NO_INK_SET.create();
        }

        return affected;
    }
}
