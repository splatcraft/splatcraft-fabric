package net.splatcraft.api.block;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.BlockPos;
import net.splatcraft.api.block.entity.SplatcraftBlockEntityType;
import net.splatcraft.api.inkcolor.InkType;

public class InkedBlock extends InkableBlock {
    public static final Settings DEFAULT_PROPERTIES = FabricBlockSettings.of(Material.ORGANIC_PRODUCT, MapColor.TERRACOTTA_BLACK)
                                                                         .sounds(BlockSoundGroup.SLIME)
                                                                         .nonOpaque().ticksRandomly()
                                                                         .suffocates((state, world, pos) -> false)
                                                                         .solidBlock((state, world, pos) -> true);
    public static final int GLOWING_LIGHT_LEVEL = 6;

    private final InkType inkType;

    public InkedBlock(InkType inkType) {
        super(inkType == InkType.GLOWING ? FabricBlockSettings.copyOf(DEFAULT_PROPERTIES).luminance(GLOWING_LIGHT_LEVEL) : DEFAULT_PROPERTIES);
        this.inkType = inkType;
    }

    public InkType getInkType() {
        return this.inkType;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return SplatcraftBlockEntityType.INKED_BLOCK.instantiate(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }
}
