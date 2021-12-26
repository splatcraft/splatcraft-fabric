package net.splatcraft.client.model;

import com.google.common.collect.ImmutableMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;
import net.splatcraft.Splatcraft;
import net.splatcraft.client.model.entity.InkSquidEntityModel;
import net.splatcraft.client.model.entity.InkTankModel;
import net.splatcraft.mixin.client.EntityModelLayersInvoker;

@Environment(EnvType.CLIENT)
public class SplatcraftEntityModelLayers {
    public static final EntityModelLayer INK_SQUID = registerMain("ink_squid");
    public static final EntityModelLayer INK_TANK = registerMain("ink_tank");

    static {
        new ImmutableMap.Builder<EntityModelLayer, EntityModelLayerRegistry.TexturedModelDataProvider>()
            .put(SplatcraftEntityModelLayers.INK_SQUID, InkSquidEntityModel::getTexturedModelData)
            .put(SplatcraftEntityModelLayers.INK_TANK, InkTankModel::getTexturedModelData)
        .build().forEach(EntityModelLayerRegistry::registerModelLayer);
    }

    private static EntityModelLayer registerMain(String id) {
        return EntityModelLayersInvoker.invoke_register(new Identifier(Splatcraft.MOD_ID, id).toString(), "main");
    }
}
