package net.splatcraft.api.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.math.BlockPos;
import net.splatcraft.api.block.SplatcraftBlocks;

import static net.splatcraft.api.util.SplatcraftConstants.*;

public class InkedBlockEntity extends InkableBlockEntity {
    /**
     * Defines a block's stored {@link BlockState}.
     */
    private BlockState state = SplatcraftBlocks.CANVAS.getDefaultState();

    public InkedBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public BlockState getState() {
        return this.state;
    }

    public boolean setState(BlockState state) {
        if (this.state.equals(state)) return false;
        this.state = state;
        this.sync();
        return true;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.put(NBT_BLOCK_STATE, NbtHelper.fromBlockState(this.state));
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.state = NbtHelper.toBlockState(nbt.getCompound(NBT_BLOCK_STATE));
    }
}
