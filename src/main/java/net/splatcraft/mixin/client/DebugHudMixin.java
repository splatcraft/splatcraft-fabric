package net.splatcraft.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.splatcraft.client.config.ClientConfig;
import net.splatcraft.inkcolor.InkColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

import static net.splatcraft.client.util.ClientUtil.getColorOption;
import static net.splatcraft.client.util.ClientUtil.getTargetedInkColor;
import static org.apache.commons.lang3.StringUtils.capitalize;

@Environment(EnvType.CLIENT)
@Mixin(DebugHud.class)
public class DebugHudMixin {
    // add targeted ink color to end of right debug hud (below targeted entity, hopefully)
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
        }
    }
}
