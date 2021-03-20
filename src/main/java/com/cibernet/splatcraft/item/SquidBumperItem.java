package com.cibernet.splatcraft.item;

import com.cibernet.splatcraft.entity.SquidBumperEntity;
import com.cibernet.splatcraft.init.SplatcraftEntities;
import com.cibernet.splatcraft.inkcolor.ColorUtils;
import com.cibernet.splatcraft.item.inkable.AbstractInkableItem;
import net.minecraft.entity.SpawnReason;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

public class SquidBumperItem extends AbstractInkableItem {
    public SquidBumperItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext ctx) {
        Direction direction = ctx.getSide();
        if (direction == Direction.DOWN) {
            return ActionResult.FAIL;
        } else {
            World world = ctx.getWorld();
            ItemPlacementContext placeCtx = new ItemPlacementContext(ctx);
            BlockPos blockPos = placeCtx.getBlockPos();
            ItemStack itemStack = ctx.getStack();
            Vec3d vec3d = Vec3d.ofBottomCenter(blockPos);
            Box box = SplatcraftEntities.SQUID_BUMPER.getDimensions().getBoxAt(vec3d.getX(), vec3d.getY(), vec3d.getZ());
            if (world.isSpaceEmpty(null, box, entity -> true) && world.getOtherEntities(null, box).isEmpty()) {
                if (!world.isClient) {
                    ServerWorld serverWorld = (ServerWorld)world;
                    SquidBumperEntity entity = SplatcraftEntities.SQUID_BUMPER.create(serverWorld, itemStack.getTag(), null, ctx.getPlayer(), blockPos, SpawnReason.SPAWN_EGG, true, true);
                    if (entity == null) {
                        return ActionResult.FAIL;
                    }

                    ColorUtils.setInkColor(entity, ColorUtils.getInkColor(itemStack));
                    serverWorld.spawnEntityAndPassengers(entity);
                    float yaw = (float) MathHelper.floor((MathHelper.wrapDegrees(ctx.getPlayerYaw() - 180.0F) + 22.5F) / 45.0F) * 45.0F;
                    entity.refreshPositionAndAngles(entity.getX(), entity.getY(), entity.getZ(), yaw, 0.0F);
                    world.spawnEntity(entity);
                    world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ENTITY_ARMOR_STAND_PLACE, SoundCategory.BLOCKS, 0.75F, 0.8F);
                }

                itemStack.decrement(1);
                return ActionResult.success(world.isClient);
            } else {
                return ActionResult.FAIL;
            }
        }
    }
}
