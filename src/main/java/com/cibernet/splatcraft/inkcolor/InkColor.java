package com.cibernet.splatcraft.inkcolor;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.client.config.SplatcraftConfig;
import com.cibernet.splatcraft.client.config.SplatcraftConfigManager;
import com.cibernet.splatcraft.component.PlayerDataComponent;
import com.cibernet.splatcraft.util.TagUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;

public class InkColor {
    public final int color;
    public final float[] colorComponents;
    public final Identifier id;
    public final String idStr;

    public InkColor(Identifier id, int color) {
        this.id = id;
        this.color = color;
        this.colorComponents = ColorUtil.getRgbFromDecimal(color);
        this.idStr = this.id.toString();
    }
    public InkColor(Identifier id, float[] colorComponents) {
        this.id = id;
        this.color = ColorUtil.getDecimalFromRgb(colorComponents);
        this.colorComponents = colorComponents;
        this.idStr = this.id.toString();
    }
    public InkColor(String id, int color) {
        this(Identifier.tryParse(id), color);
    }
    public InkColor(DyeColor dyeColor) {
        this(new Identifier(dyeColor.getName()), dyeColor.getColorComponents());
    }

    public String getTranslationKey() {
        return Splatcraft.MOD_ID + ".ink_color." + this.id;
    }

    @Nullable
    public static InkColor from(Identifier identifier) {
        return InkColors.get(identifier);
    }
    @Nullable
    public static InkColor from(String identifier) {
        return InkColor.from(Identifier.tryParse(identifier));
    }
    @NotNull
    public static InkColor fromNonNull(NbtCompound tag) {
        NbtCompound splatcraft = TagUtil.getOrCreateSplatcraftTag(TagUtil.getBlockEntityTagOrRoot(tag));
        return InkColor.fromNonNull(splatcraft.getString("InkColor"));
    }
    @NotNull
    public static InkColor fromNonNull(Identifier identifier) {
        InkColor inkColor = InkColor.from(identifier);
        return inkColor == null ? InkColors.NONE : inkColor;
    }
    @NotNull
    public static InkColor fromNonNull(String identifier) {
        return InkColor.fromNonNull(Identifier.tryParse(identifier));
    }

    @Environment(EnvType.CLIENT)
    public int getColorOrLocked() {
        PlayerEntity player = MinecraftClient.getInstance().player;
        return !this.equals(InkColors.NONE) && SplatcraftConfig.ACCESSIBILITY.colorLock.value
            ? player != null && this.matches(PlayerDataComponent.getInkColor(player).color)
                ? SplatcraftConfig.ACCESSIBILITY.colorLockFriendly.value
                : SplatcraftConfig.ACCESSIBILITY.colorLockHostile.value
            : this.color;
    }
    @Environment(EnvType.CLIENT)
    public float[] getColorOrLockedComponents() {
        PlayerEntity player = MinecraftClient.getInstance().player;
        return !this.equals(InkColors.NONE) && SplatcraftConfig.ACCESSIBILITY.colorLock.value
            ? player != null && this.matches(PlayerDataComponent.getInkColor(player).color)
                ? SplatcraftConfigManager.getLockedColorComponentsCacheFriendly()
                : SplatcraftConfigManager.getLockedColorComponentsCacheHostile()
            : this.colorComponents;
    }

    @Override
    public String toString() {
        return this.idStr;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        InkColor inkColor = (InkColor) o;
        return color == inkColor.color && Arrays.equals(colorComponents, inkColor.colorComponents) && Objects.equals(id, inkColor.id) && Objects.equals(idStr, inkColor.idStr);
    }

    /**
     * A soft check for ink color matching. Checks only the color's id.
     */
    public boolean matches(int color) {
        return this.color == color;
    }

    public NbtElement writeNbt() {
        NbtCompound tag = new NbtCompound();
        tag.putString("id", this.toString());

        NbtCompound colorTag = new NbtCompound();
        colorTag.putFloat("r", this.colorComponents[0]);
        colorTag.putFloat("g", this.colorComponents[1]);
        colorTag.putFloat("b", this.colorComponents[2]);
        tag.put("Color", colorTag);

        return tag;
    }
}
