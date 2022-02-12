package net.splatcraft.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.tag.TagGroup;
import net.minecraft.tag.TagManager;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.splatcraft.client.config.ClientConfig;
import net.splatcraft.inkcolor.InkColor;
import net.splatcraft.registry.SplatcraftRegistries;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

import static net.splatcraft.client.util.ClientUtil.*;
import static org.apache.commons.lang3.StringUtils.*;

@Environment(EnvType.CLIENT)
@Mixin(DebugHud.class)
public class DebugHudMixin {
    @Shadow @Final private MinecraftClient client;

    /**
     * Adds the targeted ink color to the end of the right debug hud, below the targeted entity usually.
     */
    @Inject(method = "getRightText", at = @At("TAIL"))
    private void onGetRightText(CallbackInfoReturnable<List<String>> cir) {
        InkColor inkColor = getTargetedInkColor();
        if (inkColor != null) {
            List<String> list = cir.getReturnValue();
            list.add("");
            list.add(Formatting.UNDERLINE + "[Splatcraft] Targeted Ink Color");
            list.add(inkColor.toString());

            getColorOption(inkColor).ifPresent(option -> {
                Identifier id = ClientConfig.INSTANCE.getDisplayedOptions().inverse().get(option);
                String path = id.getPath();
                list.add("Color Lock: " + capitalize(path.substring(path.lastIndexOf("_") + 1)));
            });

            ClientPlayNetworkHandler networkHandler = this.client.getNetworkHandler();
            if (networkHandler != null) {
                TagManager tagManager = networkHandler.getTagManager();
                RegistryKey<? extends Registry<InkColor>> key = SplatcraftRegistries.INK_COLOR.getKey();
                TagGroup<InkColor> tagGroup = tagManager.getOrCreateTagGroup(key);
                for (Identifier id : tagGroup.getTagsFor(inkColor)) list.add("#" + id);
            }
        }
    }
}
