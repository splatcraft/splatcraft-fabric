package net.splatcraft.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Blocks.class)
public interface BlocksInvoker {
    @Invoker("never")
    static Boolean invoke_never(BlockState state, BlockView world, BlockPos pos, EntityType<?> type) {
        throw new AssertionError();
    }
}
