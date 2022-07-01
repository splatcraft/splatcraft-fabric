package net.splatcraft.api.inkcolor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.splatcraft.api.tag.SplatcraftBlockTags;
import net.splatcraft.impl.client.config.ClientConfig;
import net.splatcraft.impl.entity.access.InkEntityAccess;

import static net.splatcraft.api.particle.SplatcraftParticleType.*;
import static net.splatcraft.api.world.SplatcraftGameRules.*;

public interface Inkable {
    InkColor getInkColor();
    boolean setInkColor(InkColor inkColor);

    InkType getInkType();
    boolean setInkType(InkType inkType);

    Text getTextForCommand();

    default boolean hasInkColor() {
        return true;
    }

    default boolean hasInkType() {
        return true;
    }

    default void copyInkableTo(Inkable inkable) {
        inkable.setInkColor(this.getInkColor());
        inkable.setInkType(this.getInkType());
    }

    default <T extends Entity & Inkable> void tickInkable(T entity, Vec3d movementInput) {
        if (entity.world.isClient) {
            clientTickInkable(entity, movementInput);
        } else {
            if (entity.isOnGround() && gameRule(entity.world, INKWELL_CHANGES_INK_COLOR)) {
                BlockEntity blockEntity = entity.world.getBlockEntity(entity.getSteppingPos());
                if (blockEntity != null && blockEntity.getCachedState().isIn(SplatcraftBlockTags.INK_COLOR_CHANGERS)) {
                    if (blockEntity instanceof Inkable inkable && ((InkEntityAccess) entity).isInSquidForm()) entity.setInkColor(inkable.getInkColor());
                }
            }
        }
    }

    @Environment(EnvType.CLIENT)
    default <T extends Entity & Inkable> void clientTickInkable(T entity, Vec3d movementInput) {
        if (ClientConfig.INSTANCE.inkSplashParticleOnTravel.getValue()) {
            InkEntityAccess access = (InkEntityAccess) entity;
            if (movementInput.length() > 0.2d) {
                if (access.isSubmergedInInk() || (!(entity instanceof PlayerEntity) && access.isOnInk())) {
                    inkSplash(entity.world, entity, access.getInkSplashParticlePos(), 0.75f);
                }
            }
        }
    }
}
