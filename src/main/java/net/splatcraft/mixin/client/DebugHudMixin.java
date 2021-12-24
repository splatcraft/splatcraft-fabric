package net.splatcraft.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.entity.Entity;
import net.splatcraft.inkcolor.Inkable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(DebugHud.class)
public class DebugHudMixin {
    @Shadow @Final private MinecraftClient client;

    // add ink color to end of right debug hud (below targeted entity, hopefully)
    @Inject(method = "getRightText", at = @At("TAIL"))
    private void onGetRightText(CallbackInfoReturnable<List<String>> cir) {
        Entity entity = this.client.targetedEntity;
        if (entity instanceof Inkable inkable) {
            List<String> list = cir.getReturnValue();
            list.add(inkable.getInkColor().toString());
        }
    }
}
