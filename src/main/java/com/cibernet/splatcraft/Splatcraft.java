package com.cibernet.splatcraft;

import com.cibernet.splatcraft.command.InkColorCommand;
import com.cibernet.splatcraft.command.argument.InkColorArgumentType;
import com.cibernet.splatcraft.component.PlayerDataComponent;
import com.cibernet.splatcraft.handler.PlayerHandler;
import com.cibernet.splatcraft.init.*;
import com.cibernet.splatcraft.network.SplatcraftNetworkingConstants;
import com.cibernet.splatcraft.tag.SplatcraftBlockTags;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Splatcraft implements ModInitializer {
    public static final String MOD_ID = "splatcraft";
    public static final String MOD_NAME = "Splatcraft";

    public static Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static class ItemGroups {
        public static final ItemGroup ITEM_GROUP = FabricItemGroupBuilder.build(
            new Identifier(MOD_ID, "item_group"),
            () -> new ItemStack(SplatcraftBlocks.CANVAS)
        );
        public static final ItemGroup COLORED_BLOCKS = FabricItemGroupBuilder.build(
            new Identifier(MOD_ID, "colored_blocks"),
            () -> new ItemStack(SplatcraftBlocks.INKWELL)
        );
    }

    @Override
    public void onInitialize() {
        log("Initializing");

        new SplatcraftRegistries();

        new SplatcraftBlockTags();
        // new SplatcraftInkColorTags();

        new SplatcraftAttributes();
        new SplatcraftGameRules();
        new SplatcraftSoundEvents();

        new SplatcraftBlockEntities();
        new SplatcraftBlocks();
        new SplatcraftItems();
        new SplatcraftEntities();

        ServerPlayNetworking.registerGlobalReceiver(SplatcraftNetworkingConstants.TOGGLE_SQUID_PACKET_ID, (server, player, handler, buf, responseSender) -> server.execute(() -> PlayerDataComponent.toggleSquidForm(player)));
        PlayerHandler.registerEvents();

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> InkColorCommand.register(dispatcher));
        ArgumentTypes.register(new Identifier(Splatcraft.MOD_ID, "ink_color").toString(), InkColorArgumentType.class, new ConstantArgumentSerializer<>(InkColorArgumentType::inkColor));

        log("Initialized");
    }

    public static void log(Level level, String message) {
        LOGGER.log(level, "[" + MOD_NAME + "] " + message);
    }
    public static void log(String message) {
        log(Level.INFO, message);
    }

    public static Identifier texture(String path) {
        return new Identifier(MOD_ID, "textures/" + path + ".png");
    }
}
