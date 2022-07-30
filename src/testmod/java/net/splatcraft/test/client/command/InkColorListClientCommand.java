package net.splatcraft.test.client.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.splatcraft.api.inkcolor.InkColor;
import net.splatcraft.api.registry.SplatcraftRegistry;

import java.util.Map;
import java.util.Set;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;

public interface InkColorListClientCommand {
    static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal("inkcolortestclient").executes(InkColorListClientCommand::execute));
    }

    static int execute(CommandContext<FabricClientCommandSource> context) {
        FabricClientCommandSource source = context.getSource();
        Registry<InkColor> registry = source.getRegistryManager().get(SplatcraftRegistry.INK_COLOR_KEY);

        Set<Map.Entry<RegistryKey<InkColor>, InkColor>> entries = registry.getEntrySet();
        source.sendFeedback(Text.literal("Found " + entries.size() + " ink colors:").formatted(Formatting.GRAY));

        for (Map.Entry<RegistryKey<InkColor>, InkColor> entry : entries) {
            Identifier id = entry.getKey().getValue();
            InkColor inkColor = entry.getValue();
            int color = inkColor.getColor().getColor();
            source.sendFeedback(Text.literal("- ID:     " + id));
            source.sendFeedback(Text.literal("  Color: " + String.format("0x%06X", color) + " ").append(Text.literal("⬛").setStyle(Style.EMPTY.withColor(color))));
        }

        return entries.size();
    }
}
