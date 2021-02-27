package com.cibernet.splatcraft.mixin.client;

import com.cibernet.splatcraft.Splatcraft;
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
    @Shadow @Final
    private static Map<String, Identifier> ARMOR_TEXTURE_CACHE;

    /**
     * Vanilla Minecraft defaults all registered armor textures to the <code>minecraft</code> assets namespace. This fixes them to use the namespace of the mod.
     */
    @Inject(method = "getArmorTexture", at = @At("HEAD"), cancellable = true)
    private void getArmorTexture(ArmorItem item, boolean layer, String id, CallbackInfoReturnable<Identifier> cir) {
        Identifier identifier = Registry.ITEM.getId(item);
        String namespace = identifier.getNamespace();
        if (/*!namespace.equals("minecraft")*/ namespace.equals(Splatcraft.MOD_ID)) { // could cause mod incompatibilities if done the commented-out way
            String string2 = "textures/models/armor/" + item.getMaterial().getName() + "_layer_" + (layer ? 2 : 1) + (id == null ? "" : "_" + id) + ".png";
            cir.setReturnValue(ArmorFeatureRendererMixin.ARMOR_TEXTURE_CACHE.computeIfAbsent(string2, s -> new Identifier(namespace, s)));
        }
    }
}
