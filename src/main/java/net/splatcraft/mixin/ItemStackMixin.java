package net.splatcraft.mixin;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.splatcraft.inkcolor.InkColor;
import net.splatcraft.inkcolor.InkColors;
import net.splatcraft.inkcolor.Inkable;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import static net.splatcraft.util.SplatcraftConstants.NBT_BLOCK_ENTITY_TAG;
import static net.splatcraft.util.SplatcraftConstants.NBT_INK_COLOR;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements Inkable {
    @Shadow public abstract Item getItem();
    @Shadow public abstract @Nullable NbtCompound getSubNbt(String key);
    @Shadow public abstract @Nullable NbtCompound getNbt();
    @Shadow public abstract NbtCompound getOrCreateSubNbt(String key);
    @Shadow public abstract NbtCompound getOrCreateNbt();
    @Shadow public abstract Text getName();

    @Override
    public InkColor getInkColor() {
        NbtCompound nbt = this.getItem() instanceof BlockItem
            ? this.getSubNbt(NBT_BLOCK_ENTITY_TAG)
            : this.getNbt();
        if (nbt == null) return InkColors.getDefault();
        return InkColor.fromString(nbt.getString(NBT_INK_COLOR));
    }

    @Override
    public boolean setInkColor(InkColor inkColor) {
        if (inkColor.equals(this.getInkColor())) return false;
        NbtCompound nbt = this.getItem() instanceof BlockItem
            ? this.getOrCreateSubNbt(NBT_BLOCK_ENTITY_TAG)
            : this.getOrCreateNbt();
        nbt.putString(NBT_INK_COLOR, inkColor.getId().toString());
        return true;
    }

    @Override
    public Text getTextForCommand() {
        return this.getName();
    }
}
