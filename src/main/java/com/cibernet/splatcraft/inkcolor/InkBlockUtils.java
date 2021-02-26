package com.cibernet.splatcraft.inkcolor;

import com.cibernet.splatcraft.block.AbstractColorableBlock;
import com.cibernet.splatcraft.block.AbstractPassableBlock;
import com.cibernet.splatcraft.block.InkedBlock;
import com.cibernet.splatcraft.block.entity.AbstractColorableBlockEntity;
import com.cibernet.splatcraft.block.entity.InkedBlockEntity;
import com.cibernet.splatcraft.init.SplatcraftItems;
import com.cibernet.splatcraft.init.SplatcraftStats;
import com.cibernet.splatcraft.tag.SplatcraftBlockTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InkBlockUtils {
    public static boolean playerInkBlock(PlayerEntity player, World world, BlockPos pos, InkColor color, float damage, InkType inkType) {
        boolean inked = InkBlockUtils.inkBlock(world, pos, color, damage, inkType);

        if (inked) {
            player.incrementStat(SplatcraftStats.BLOCKS_INKED);
        }

        return inked;
    }

    public static boolean inkBlock(World world, BlockPos pos, InkColor color, float damage, InkType inkType) {
        BlockState state = world.getBlockState(pos);

        if (InkedBlock.isTouchingLiquid(world, pos)) {
            return false;
        } else if (state.getBlock() instanceof AbstractColorableBlock) {
            return ((AbstractColorableBlock) state.getBlock()).inkBlock(world, pos, color, damage, inkType);
        } else if (canInk(world, pos)) {
            return true;
        } else {
            world.setBlockEntity(pos, new InkedBlockEntity());

            InkedBlockEntity inkedBlockEntity = (InkedBlockEntity) world.getBlockEntity(pos);
            if (inkedBlockEntity == null) {
                return false;
            }
            inkedBlockEntity.setInkColor(color);
            inkedBlockEntity.setSavedState(state);

            return true;
        }
    }

    private static <T extends Comparable<T>> BlockState mergeProperty(BlockState state, BlockState baseState, Property<T> property) {
        T value = baseState.get(property);
        return state.with(property, value);
    }


    public static boolean canInk(World world, BlockPos pos) {
        if (InkedBlock.isTouchingLiquid(world, pos)) {
            return false;
        }

        Block block = world.getBlockState(pos).getBlock();

        if (SplatcraftBlockTags.UNINKABLE_BLOCKS.contains(block)) {
            return false;
        } else if (!(world.getBlockEntity(pos) instanceof AbstractColorableBlockEntity) && world.getBlockEntity(pos) != null) {
            return false;
        } else if (SplatcraftBlockTags.INKABLE_BLOCKS.contains(block)) {
            return true;
        } else if (!canInkPassthrough(world, pos)) {
            return true;
        } else if (world.getBlockState(pos).isFullCube(world, pos)) {
            return true;
        }

        return !world.getBlockState(pos).hasSidedTransparency();
    }

    public static boolean canInkPassthrough(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);

        if (state.getBlock() instanceof AbstractPassableBlock) {
            return true;
        }
        return state.getCollisionShape(world, pos).isEmpty();
    }

    public static boolean canSquidHide(LivingEntity entity) {
        return !entity.world.getBlockState(new BlockPos(entity.getX(), entity.getY()-0.1, entity.getZ())).getBlock().equals(Blocks.AIR) && InkBlockUtils.canSquidSwim(entity) || InkBlockUtils.canSquidClimb(entity);
    }

    public static boolean canSquidSwim(LivingEntity entity) {
        BlockPos down = entity.getBlockPos().down();
        Block floorBlock = entity.world.getBlockState(down).getBlock();

        if (floorBlock instanceof AbstractColorableBlock && ((AbstractColorableBlock) floorBlock).canSwim()) {
            return ColorUtils.colorEquals(entity, entity.world.getBlockEntity(down));
        } else {
            return false;
        }
    }

    public static boolean onEnemyInk(LivingEntity entity) {
        if (!entity.isOnGround()) {
            return false;
        }

        BlockPos down = entity.getBlockPos().down();
        Block block = entity.world.getBlockState(down).getBlock();
        boolean canDamage = false;
        if (block instanceof AbstractColorableBlock) {
            canDamage = ((AbstractColorableBlock) block).canDamage();
        }

        return canDamage && ColorUtils.getInkColor(entity.world.getBlockEntity(down)) != InkColors.NONE && !canSquidSwim(entity);
    }

    public static boolean canSquidClimb(LivingEntity entity) {
        if (InkBlockUtils.onEnemyInk(entity)) {
            return false;
        } else {
            for (int i = 0; i < 4; i++) {
                float xOff = (i < 2 ? .32f : 0) * (i % 2 == 0 ? 1 : -1), zOff = (i < 2 ? 0 : .32f) * (i % 2 == 0 ? 1 : -1);
                BlockPos pos = new BlockPos(entity.getX() - xOff, entity.getY(), entity.getZ() - zOff);
                Block block = entity.world.getBlockState(pos).getBlock();

                if (!(block instanceof AbstractColorableBlock) || ((AbstractColorableBlock) block).canClimb()) {
                    BlockEntity blockEntity = entity.world.getBlockEntity(pos);
                    if (blockEntity instanceof AbstractColorableBlockEntity && ((AbstractColorableBlockEntity) blockEntity).getInkColor() == ColorUtils.getEntityColor(entity) && !entity.hasVehicle()) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static InkBlockUtils.InkType getInkType(LivingEntity entity) {
        if (entity instanceof PlayerEntity && ((PlayerEntity) entity).inventory.contains(new ItemStack(SplatcraftItems.SPLATFEST_BAND))) {
            return InkType.GLOWING;
        }
        return InkType.NORMAL;
    }

    public static class InkType implements Comparable<InkType> {

        public static final ArrayList<InkType> values = new ArrayList<>();

        public static final InkType NORMAL = new InkType();
        public static final InkType GLOWING = new InkType();

        public InkType() {
            values.add(this);
        }

        @Override
        public int compareTo(InkType o) {
            return values.indexOf(this) - values.indexOf(o);
        }

        public int getIndex() {return values.indexOf(this);}
    }

    public static class InkBlocks {
        Block defaultBlock;
        final List<Map.Entry<Class<? extends Block>, Block>> blockMap = new ArrayList<>();

        public InkBlocks(Block defaultBlock) {
            this.defaultBlock = defaultBlock;
        }

        public Block get(Block block) {
            if (blockMap.isEmpty())
                return defaultBlock;

            for (Map.Entry<Class<? extends Block>, Block> entry : blockMap)
                if (entry.getKey().isInstance(block))
                    return entry.getValue();

            return defaultBlock ;
        }

        public InkBlocks put(Class<? extends Block> blockClass, Block block) {
            blockMap.add(new AbstractMap.SimpleEntry<>(blockClass, block));
            return this;
        }
    }
}
