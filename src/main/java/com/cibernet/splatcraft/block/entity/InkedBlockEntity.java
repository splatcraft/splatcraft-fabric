package com.cibernet.splatcraft.block.entity;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.block.InkedBlock;
import com.cibernet.splatcraft.init.SplatcraftBlockEntities;
import com.cibernet.splatcraft.util.TagUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.math.BlockPos;

public class InkedBlockEntity extends AbstractInkableBlockEntity {
    public static final String id = InkedBlock.id;

    protected BlockState savedState = Blocks.AIR.getDefaultState();

    public InkedBlockEntity(BlockPos pos, BlockState state) {
        super(SplatcraftBlockEntities.INKED_BLOCK, pos, state);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        NbtCompound splatcraft = TagUtil.getOrCreateSplatcraftTag(tag);
        splatcraft.put("SavedState", NbtHelper.fromBlockState(savedState));

        tag.put(Splatcraft.MOD_ID, splatcraft);
        return super.writeNbt(tag);
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);

        NbtCompound splatcraft = TagUtil.getOrCreateSplatcraftTag(TagUtil.getBlockEntityTagOrRoot(tag));
        this.savedState = NbtHelper.toBlockState(splatcraft.getCompound("SavedState"));
    }

    public BlockState getSavedState() {
        return this.savedState;
    }
    public void setSavedState(BlockState savedState) {
        this.savedState = savedState;
        if (this.world != null && !this.world.isClient) {
            this.sync();
        }
    }
    public boolean hasSavedState() {
        return this.savedState.getBlock() != null;
    }
}
