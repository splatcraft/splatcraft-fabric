package com.cibernet.splatcraft.mixin;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.inkcolor.InkColors;
import com.cibernet.splatcraft.text.InkColorText;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.lang.reflect.Type;

@Mixin(Text.Serializer.class)
public class TextSerializerMixin {
    private static final String splatcraft_INK_COLOR = Splatcraft.MOD_ID + ".ink_color";

    /**
     * @see InkColorText
     */
    /*@Inject(method = "serialize", at = @At(value = "INVOKE", target = "Ljava/util/List;isEmpty()Z", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void serializeAdditionals(Text text, Type type, JsonSerializationContext jsonSerializationContext, CallbackInfoReturnable<JsonElement> cir, JsonObject jsonObject) {
        if (text instanceof InkColorText) {
            InkColorText inkColorText = (InkColorText) text;
            jsonObject.addProperty(splatcraft_INK_COLOR, inkColorText.getInput());
        }
    }*/

    /*
     * TODO respect color lock
     */
    @Inject(method = "deserialize", at = @At(value = "INVOKE", target = "Lnet/minecraft/text/MutableText;setStyle(Lnet/minecraft/text/Style;)Lnet/minecraft/text/MutableText;", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void deserializeAdditionals(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext, CallbackInfoReturnable<MutableText> cir, JsonObject jsonObject, MutableText mutableText12) {
        if (jsonObject.has(splatcraft_INK_COLOR)) {
            mutableText12.fillStyle(Style.EMPTY.withColor(TextColor.fromRgb(InkColors.getNonNull(Identifier.tryParse(JsonHelper.getString(jsonObject, Splatcraft.MOD_ID + ".ink_color"))).color)));
        }
    }
}
