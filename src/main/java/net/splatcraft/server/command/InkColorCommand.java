package net.splatcraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.*;
import net.splatcraft.entity.Inkable;
import net.splatcraft.inkcolor.InkColor;
import net.splatcraft.server.argument.InkColorArgumentType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static net.minecraft.server.command.CommandManager.*;
import static net.splatcraft.util.SplatcraftConstants.*;

public class InkColorCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
            literal(COMMAND_INK_COLOR.toString()).then(
                argument(COMMAND_ARGUMENT_INK_COLOR, InkColorArgumentType.inkColor())
                    .executes(InkColorCommand::executeSelf)
                    .then(
                        argument(COMMAND_ARGUMENT_TARGETS, EntityArgumentType.entities())
                            .executes(InkColorCommand::executeTargets)
                    )
            )
        );
    }

    private static int executeTargets(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        List<? extends Entity> targets = EntityArgumentType.getEntities(ctx, COMMAND_ARGUMENT_TARGETS).stream().filter(entity -> entity instanceof Inkable).toList();
        return execute(ctx, targets);
    }

    private static int executeSelf(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        Entity entity = ctx.getSource().getEntity();
        if (entity instanceof Inkable) {
            return execute(ctx, Collections.singleton(entity));
        }

        throw EXCEPTION_NO_ENTITIES_AFFECTED.create();
    }

    private static int execute(CommandContext<ServerCommandSource> ctx, Collection<? extends Entity> targets) throws CommandSyntaxException {
        InkColor inkColor = InkColorArgumentType.getInkColor(ctx, COMMAND_ARGUMENT_INK_COLOR);
        List<? extends Entity> entities = targets.stream().filter(entity -> entity instanceof Inkable).toList();

        if (entities.isEmpty()) throw EntityArgumentType.ENTITY_NOT_FOUND_EXCEPTION.create();

        ArrayList<Entity> affectedEntities = new ArrayList<>();
        for (Entity entity : entities) {
            if (entity instanceof Inkable inkable) {
                if (inkable.setInkColor(inkColor)) {
                    affectedEntities.add(entity);
                }
            }
        }

        if (affectedEntities.isEmpty()) throw EXCEPTION_NO_ENTITIES_AFFECTED.create();

        int affected = affectedEntities.size();

        
        MutableText iconText = new TranslatableText(T_INK_COLOR_TEXT_DISPLAY_ICON).setStyle(Style.EMPTY.withColor(inkColor.getDecimalColor()));
        MutableText text = new TranslatableText(T_INK_COLOR_TEXT_DISPLAY, iconText, inkColor);
        if (affected == 1) {
            Entity entity = affectedEntities.get(0);
            ctx.getSource().sendFeedback(new TranslatableText(T_COMMAND_INK_COLOR_SUCCESS_SINGLE, entity.getDisplayName(), text), true);
        } else {
            ctx.getSource().sendFeedback(new TranslatableText(T_COMMAND_INK_COLOR_SUCCESS_MULTIPLE, affected, text), true);
        }

        return affected;
    }
}
