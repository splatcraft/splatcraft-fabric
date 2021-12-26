package net.splatcraft.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.splatcraft.command.argument.InkColorArgumentType;
import net.splatcraft.inkcolor.InkColor;
import net.splatcraft.inkcolor.Inkable;
import net.splatcraft.registry.SplatcraftRegistries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;
import static net.splatcraft.util.SplatcraftConstants.*;

public class InkColorCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
            literal(COMMAND_INK_COLOR)
                .executes(InkColorCommand::executeGet)
                .then(
                    argument(COMMAND_ARGUMENT_INK_COLOR, InkColorArgumentType.inkColor())
                        .executes(InkColorCommand::executeSelf)
                        .then(
                            argument(COMMAND_ARGUMENT_TARGETS, EntityArgumentType.entities())
                                .executes(InkColorCommand::executeEntityTargets)
                        )

                        .then(
                            argument(COMMAND_ARGUMENT_FROM, BlockPosArgumentType.blockPos())
                                .executes(ctx -> InkColorCommand.executeBlockTargets(ctx, false))
                                .then(
                                    argument(COMMAND_ARGUMENT_TO, BlockPosArgumentType.blockPos())
                                        .executes(ctx -> InkColorCommand.executeBlockTargets(ctx, true))
                                )
                        )
                )
        );
    }

    private static int executeGet(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        Entity entity = ctx.getSource().getEntityOrThrow();
        if (entity instanceof Inkable inkable) {
            InkColor inkColor = inkable.getInkColor();
            Text text = inkColor.getDisplayText();
            ctx.getSource().sendFeedback(new TranslatableText(T_COMMAND_INK_COLOR_GET, inkable.getTextForCommand(), text), true);
            return SplatcraftRegistries.INK_COLOR.getRawId(inkColor);
        }

        throw EntityArgumentType.ENTITY_NOT_FOUND_EXCEPTION.create();
    }

    private static int executeEntityTargets(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        return execute(ctx, EntityArgumentType.getEntities(ctx, COMMAND_ARGUMENT_TARGETS)
                                              .stream()
                                              .filter(Inkable.class::isInstance)
                                              .map(Inkable.class::cast)
                                              .toList());
    }

    private static int executeSelf(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        Entity entity = ctx.getSource().getEntity();
        if (entity instanceof Inkable inkable) {
            return execute(ctx, Collections.singleton(inkable));
        }

        throw EXCEPTION_NO_ENTITIES_AFFECTED.create();
    }

    @SuppressWarnings("ConstantConditions")
    private static int executeBlockTargets(CommandContext<ServerCommandSource> ctx, boolean expectTo) throws CommandSyntaxException {
        BlockPos from = BlockPosArgumentType.getLoadedBlockPos(ctx, COMMAND_ARGUMENT_FROM);
        BlockPos to = expectTo ? BlockPosArgumentType.getLoadedBlockPos(ctx, COMMAND_ARGUMENT_TO) : from;

        ArrayList<Inkable> targets = new ArrayList<>();
        World world = ctx.getSource().getWorld();
        for (BlockPos pos : BlockPos.iterate(from, to)) {
            if (world.getBlockEntity(pos) instanceof Inkable inkable) {
                targets.add(inkable);
            }
        }

        return execute(ctx, targets);
    }

    private static int execute(CommandContext<ServerCommandSource> ctx, Collection<Inkable> targets) throws CommandSyntaxException {
        InkColor inkColor = InkColorArgumentType.getInkColor(ctx, COMMAND_ARGUMENT_INK_COLOR);

        ArrayList<Inkable> affectedInkables = new ArrayList<>();
        for (Inkable inkable : targets) {
            if (inkable.setInkColor(inkColor)) {
                affectedInkables.add(inkable);
            }
        }

        if (affectedInkables.isEmpty()) throw EXCEPTION_NO_ENTITIES_AFFECTED.create();

        int affected = affectedInkables.size();

        Text text = inkColor.getDisplayText();
        if (affected == 1) {
            Inkable inkable = affectedInkables.get(0);
            ctx.getSource().sendFeedback(new TranslatableText(T_COMMAND_INK_COLOR_SUCCESS_SINGLE, inkable.getTextForCommand(), text), true);
        } else {
            ctx.getSource().sendFeedback(new TranslatableText(T_COMMAND_INK_COLOR_SUCCESS_MULTIPLE, affected, text), true);
        }

        return affected;
    }
}
