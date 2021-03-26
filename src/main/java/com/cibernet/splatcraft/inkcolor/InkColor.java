package com.cibernet.splatcraft.inkcolor;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.client.config.SplatcraftConfig;
import com.cibernet.splatcraft.component.PlayerDataComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

import java.util.Objects;

public class InkColor {
    private final int color;
    private final Identifier id;

    public InkColor(Identifier id, int color) {
        this.id = id;
        this.color = color;
    }
    public InkColor(String id, int color) {
        this(Identifier.tryParse(id), color);
    }
    public InkColor(DyeColor dyeColor) {
        this(new Identifier(dyeColor.getName()), dyeColor.color);
    }

    public String getTranslationKey() {
        return Splatcraft.MOD_ID + ".ink_color." + this.id;
    }

    public static InkColor getFromId(Identifier identifier) {
        InkColor object = InkColors.get(identifier);
        return object == null ? InkColors.NONE : object;
    }
    public static InkColor getFromId(String identifier) {
        return InkColor.getFromId(Identifier.tryParse(identifier));
    }

    public int getColor() {
        return this.color;
    }
    @Environment(EnvType.CLIENT)
    public int getColorOrLocked() {
        PlayerEntity player = MinecraftClient.getInstance().player;
        return !this.equals(InkColors.NONE) && SplatcraftConfig.ACCESSIBILITY.colorLock.value
            ? player != null && this.matches(PlayerDataComponent.getInkColor(player).getColor())
                ? SplatcraftConfig.ACCESSIBILITY.colorLockFriendly.value
                : SplatcraftConfig.ACCESSIBILITY.colorLockHostile.value
            : this.color;
    }
    public Identifier getId() {
        return this.id;
    }

    @Override
    public String toString() {
        return this.getId().toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InkColor inkColor = (InkColor) o;
        return color == inkColor.color && Objects.equals(id, inkColor.id);
    }

    /**
     * A soft check for ink color matching. Checks only the color's id.
     */
    public boolean matches(int color) {
        return this.color == color;
    }

    public Tag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.putString("id", id.toString());
        tag.putInt("Color", color);

        return tag;
    }
}
