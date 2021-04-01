package com.cibernet.splatcraft.client.model.entity.player;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.client.signal.Signal;
import com.cibernet.splatcraft.client.signal.SignalRendererManager;
import com.cibernet.splatcraft.entity.player.signal.AnimatablePlayerEntity;
import com.mojang.authlib.GameProfile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.model.AnimatedGeoModel;

@Environment(EnvType.CLIENT)
public class SignalPlayerModel extends AnimatedGeoModel<IAnimatable> {
    public final boolean thin;

    protected static final String PLAYER_ID = Registry.ENTITY_TYPE.getId(EntityType.PLAYER).getPath();
    protected static final Identifier CLASSIC_MODEL = new Identifier(Splatcraft.MOD_ID, "geo/" + PLAYER_ID + "_classic.geo.json");
    protected static final Identifier SLIM_MODEL = new Identifier(Splatcraft.MOD_ID, "geo/" + PLAYER_ID + "_slim.geo.json");

    public SignalPlayerModel(boolean thin) {
        this.thin = thin;
    }

    @Override
    public Identifier getModelLocation(IAnimatable object) {
        AbstractClientPlayerEntity player = (AbstractClientPlayerEntity) ((AnimatablePlayerEntity) object).player;
        if (player != null) {
            GameProfile profile = player.getGameProfile();
            if (profile != null) {
                return !this.thin ? CLASSIC_MODEL : SLIM_MODEL;
            }
        }

        return null;
    }
    @SuppressWarnings("all")
    @Override
    public Identifier getAnimationFileLocation(IAnimatable object) {
        return SignalPlayerModel.getAnimationLocation(SignalRendererManager.PLAYER_TO_SIGNAL_MAP.get((AbstractClientPlayerEntity) (((AnimatablePlayerEntity) object).player)));
    }
    protected static Identifier getAnimationLocation(Signal signal) {
        return new Identifier(signal.id.getNamespace(), "animations/" + Splatcraft.MOD_ID + "/signals/" + signal.id.getPath() + ".json");
    }

    @Override
    public Identifier getTextureLocation(IAnimatable object) {
        AbstractClientPlayerEntity player = (AbstractClientPlayerEntity) ((AnimatablePlayerEntity) object).player;
        return player == null ? new Identifier("") : player.getSkinTexture();
    }
}
