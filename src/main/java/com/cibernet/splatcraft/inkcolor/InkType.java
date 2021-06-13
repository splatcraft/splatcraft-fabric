package com.cibernet.splatcraft.inkcolor;

import com.cibernet.splatcraft.block.InkedBlock;
import com.cibernet.splatcraft.init.SplatcraftBlocks;
import com.cibernet.splatcraft.init.SplatcraftItems;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public enum InkType {
    NORMAL(false),
    GLOWING(true);

    protected final boolean glowing;

    InkType(boolean glowing) {
        this.glowing = glowing;
    }

    public boolean isGlowing() {
        return this.glowing;
    }

    public Block asBlock() {
        return switch (this) {
            case NORMAL -> SplatcraftBlocks.INKED_BLOCK;
            case GLOWING -> SplatcraftBlocks.GLOWING_INKED_BLOCK;
        };
    }

    public static InkType from(InkedBlock block) {
        return block.isGlowing() ? GLOWING : NORMAL;
    }
    public static InkType from(PlayerEntity entity) {
        return entity != null && entity.getInventory().contains(new ItemStack(SplatcraftItems.SPLATFEST_BAND)) ? GLOWING : NORMAL;
    }
}
