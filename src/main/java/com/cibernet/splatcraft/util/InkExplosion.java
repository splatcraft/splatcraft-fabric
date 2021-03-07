package com.cibernet.splatcraft.util;

import com.cibernet.splatcraft.inkcolor.*;
import com.cibernet.splatcraft.particle.InkSplashParticleEffect;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class InkExplosion extends Explosion {
    private final World world;
    private final double x;
    private final double y;
    private final double z;
    private final Entity entity;
    private final float size;
    private final DamageSource damageSource;
    private final List<BlockPos> affectedBlockPositions = Lists.newArrayList();

    private final InkColor color;
    private final InkBlockUtils.InkType inkType;
    private final boolean damageMobs;
    private final float damage;
    private final float blockDamage;
    private final ItemStack weapon;

    public static void createInkExplosion(World world, Entity source, DamageSource damageSource, BlockPos pos, float size, float blockDamage, float damage, boolean damageMobs, InkColor color, InkBlockUtils.InkType type, ItemStack weapon) {
        if (world.isClient) {
            return;
        }

        InkExplosion inksplosion = new InkExplosion(world, source, damageSource, pos.getX(), pos.getY(), pos.getZ(), blockDamage, damage, damageMobs, size, color, type, weapon);

        inksplosion.affectEntities();
        inksplosion.affectBlocks(false);
    }

    public InkExplosion(World world, Entity entity, DamageSource damageSource, double x, double y, double z, float blockDamage, float damage, boolean damageMobs, float size, InkColor color, InkBlockUtils.InkType inkType, ItemStack weapon) {
        super(world, entity, damageSource, null, x, y, z, blockDamage, false, DestructionType.BREAK);

        this.world = world;
        this.entity = entity;
        this.size = size;
        this.x = x;
        this.y = y;
        this.z = z;
        this.damageSource = damageSource;

        this.color = color;
        this.inkType = inkType;
        this.damageMobs = damageMobs;
        this.damage = damage;
        this.blockDamage = blockDamage;
        this.weapon = weapon;
    }

    public void affectEntities() {
        Set<BlockPos> set = Sets.newHashSet();

        for (int j = 0; j < 16; ++j) {
            for (int k = 0; k < 16; ++k) {
                for (int l = 0; l < 16; ++l) {
                    if (j == 0 || j == 15 || k == 0 || k == 15 || l == 0 || l == 15) {
                        double d0 = (float)j / 15.0F * 2.0F - 1.0F;
                        double d1 = (float)k / 15.0F * 2.0F - 1.0F;
                        double d2 = (float)l / 15.0F * 2.0F - 1.0F;
                        double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                        d0 = d0 / d3;
                        d1 = d1 / d3;
                        d2 = d2 / d3;
                        float f = this.size * (0.7F + this.world.random.nextFloat() * 0.6F);
                        double d4 = this.x;
                        double d6 = this.y;
                        double d8 = this.z;

                        for (; f > 0.0F; f -= 0.22500001F) {
                            BlockHitResult raytrace = world.raycast(new RaycastContext(new Vec3d(x+0.5f, y+0.5f, z+0.5f), new Vec3d(d4+0.5f, d6+0.5f, d8+0.5f), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, this.entity));
                            BlockPos blockpos = new BlockPos(d4, d6, d8);
                            f -= 0.3f * 0.3F;

                            if (raytrace != null) {
                                blockpos = raytrace.getBlockPos();
                            }

                            set.add(blockpos);

                            d4 += d0 * (double)0.3F;
                            d6 += d1 * (double)0.3F;
                            d8 += d2 * (double)0.3F;
                        }
                    }
                }
            }
        }

        this.affectedBlockPositions.addAll(set);
        float f2 = this.size * 2.0F;
        int k1 = MathHelper.floor(this.x - (double)f2 - 1.0D);
        int l1 = MathHelper.floor(this.x + (double)f2 + 1.0D);
        int i2 = MathHelper.floor(this.y - (double)f2 - 1.0D);
        int i1 = MathHelper.floor(this.y + (double)f2 + 1.0D);
        int j2 = MathHelper.floor(this.z - (double)f2 - 1.0D);
        int j1 = MathHelper.floor(this.z + (double)f2 + 1.0D);
        List<Entity> list = this.world.getOtherEntities(this.entity, new Box(k1, i2, j2, l1, i1, j1));

        for (Entity entity : list) {
            InkColor targetColor = InkColors.NONE;
            if (entity instanceof LivingEntity) {
                targetColor = ColorUtils.getInkColor(entity);
            }

            if (color != targetColor && targetColor != InkColors.NONE) {
                InkDamageUtils.splatDamage(world, (LivingEntity) entity, damage, color, this.entity, weapon, damageMobs);
            }

            if (entity instanceof SheepEntity) {
                DyeColor dyeColor = DyeColor.byName(this.color.getId().getPath(), ((SheepEntity) entity).getColor());
                if (dyeColor != null) {
                    ((SheepEntity) entity).setColor(dyeColor);
                }
            }
        }
    }

    public void affectBlocks(boolean spawnParticles) {
        if (spawnParticles) {
            if (!(this.size < 2.0F)) {
                this.world.addParticle(new InkSplashParticleEffect(this.color) /* TODO emitter */, this.x, this.y, this.z, 1.0D, 0.0D, 0.0D);
            } else {
                this.world.addParticle(new InkSplashParticleEffect(this.color), this.x, this.y, this.z, 1.0D, 0.0D, 0.0D);
            }
        }

        Collections.shuffle(this.affectedBlockPositions, this.world.random);

        for (BlockPos blockpos : this.affectedBlockPositions) {
            BlockState blockstate = this.world.getBlockState(blockpos);
            if (!blockstate.isAir()) {
                if (entity instanceof PlayerEntity)
                    InkBlockUtils.inkBlockAsPlayer((PlayerEntity) entity, world, blockpos, color, blockDamage, inkType);
                else {
                    InkBlockUtils.inkBlock(world, blockpos, color, blockDamage, inkType);
                }
            }
        }
    }

    @Override
    public DamageSource getDamageSource() {
        return this.damageSource;
    }

    public Entity getEntity() {
        return this.entity;
    }
}