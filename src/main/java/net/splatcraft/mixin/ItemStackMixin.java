package net.splatcraft.mixin;

import net.minecraft.block.BlockWithEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.splatcraft.inkcolor.InkColor;
import net.splatcraft.inkcolor.InkColors;
import net.splatcraft.inkcolor.InkType;
import net.splatcraft.inkcolor.Inkable;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import static net.splatcraft.util.SplatcraftConstants.*;

@SuppressWarnings("ConstantConditions")
@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements Inkable {
    @Shadow public abstract Item getItem();
    @Shadow public abstract @Nullable NbtCompound getSubNbt(String key);
    @Shadow public abstract @Nullable NbtCompound getNbt();
    @Shadow public abstract NbtCompound getOrCreateSubNbt(String key);
    @Shadow public abstract NbtCompound getOrCreateNbt();
    @Shadow public abstract Text getName();

    @Unique
    @Override
    public InkColor getInkColor() {
        NbtCompound nbt = this.getItem() instanceof BlockItem item && item.getBlock() instanceof BlockWithEntity
            ? this.getSubNbt(NBT_BLOCK_ENTITY_TAG)
            : this.getNbt();
        if (nbt == null) return InkColors.getDefault();
        return InkColor.fromString(nbt.getString(NBT_INK_COLOR));
    }

    @Unique
    @Override
    public boolean setInkColor(InkColor inkColor) {
        if (inkColor.equals(this.getInkColor()) && this.hasInkColor()) return false;
        NbtCompound nbt = this.getItem() instanceof BlockItem item && item.getBlock() instanceof BlockWithEntity
            ? this.getOrCreateSubNbt(NBT_BLOCK_ENTITY_TAG)
            : this.getOrCreateNbt();
        nbt.putString(NBT_INK_COLOR, inkColor.getId().toString());
        return true;
    }

    @Unique
    @Override
    public boolean hasInkColor() {
        try {
            return this.getItem() instanceof BlockItem item && item.getBlock() instanceof BlockWithEntity
                ? this.getSubNbt(NBT_BLOCK_ENTITY_TAG).contains(NBT_INK_COLOR)
                : this.getNbt().contains(NBT_INK_COLOR);
        } catch (NullPointerException ignored) {}
        return false;
    }

    @Unique
    @Override
    public InkType getInkType() {
        NbtCompound nbt = this.getItem() instanceof BlockItem item && item.getBlock() instanceof BlockWithEntity
            ? this.getSubNbt(NBT_BLOCK_ENTITY_TAG)
            : this.getNbt();
        if (nbt == null) return InkType.NORMAL;
        return InkType.safeValueOf(nbt.getString(NBT_INK_TYPE));
    }

    @Unique
    @Override
    public boolean setInkType(InkType inkType) {
        if (inkType.equals(this.getInkType()) && this.hasInkType()) return false;
        NbtCompound nbt = this.getItem() instanceof BlockItem item && item.getBlock() instanceof BlockWithEntity
            ? this.getOrCreateSubNbt(NBT_BLOCK_ENTITY_TAG)
            : this.getOrCreateNbt();
        nbt.putString(NBT_INK_TYPE, inkType.toString());
        return true;
    }

    @Unique
    @Override
    public boolean hasInkType() {
        try {
            return this.getItem() instanceof BlockItem item && item.getBlock() instanceof BlockWithEntity
                ? this.getSubNbt(NBT_BLOCK_ENTITY_TAG).contains(NBT_INK_TYPE)
                : this.getNbt().contains(NBT_INK_TYPE);
        } catch (NullPointerException ignored) {}
        return false;
    }

    @Unique
    @Override
    public Text getTextForCommand() {
        return this.getName();
    }
}
