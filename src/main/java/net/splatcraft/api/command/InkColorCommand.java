package net.splatcraft.api.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.splatcraft.api.Splatcraft;
import net.splatcraft.api.command.argument.InkColorArgumentType;
import net.splatcraft.api.inkcolor.Inkable;

import java.util.Collection;

import static net.minecraft.server.command.CommandManager.*;

public interface InkColorCommand {
    static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(create("inkcolor"));
        dispatcher.register(create(Splatcraft.MOD_ID + ":inkcolor"));
    }

    static LiteralArgumentBuilder<ServerCommandSource> create(String id) {
        return literal(id).executes(InkColorCommand::executeGet)
                          .then(argument("inkcolor", InkColorArgumentType.inkColor())
                              .executes(InkColorCommand::executeSelf)

                              .then(argument("targets", EntityArgumentType.entities()).executes(InkColorCommand::executeEntityTargets))

                              .then(argument("from", BlockPosArgumentType.blockPos()).executes(ctx -> InkColorCommand.executeBlockTargets(ctx, false))
                                  .then(argument("to", BlockPosArgumentType.blockPos()).executes(ctx -> InkColorCommand.executeBlockTargets(ctx, true)))
                              )
                          );
    }

    private static int executeGet(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        throw EntityArgumentType.ENTITY_NOT_FOUND_EXCEPTION.create();
    }

    private static int executeEntityTargets(CommandContext<ServerCommandSource> ctx) {
        return 0;
    }

    private static int executeSelf(CommandContext<ServerCommandSource> ctx) {
        return 0;
    }

    private static int executeBlockTargets(CommandContext<ServerCommandSource> ctx, boolean expectTo) {
        return 0;
    }

    private static int execute(CommandContext<ServerCommandSource> ctx, Collection<Inkable> targets) {
        return 0;
    }
}
