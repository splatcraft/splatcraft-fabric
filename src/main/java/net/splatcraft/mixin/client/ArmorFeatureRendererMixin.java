package net.splatcraft.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.item.ArmorItem;
import net.minecraft.util.Identifier;
import net.splatcraft.api.Splatcraft;
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

    /**
     * Fixes namespacing on armor textures.
     */
    @Inject(method = "getArmorTexture", at = @At("HEAD"), cancellable = true)
    private void onGetArmorTexture(ArmorItem item, boolean legs, String overlay, CallbackInfoReturnable<Identifier> cir) {
        String materialName = item.getMaterial().getName();
        if (materialName.startsWith(Splatcraft.MOD_ID)) {
            String path = "textures/models/armor/%s_layer_%s%s.png".formatted(
                materialName.substring(materialName.indexOf(Identifier.NAMESPACE_SEPARATOR) + 1),
                (legs ? 2 : 1), (overlay == null ? "" : "_" + overlay)
            );
            cir.setReturnValue(ARMOR_TEXTURE_CACHE.computeIfAbsent(path, s -> new Identifier(Splatcraft.MOD_ID, s)));
        }
    }
}
