package com.cibernet.splatcraft.item.remote;

import com.cibernet.splatcraft.game.turf_war.ColorModificationMode;
import com.cibernet.splatcraft.game.turf_war.ColorModifications;
import com.cibernet.splatcraft.init.SplatcraftItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class InkDisruptorItem extends AbstractWhitelistedRemoteItem {
    public InkDisruptorItem(Item.Settings settings) {
        super(settings);
        SplatcraftItems.addToInkables(this);
    }

    @Override
    public boolean whitelistedRemoteUse(World world, PlayerEntity player, Hand hand, ItemStack stack, BlockPos pos, ColorModificationMode mode) {
        return ColorModifications.clearInk(world, player, pos, mode);
    }
}
