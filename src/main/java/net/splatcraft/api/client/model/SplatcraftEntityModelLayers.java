package net.splatcraft.api.client.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;
import net.splatcraft.api.Splatcraft;
import net.splatcraft.api.client.model.entity.InkProjectileEntityModel;
import net.splatcraft.api.client.model.entity.InkSquidEntityModel;
import net.splatcraft.api.client.model.entity.InkTankModel;

@Environment(EnvType.CLIENT)
public interface SplatcraftEntityModelLayers {
    EntityModelLayer INK_SQUID = main("ink_squid", InkSquidEntityModel::getTexturedModelData);
    EntityModelLayer INK_PROJECTILE = main("ink_projectile", InkProjectileEntityModel::getTexturedModelData);
    EntityModelLayer INK_TANK = main("ink_tank", InkTankModel::getTexturedModelData);

    private static EntityModelLayer register(String id, String name, EntityModelLayerRegistry.TexturedModelDataProvider provider) {
        EntityModelLayer layer = new EntityModelLayer(new Identifier(Splatcraft.MOD_ID, id), name);
        EntityModelLayerRegistry.registerModelLayer(layer, provider);
        return layer;
    }

    private static EntityModelLayer main(String id, EntityModelLayerRegistry.TexturedModelDataProvider provider) {
        return register(id, "main", provider);
    }
}
