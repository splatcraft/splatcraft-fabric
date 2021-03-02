package com.cibernet.splatcraft.item;

import com.cibernet.splatcraft.component.PlayerDataComponent;
import com.cibernet.splatcraft.entity.InkProjectileEntity;
import com.cibernet.splatcraft.entity.SquidBumperEntity;
import com.cibernet.splatcraft.handler.PlayerPoseHandler;
import com.cibernet.splatcraft.inkcolor.ColorUtils;
import com.cibernet.splatcraft.inkcolor.InkBlockUtils;
import com.cibernet.splatcraft.inkcolor.InkColor;
import com.cibernet.splatcraft.inkcolor.InkDamageUtils;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

import java.util.List;

public class SplatRollerItem extends AbstractWeaponItem {
    public static final String id = "splat_roller";

    protected double weaponSpeed;
    protected float flingSpeed;
    protected boolean isBrush;
    protected int rollRadius;
    protected float flingSize;
    protected float rollDamage;
    protected float flingDamage;
    protected float flingConsumption;

    /*private final double rollSpeed;*/

    public SplatRollerItem(Settings settings, double weaponSpeed, float flingSpeed, float flingDamage, float flingSize, float flingConsumption/*, double rollSpeed*/, int rollRadius, float rollDamage, float inkConsumption, boolean isBrush) {
        super(inkConsumption, settings);

        this.weaponSpeed = weaponSpeed;
        this.flingSpeed = flingSpeed;
        this.rollRadius = rollRadius;
        this.flingSize = flingSize;
        this.rollDamage = rollDamage;
        this.flingDamage = flingDamage;
        this.flingConsumption = flingConsumption;
        this.isBrush = isBrush;
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
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        Multimap<EntityAttribute, EntityAttributeModifier> multimap = HashMultimap.create();
        if (slot == EquipmentSlot.MAINHAND) {
            multimap.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Weapon modifier", getWeaponSpeed(), EntityAttributeModifier.Operation.ADDITION));
        }
        return multimap;
    }

    public double getWeaponSpeed() {
        return this.weaponSpeed;
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (user instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) user;
            InkBlockUtils.InkType isGlowing = InkBlockUtils.getInkType(player);

            if (hasInk(player, stack)) {
                InkColor color = ColorUtils.getInkColor(stack);
                int downReach = player.getY() % 1 < 0.5 ? 1 : 0;
                Vec3d fwd = getFwd(0, player.yaw).normalize();
                fwd = new Vec3d(Math.round(fwd.x), Math.round(fwd.y), Math.round(fwd.z));

                BlockPos pos = new BlockPos(Math.floor(player.getX()) + 0.5, player.getY() - downReach, Math.floor(player.getZ()) + 0.5);

                for (int i = 0; i < rollRadius; i++)
                    for (int rollDepth = 0; rollDepth < 2; rollDepth++) {
                        double xOff = i == 0 ? 0 : Math.round(fwd.z) * Math.ceil(i/2.0);
                        double zOff = i == 0 ? 0 : Math.round(fwd.x) * Math.ceil(i/2.0);

                        if (i % 2 == 0) {
                            xOff *= -1;
                            zOff *= -1;
                        }

                        if (player.getHorizontalFacing().equals(Direction.NORTH) || player.getHorizontalFacing().equals(Direction.SOUTH)) {
                            zOff = (rollDepth - 1) * player.getHorizontalFacing().getDirection().offset();
                        } else {
                            xOff = (rollDepth - 1) * player.getHorizontalFacing().getDirection().offset();
                        }


                        BlockPos inkPos = pos.add(fwd.x * 2 + xOff, -1, fwd.z * 2 + zOff);

                        int h = 0;
                        while (h <= downReach) {
                            if (InkBlockUtils.canInkPassthrough(world, inkPos.up())) {
                                break;
                            } else {
                                inkPos = inkPos.up();
                            }
                            h++;
                        }

                        if (InkBlockUtils.canInk(world, inkPos)) {
                            ColorUtils.addInkSplashParticle(world, color, Vec3d.ofCenter(inkPos.up()));
                            InkBlockUtils.inkBlockAsPlayer(player, world, inkPos, color, rollDamage, isGlowing);
                            reduceInk(player);
                        }

                        Entity knockbackEntity = null;
                        List<LivingEntity> inkedPlayers = world.getEntitiesIncludingUngeneratedChunks(LivingEntity.class, new Box(inkPos.up()));
                        int j = 0;

                        if (player.getAttackCooldownProgress(0.0F) >= 1.0F) {
                            for (LivingEntity target : inkedPlayers) {
                                if (target.equals(player)) {
                                    continue;
                                }

                                boolean isTargetSameColor = false;
                                if (target instanceof PlayerEntity) {
                                    isTargetSameColor = PlayerDataComponent.getInkColor((PlayerEntity) target) == color;
                                }

                                float rollDamage = this.rollDamage;
                                boolean damaged = InkDamageUtils.rollDamage(world, target, rollDamage, color, player, stack, false);

                                if (target instanceof SquidBumperEntity && (((SquidBumperEntity) target).getInkColor() == color) || !damaged) {
                                    rollDamage = 0;
                                }

                                if (!isTargetSameColor && ((!(target.getHealth() - rollDamage > 0) && !(target instanceof SquidBumperEntity)) || (target instanceof SquidBumperEntity && ((SquidBumperEntity) target).getInkHealth() - rollDamage > 0))) {
                                    knockbackEntity = target;
                                }

                                if (j++ >= 5) {
                                    knockbackEntity = target;
                                    break;
                                }
                            }
                        }
                        if (knockbackEntity != null && world.isClient) {
                            applyEntityCollision(knockbackEntity, player, 10);
                        }

                        if (h > downReach) {
                            break;
                        }
                    }

            } else {
                sendNoInkMessage(player);
            }
        }
    }

    public void applyEntityCollision(Entity source, Entity target, double power) {
        if (!target.isConnectedThroughVehicle(source) && !target.equals(source)) {
            if (!source.noClip && !target.noClip) {
                double d0 = target.getX() - source.getX();
                double d1 = target.getZ() - source.getZ();
                double d2 = MathHelper.absMax(d0, d1);

                if (d2 >= 0.009999999776482582D) {
                    d2 = MathHelper.sqrt(d2);
                    d0 = d0 / d2;
                    d1 = d1 / d2;
                    double d3 = 1.0D / d2;

                    if (d3 > 1.0D) {
                        d3 = 1.0D;
                    }

                    d0 = d0 * d3;
                    d1 = d1 * d3;
                    d0 = d0 * 0.05000000074505806D;
                    d1 = d1 * 0.05000000074505806D;
                    d0 = d0 * (double)(1.0F - source.pushSpeedReduction);
                    d1 = d1 * (double)(1.0F - source.pushSpeedReduction);
                    d0 *= power;
                    d1 *= power;

                    if (!target.hasPassengers()) {
                        target.setVelocity(0.0D, target.getVelocity().getY(), 0.0D);
                        target.addVelocity(d0, 0.0D, d1);
                    }
                }
            }
        }
    }

    @Override
    public void onAttack(ServerPlayerEntity player, ItemStack stack) {
        if (hasInk(player, stack)) {
            reduceInk(player, this.flingConsumption);

            InkProjectileEntity proj = new InkProjectileEntity(player.world, player, stack, InkBlockUtils.getInkType(player), this.flingSize, this.flingDamage);
            proj.setProperties(player, player.pitch, player.yaw, 0.0F, 1.5F, 1.0F);
            player.world.spawnEntity(proj);
        } else {
            sendNoInkMessage(player);
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
    public boolean doesNotAffectMovementWhenUsed() {
        return true;
    }

    @Override
    public PlayerPoseHandler.WeaponPose getPose() {
        return PlayerPoseHandler.WeaponPose.ROLL;
    }
}
