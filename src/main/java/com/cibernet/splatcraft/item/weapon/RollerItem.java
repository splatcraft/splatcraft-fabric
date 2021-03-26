package com.cibernet.splatcraft.item.weapon;

import com.cibernet.splatcraft.entity.InkProjectileEntity;
import com.cibernet.splatcraft.handler.PlayerPoseHandler;
import com.cibernet.splatcraft.inkcolor.ColorUtils;
import com.cibernet.splatcraft.inkcolor.InkBlockUtils;
import com.cibernet.splatcraft.inkcolor.InkColor;
import com.cibernet.splatcraft.inkcolor.InkDamageUtils;
import com.cibernet.splatcraft.item.AttackInputDetectable;
import com.cibernet.splatcraft.item.weapon.component.RollerComponent;
import com.google.common.collect.ImmutableList;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

import java.util.Random;

public class RollerItem extends AbstractWeaponItem implements AttackInputDetectable {
    protected final Item.Settings settings;
    public final RollerComponent component;

    public RollerItem(Item.Settings settings, RollerComponent component) {
        super(settings, component.consumption);

        this.settings = settings;
        this.component = component;
    }

    public RollerItem(RollerItem rollerItem) {
        this(rollerItem.settings, rollerItem.component);
    }

    public RollerItem(RollerItem rollerItem, RollerComponent component) {
        this(rollerItem.settings, component);
    }

    @Override
    protected ImmutableList<WeaponStat> createWeaponStats() {
        return ImmutableList.of(
            new WeaponStat("range", (stack, world) -> (int) ((this.component.fling.speed /*+ this.component.swing.speed*/) * 50)),
            new WeaponStat("ink_speed", (stack, world) -> (int) (/*this.component.dash.speed /*/ 2f * 100)),
            new WeaponStat("handling", (stack, world) -> (int) ((20/* - (this.component.fling.time + this.component.swing.time)*/ / 2f) * 5))
        );
    }

    @Override
    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        return !miner.isCreative();
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if (!player.isSneaking()) {
            player.getItemCooldownManager().getCooldownProgress(this, 0);
        }

        return super.use(world, player, hand);
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (user instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) user;
            InkBlockUtils.InkType isGlowing = InkBlockUtils.getInkType(player);

            if (hasInk(player, stack, false)) {
                InkColor color = ColorUtils.getInkColor(stack);
                int downReach = player.getY() % 1 < 0.5 ? 1 : 0;
                Vec3d fwd = getFwd(0, player.yaw).normalize();
                fwd = new Vec3d(Math.round(fwd.x), Math.round(fwd.y), Math.round(fwd.z));

                BlockPos pos = new BlockPos(Math.floor(player.getX()) + 0.5, player.getY() - downReach, Math.floor(player.getZ()) + 0.5);

                for (int i = 0; i < this.component.radius; i++) {
                    for (int rollDepth = 0; rollDepth < 2; rollDepth++) {
                        double xOff;
                        double zOff = i == 0 ? 0 : Math.round(fwd.x) * Math.ceil(i / 2.0);

                        if (i % 2 == 0) {
                            zOff *= -1;
                        }

                        Direction horizontalFacing = player.getHorizontalFacing();
                        boolean horizontalFacingIsSouth = horizontalFacing.equals(Direction.SOUTH);
                        if (horizontalFacing.equals(Direction.NORTH) || horizontalFacingIsSouth) {
                            zOff = rollDepth - (horizontalFacingIsSouth ? horizontalFacing.getDirection().offset() : 0);
                            xOff = i * (horizontalFacing.equals(Direction.SOUTH) ? -1 : 1);
                        } else {
                            xOff = rollDepth;
                        }

                        BlockPos inkPos = pos.add(fwd.x * 2 + xOff, -1, fwd.z * 2 + zOff);

                        int h = 0;
                        for (; h <= downReach; h++) {
                            if (InkBlockUtils.canInkPassthrough(world, inkPos.up())) {
                                break;
                            } else {
                                inkPos = inkPos.up();
                            }
                        }

                        if (InkBlockUtils.inkBlockAsPlayer(player, world, inkPos, color, this.component.damage, isGlowing)) {
                            Random random = world.random;
                            double min = -0.5D;
                            double max = 0.5D;

                            for (int pCount = 0; pCount < 4; pCount++) {
                                double x = inkPos.getX() + 0.5D + (min + random.nextDouble() * (max - min));
                                double y = inkPos.getY() + 1.0D + (0.0D + random.nextDouble() * (0.02D - 0.0D));
                                double z = inkPos.getZ() + 0.5D + (min + random.nextDouble() * (max - min));
                                ColorUtils.addInkSplashParticle(world, color, new Vec3d(x, y, z));
                            }
                            reduceInk(player, false);

                            if (player.getVelocity().getX() != 0 || player.getVelocity().getZ() != 0) {
                                player.setSprinting(true);
                            }
                        }

                        for (LivingEntity target : world.getEntitiesIncludingUngeneratedChunks(LivingEntity.class, new Box(inkPos.up()))) {
                            if (!target.equals(player)) {
                                InkDamageUtils.rollDamage(world, target, this.component.damage, color, player, false);
                            }
                        }

                        if (h > downReach) {
                            break;
                        }
                    }
                }
            } else {
                sendNoInkMessage(player);
            }
        }
    }

    @Override
    public void onAttack(ServerPlayerEntity player, ItemStack stack) {
        if (this.component.fling != null) {
            if (hasInk(player, stack, true)) {
                reduceInk(player, true);

                InkProjectileEntity proj = new InkProjectileEntity(player.world, player, stack, InkBlockUtils.getInkType(player), this.component.fling.size, this.component.fling.damage);
                proj.setProperties(player, player.pitch, player.yaw, 0.0F, 1.5F, 1.0F);
                player.world.spawnEntity(proj);
            } else {
                sendNoInkMessage(player);
            }
        }
    }

    private Vec3d getFwd(float pitch, float yaw) {
        float f = MathHelper.cos(-yaw * 0.017453292F - (float)Math.PI);
        float f1 = MathHelper.sin(-yaw * 0.017453292F - (float)Math.PI);
        float f2 = -MathHelper.cos(-pitch * 0.017453292F);
        float f3 = MathHelper.sin(-pitch * 0.017453292F);
        return new Vec3d(f1 * f2, f3, f * f2);
    }

    @Override
    public PlayerPoseHandler.WeaponPose getPose() {
        return PlayerPoseHandler.WeaponPose.ROLL;
    }
}
