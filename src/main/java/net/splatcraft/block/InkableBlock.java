package net.splatcraft.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.splatcraft.block.entity.SplatcraftBlockEntities;
import net.splatcraft.inkcolor.Inkable;

import static net.splatcraft.util.SplatcraftUtil.*;

public class InkableBlock extends BlockWithEntity {
    public InkableBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return SplatcraftBlockEntities.INKABLE.instantiate(pos, state);
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        ItemStack stack = super.getPickStack(world, pos, state);
        if (world.getBlockEntity(pos) instanceof Inkable inkable) setInkColorOnStack(stack, inkable.getInkColor());
        return stack;
    }
}
