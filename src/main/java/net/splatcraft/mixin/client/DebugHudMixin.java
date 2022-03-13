package net.splatcraft.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.splatcraft.api.Splatcraft;
import net.splatcraft.api.inkcolor.InkColor;
import net.splatcraft.impl.client.config.ClientConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

import static net.splatcraft.api.client.util.ClientUtil.*;
import static org.apache.commons.lang3.StringUtils.*;

@Environment(EnvType.CLIENT)
@Mixin(DebugHud.class)
public class DebugHudMixin {
    /**
     * Adds the targeted ink color to the end of the right debug hud, below the targeted entity usually.
     */
    @Inject(method = "getRightText", at = @At("TAIL"))
    private void onGetRightText(CallbackInfoReturnable<List<String>> cir) {
        InkColor inkColor = getTargetedInkColor();
        if (inkColor != null) {
            List<String> list = cir.getReturnValue();
            list.add("");
            list.add(Formatting.UNDERLINE + "[%s] Targeted Ink Color".formatted(Splatcraft.MOD_NAME));
            list.add(inkColor.toString());

            getColorOption(inkColor).ifPresent(option -> {
                Identifier id = ClientConfig.INSTANCE.getDisplayedOptions().inverse().get(option);
                String path = id.getPath();
                list.add("Color Lock: " + capitalize(path.substring(path.lastIndexOf("_") + 1)));
            });

            inkColor.streamTags().map(key -> "#" + key.id()).forEach(list::add);
        }
    }
}
