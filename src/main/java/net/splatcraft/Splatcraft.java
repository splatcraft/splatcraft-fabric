package net.splatcraft;

import com.google.common.reflect.Reflection;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.splatcraft.block.SplatcraftBlocks;
import net.splatcraft.config.CommonConfig;
import net.splatcraft.component.SplatcraftComponents;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Splatcraft implements ModInitializer {
    public static final String    MOD_ID     = "splatcraft";
    public static final String    MOD_NAME   = "Splatcraft";

    public static final Logger    LOGGER     = LogManager.getLogger(MOD_ID);
    public static final ItemGroup ITEM_GROUP = FabricItemGroupBuilder.build(new Identifier(MOD_ID, "item_group"), () -> new ItemStack(SplatcraftBlocks.GRATE));

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public void onInitialize() {
        LOGGER.info("Initializing {}", MOD_NAME);

        Reflection.initialize(
            CommonConfig.class,
            SplatcraftBlocks.class,
            SplatcraftComponents.class
        );

        LOGGER.info("Initialized {}", MOD_NAME);
    }
}
