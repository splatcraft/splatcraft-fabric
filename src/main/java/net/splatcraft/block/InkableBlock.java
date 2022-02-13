package net.splatcraft.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.splatcraft.block.entity.SplatcraftBlockEntities;
import net.splatcraft.inkcolor.InkColors;
import net.splatcraft.inkcolor.Inkable;
import org.jetbrains.annotations.Nullable;


public class InkableBlock extends BlockWithEntity {
    public InkableBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return SplatcraftBlockEntities.INKABLE.instantiate(pos, state);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        // immediately set ink color to prevent flashing white on place
        if (world.getBlockEntity(pos) instanceof Inkable inkable) ((Inkable) (Object) stack).copyInkableTo(inkable);
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        ItemStack stack = super.getPickStack(world, pos, state);
        if (world.getBlockEntity(pos) instanceof Inkable inkable) inkable.copyInkableTo((Inkable) (Object) stack);
        return stack;
    }

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
        ItemStack stack = new ItemStack(this);
        ((Inkable) (Object) stack).setInkColor(InkColors.getDefault());
        stacks.add(stack);
    }
}
