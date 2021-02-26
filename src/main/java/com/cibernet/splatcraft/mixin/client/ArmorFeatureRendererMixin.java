package com.cibernet.splatcraft.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.item.ArmorItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Environment(EnvType.CLIENT)
@Mixin(ArmorFeatureRenderer.class)
public class ArmorFeatureRendererMixin {
    @Shadow @Final private static Map<String, Identifier> ARMOR_TEXTURE_CACHE;

    @Inject(method = "getArmorTexture", at = @At("HEAD"), cancellable = true)
    private void getArmorTexture(ArmorItem armorItem, boolean bl, String string, CallbackInfoReturnable<Identifier> cir) {
        Identifier identifier = Registry.ITEM.getId(armorItem);
        String namespace = identifier.getNamespace();
        if (!namespace.equals("minecraft")) {
            String string2 = "textures/models/armor/" + armorItem.getMaterial().getName() + "_layer_" + (bl ? 2 : 1) + (string == null ? "" : "_" + string) + ".png";
            cir.setReturnValue(ArmorFeatureRendererMixin.ARMOR_TEXTURE_CACHE.computeIfAbsent(string2, s -> new Identifier(namespace, s)));
        }
    }
}
