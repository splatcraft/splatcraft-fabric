package com.cibernet.splatcraft.command;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.command.argument.InkColorArgumentType;
import com.cibernet.splatcraft.game.turf_war.ColorModificationMode;
import com.cibernet.splatcraft.game.turf_war.ColorModifications;
import com.cibernet.splatcraft.inkcolor.ColorUtil;
import com.cibernet.splatcraft.inkcolor.InkColor;
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
 * Replaces blocks' ink colors and base ink colors within two positions.
 */
public class FillInkColorCommand {
    public static final Identifier ID = new Identifier(Splatcraft.MOD_ID, "fillinkcolor");

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal(ID.toString())
            .requires((serverCommandSource) -> serverCommandSource.hasPermissionLevel(2))
            .then(CommandManager.argument("from", BlockPosArgumentType.blockPos())
                .then(CommandManager.argument("to", BlockPosArgumentType.blockPos())
                    .then(CommandManager.argument("inkColor", InkColorArgumentType.inkColor())
                        .executes(FillInkColorCommand::execute)
                    )
                )
            )
        );
    }

    protected static int execute(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerCommandSource source = ctx.getSource();

        InkColor color = InkColor.fromNonNull(InkColorArgumentType.getIdentifier(ctx, "inkColor"));

        int affected = 0;
        for (BlockPos pos : BlockPos.iterate(BlockPosArgumentType.getLoadedBlockPos(ctx, "from"), BlockPosArgumentType.getLoadedBlockPos(ctx, "to"))) {
            if (ColorModifications.changeInkColor(source.getWorld(), null, color, pos, ColorModificationMode.ALL)) {
                affected++;
            }
        }

        if (affected == 0) {
            throw SplatcraftCommandExceptions.NO_INK.create();
        } else {
            source.sendFeedback(new TranslatableText(StringConstants.COMMAND_FILLINKCOLOR_SUCCESS, affected, ColorUtil.getFormattedColorName(color)), true);
        }

        return affected;
    }
}
