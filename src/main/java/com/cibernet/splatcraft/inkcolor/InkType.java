package com.cibernet.splatcraft.inkcolor;

import com.cibernet.splatcraft.block.InkedBlock;
import com.cibernet.splatcraft.init.SplatcraftBlocks;
import com.cibernet.splatcraft.init.SplatcraftItems;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public enum InkType {
    NORMAL,
    GLOWING;

    public Block asBlock() {
        switch (this) {
            default:
            case NORMAL:
                return SplatcraftBlocks.INKED_BLOCK;
            case GLOWING:
                return SplatcraftBlocks.GLOWING_INKED_BLOCK;
        }
    }

    public static InkType from(InkedBlock block) {
        return block.isGlowing() ? GLOWING : NORMAL;
    }
    public static InkType from(PlayerEntity entity) {
        return entity != null && entity.inventory.contains(new ItemStack(SplatcraftItems.SPLATFEST_BAND)) ? GLOWING : NORMAL;
    }
}
