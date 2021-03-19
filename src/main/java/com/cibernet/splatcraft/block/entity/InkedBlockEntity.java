package com.cibernet.splatcraft.block.entity;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.block.InkedBlock;
import com.cibernet.splatcraft.init.SplatcraftBlockEntities;
import com.cibernet.splatcraft.inkcolor.ColorUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.registry.Registry;

public class InkedBlockEntity extends AbstractInkableBlockEntity {
    public static final String id = InkedBlock.id;

    private BlockState savedState = Blocks.AIR.getDefaultState();

    public InkedBlockEntity() {
        super(SplatcraftBlockEntities.INKED_BLOCK);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        CompoundTag splatcraft = ColorUtils.getOrCreateSplatcraftTag(tag);
        splatcraft.put("SavedState", NbtHelper.fromBlockState(savedState));

        tag.put(Splatcraft.MOD_ID, splatcraft);
        return super.toTag(tag);
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        CompoundTag splatcraft = tag.getCompound(Splatcraft.MOD_ID);

        this.savedState = NbtHelper.toBlockState(splatcraft.getCompound("SavedState"));
    }

    @Override
    public int getRawId() {
        return Registry.BLOCK_ENTITY_TYPE.getRawId(SplatcraftBlockEntities.INKED_BLOCK);
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
