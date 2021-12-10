package net.splatcraft.client;

import com.google.common.reflect.Reflection;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.render.RenderLayer;
import net.splatcraft.Splatcraft;
import net.splatcraft.block.SplatcraftBlocks;
import net.splatcraft.block.entity.SplatcraftBlockEntities;
import net.splatcraft.client.config.ClientCompatConfig;
import net.splatcraft.client.config.ClientConfig;
import net.splatcraft.client.keybind.SplatcraftDevelopmentKeyBindings;
import net.splatcraft.client.keybind.SplatcraftKeyBindings;
import net.splatcraft.client.model.SplatcraftEntityModelLayers;
import net.splatcraft.client.network.NetworkingClient;
import net.splatcraft.client.render.block.inkable.InkedBlockEntityRenderer;
import net.splatcraft.client.render.entity.inkable.InkSquidEntityModelRenderer;
import net.splatcraft.entity.SplatcraftEntities;
import net.splatcraft.inkcolor.Inkable;
import net.splatcraft.util.SplatcraftUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("UnstableApiUsage")
@Environment(EnvType.CLIENT)
public class SplatcraftClient implements ClientModInitializer {
    public static final Logger LOGGER = LogManager.getLogger("%s-client".formatted(Splatcraft.MOD_ID));

    @Override
    public void onInitializeClient() {
        LOGGER.info("Initializing {}-client", Splatcraft.MOD_NAME);

        Reflection.initialize(
            ClientConfig.class,
            ClientCompatConfig.class,

            SplatcraftEntityModelLayers.class,
            SplatcraftKeyBindings.class,
            NetworkingClient.class
        );

        BlockRenderLayerMap brlm = BlockRenderLayerMap.INSTANCE;
        brlm.putBlocks(RenderLayer.getCutout(),
            SplatcraftBlocks.GRATE,
            SplatcraftBlocks.EMPTY_INKWELL,
            SplatcraftBlocks.INKWELL
        );

        EntityRendererRegistry.register(SplatcraftEntities.INK_SQUID, InkSquidEntityModelRenderer::new);

        BlockEntityRendererRegistry.register(SplatcraftBlockEntities.INKED_BLOCK, InkedBlockEntityRenderer::new);

        ColorProviderRegistry.BLOCK.register(
            (state, world, pos, tintIndex) -> {
                if (world != null && world.getBlockEntity(pos) instanceof Inkable inkable) return inkable.getInkColor().getDecimalColor();
                return 0xFFFFFF;
            }, SplatcraftBlocks.CANVAS, SplatcraftBlocks.INKWELL, SplatcraftBlocks.INKED_BLOCK
        );
        ColorProviderRegistry.ITEM.register(
            (stack, tintIndex) -> SplatcraftUtil.getInkColorFromStack(stack).getDecimalColor(),
            SplatcraftBlocks.CANVAS, SplatcraftBlocks.INKWELL
        );

        if (FabricLoader.getInstance().isDevelopmentEnvironment()) initDev();

        LOGGER.info("Initialized {}-client", Splatcraft.MOD_NAME);
    }

    private void initDev() {
        LOGGER.info("Initializing {}-client-dev", Splatcraft.MOD_NAME);
        Reflection.initialize(SplatcraftDevelopmentKeyBindings.class);
        LOGGER.info("Initialized {}-client-dev", Splatcraft.MOD_NAME);
    }
}
