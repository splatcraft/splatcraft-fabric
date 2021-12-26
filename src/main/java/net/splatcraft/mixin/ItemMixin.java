package net.splatcraft.mixin;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import net.splatcraft.inkcolor.InkColor;
import net.splatcraft.inkcolor.Inkable;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

import static net.splatcraft.util.SplatcraftConstants.NBT_INK_COLOR;
import static net.splatcraft.util.SplatcraftConstants.T_INK_COLOR;

@Mixin(Item.class)
public abstract class ItemMixin implements ItemConvertible {
    // add ink color tooltip to items with the tag
    // does not apply to blocks, as their translation key will most likely be changed for ink color
    @Inject(method = "appendTooltip", at = @At("HEAD"))
    private void onAppendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext ctx, CallbackInfo ci) {
        NbtCompound nbt = stack.getNbt();
        if (nbt != null && nbt.contains(NBT_INK_COLOR)) {
            Inkable inkable = Inkable.class.cast(stack);
            InkColor inkColor = inkable.getInkColor();
            Text colorText = new TranslatableText(inkColor.getTranslationKey()).setStyle(Style.EMPTY.withColor(inkColor.getDecimalColor()));
            tooltip.add(new TranslatableText(T_INK_COLOR, colorText).formatted(Formatting.GRAY));
        }
    }
}
