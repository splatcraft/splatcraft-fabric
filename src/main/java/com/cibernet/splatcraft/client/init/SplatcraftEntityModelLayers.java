package com.cibernet.splatcraft.client.init;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.entity.InkProjectileEntity;
import com.cibernet.splatcraft.entity.InkSquidEntity;
import com.cibernet.splatcraft.entity.SquidBumperEntity;
import com.cibernet.splatcraft.mixin.client.EntityModelLayersInvoker;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class SplatcraftEntityModelLayers {
    public static final EntityModelLayer INK_PROJECTILE = registerMain(InkProjectileEntity.id);
    public static final EntityModelLayer INK_SQUID = registerMain(InkSquidEntity.id);
    public static final EntityModelLayer SQUID_BUMPER = registerMain(SquidBumperEntity.id);

    private static EntityModelLayer registerMain(String id) {
        return EntityModelLayersInvoker.register(new Identifier(Splatcraft.MOD_ID, id).toString(), "main");
    }
}
