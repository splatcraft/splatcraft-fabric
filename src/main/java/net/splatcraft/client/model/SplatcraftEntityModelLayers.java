package net.splatcraft.client.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;
import net.splatcraft.Splatcraft;
import net.splatcraft.client.model.entity.InkProjectileEntityModel;
import net.splatcraft.client.model.entity.InkSquidEntityModel;
import net.splatcraft.client.model.entity.InkTankModel;

@Environment(EnvType.CLIENT)
public class SplatcraftEntityModelLayers {
    public static final EntityModelLayer INK_SQUID = main("ink_squid", InkSquidEntityModel::getTexturedModelData);
    public static final EntityModelLayer INK_PROJECTILE = main("ink_projectile", InkProjectileEntityModel::getTexturedModelData);
    public static final EntityModelLayer INK_TANK = main("ink_tank", InkTankModel::getTexturedModelData);

    private static EntityModelLayer register(String id, String layer, EntityModelLayerRegistry.TexturedModelDataProvider provider) {
        EntityModelLayer ret = new EntityModelLayer(new Identifier(Splatcraft.MOD_ID, id), layer);
        EntityModelLayerRegistry.registerModelLayer(ret, provider);
        return ret;
    }

    private static EntityModelLayer main(String id, EntityModelLayerRegistry.TexturedModelDataProvider provider) {
        return register(id, "main", provider);
    }
}
