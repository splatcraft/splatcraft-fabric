package net.splatcraft.network;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.splatcraft.block.InkPassableBlock;
import net.splatcraft.component.PlayerDataComponent;

public class NetworkingCommon {
    public static void setSquidForm(PlayerEntity player, boolean squid) {
        PlayerDataComponent data = PlayerDataComponent.get(player);
        data.setSquid(squid);

        player.calculateDimensions();

        if (squid) {
            player.setSprinting(false);
        } else {
            // teleport up if inside block
            BlockPos pos = player.getBlockPos();
            BlockState state = player.world.getBlockState(pos);
            if (state.getBlock() instanceof InkPassableBlock) {
                VoxelShape shape = state.getCollisionShape(player.world, pos);
                double maxY = shape.getMax(Direction.Axis.Y);
                if (maxY < 1.0d) {
                    double y = pos.getY() + maxY;
                    if (y > player.getY()) player.setPosition(player.getX(), y, player.getZ());
                }
            }
        }
    }
}
