package com.cibernet.splatcraft.command;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.game.turf_war.ColorModificationMode;
import com.cibernet.splatcraft.game.turf_war.ColorModifications;
import com.cibernet.splatcraft.util.StringConstants;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

/**
 * Clears valid ink within two positions.
 */
public class ClearInkColorCommand {
    public static final Identifier ID = new Identifier(Splatcraft.MOD_ID, "clearink");

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal(ID.toString())
            .requires((serverCommandSource) -> serverCommandSource.hasPermissionLevel(2))
            .then(CommandManager.argument("from", BlockPosArgumentType.blockPos())
                .then(CommandManager.argument("to", BlockPosArgumentType.blockPos())
                    .executes(ClearInkColorCommand::execute)
                )
            )
        );
    }

    protected static int execute(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerCommandSource source = ctx.getSource();

        int affected = 0;
        for (BlockPos pos : BlockPos.iterate(BlockPosArgumentType.getLoadedBlockPos(ctx, "from"), BlockPosArgumentType.getLoadedBlockPos(ctx, "to"))) {
            if (ColorModifications.clearInk(source.getWorld(), null, pos, ColorModificationMode.ALL)) {
                affected++;
            }
        }

        if (affected == 0) {
            throw SplatcraftCommandExceptions.NO_INK.create();
        } else {
            source.sendFeedback(new TranslatableText(StringConstants.COMMAND_CLEARINK_SUCCESS, affected), true);
        }

        return affected;
    }
}
