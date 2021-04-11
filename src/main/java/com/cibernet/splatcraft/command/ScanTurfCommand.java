package com.cibernet.splatcraft.command;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.game.turf_war.TurfScanMode;
import com.cibernet.splatcraft.game.turf_war.TurfScanner;
import com.cibernet.splatcraft.util.StringConstants;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

/**
 * Scans for inked blocks between two positions and posts scores/winners.
 */
public class ScanTurfCommand {
    public static final Identifier ID = new Identifier(Splatcraft.MOD_ID, "scanturf");

    /*
        DEFAULTS:
            from             - required
            to               - required
            countOnlyInkable - false
            turfScanMode     - TOP_DOWN
            debug            - false
     */

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        RequiredArgumentBuilder<ServerCommandSource, Boolean> countOnlyInkable = CommandManager.argument("countOnlyInkable", BoolArgumentType.bool());
        for (TurfScanMode mode : TurfScanMode.values()) {
            countOnlyInkable.then(CommandManager.literal(mode.toSnakeCase())
                .then(CommandManager.literal("debug")
                    .executes((ctx) -> ScanTurfCommand.execute(ctx, mode, true))
                )
                .executes(ctx -> ScanTurfCommand.execute(ctx, mode, false))
            );
        }

        dispatcher.register(CommandManager.literal(ID.toString())
            .requires((serverCommandSource) -> serverCommandSource.hasPermissionLevel(2))
            .then(CommandManager.argument("from", BlockPosArgumentType.blockPos())
                .then(CommandManager.argument("to", BlockPosArgumentType.blockPos())
                    .executes(ScanTurfCommand::execute)
                    .then(countOnlyInkable.executes(ScanTurfCommand::execute))
                )
            )
        );
    }

    protected static int execute(CommandContext<ServerCommandSource> ctx, TurfScanMode mode, boolean debug) throws CommandSyntaxException {
        ServerCommandSource source = ctx.getSource();

        ServerPlayerEntity player = null;
        try {
            player = source.getPlayer();
        } catch (CommandSyntaxException ignored) {}

        boolean countOnlyInkable = false;
        try {
            countOnlyInkable = BoolArgumentType.getBool(ctx, "countOnlyInkable");
        } catch (IllegalArgumentException ignored) {}

        Pair<Integer, Integer> result = TurfScanner.scanArea(
            source.getWorld(),
            BlockPosArgumentType.getLoadedBlockPos(ctx, "from"), BlockPosArgumentType.getLoadedBlockPos(ctx, "to"),
           mode, player, countOnlyInkable, debug
        );

        int affected = result.getLeft();
        if (affected == 0) {
            throw SplatcraftCommandExceptions.NO_INK.create();
        } else {
            source.sendFeedback(new TranslatableText(StringConstants.COMMAND_SCANTURF_SUCCESS, affected), true);
        }

        return affected;
    }
    public static int execute(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        return ScanTurfCommand.execute(ctx, TurfScanMode.getDefault(), false);
    }
}
