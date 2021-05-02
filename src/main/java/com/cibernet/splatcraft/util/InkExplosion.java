package com.cibernet.splatcraft.util;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.entity.damage.SplatcraftDamageSources;
import com.cibernet.splatcraft.init.SplatcraftSoundEvents;
import com.cibernet.splatcraft.inkcolor.*;
import com.cibernet.splatcraft.particle.InkSplashParticleEffect;
import com.google.common.collect.Sets;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class InkExplosion extends Explosion {
    protected final World world;
    protected final Entity owner;
    protected final BlockPos startPos;
    protected final InkColor inkColor;
    protected final InkType inkType;
    protected final float size;

    protected static final ExplosionBehavior BEHAVIOR = new ExplosionBehavior() {
        @Override
        public Optional<Float> getBlastResistance(Explosion explosion, BlockView world, BlockPos pos, BlockState blockState, FluidState fluidState) {
            return blockState.isAir() && fluidState.isEmpty() ? Optional.empty() : Optional.of(0.0f);
        }
    };

    protected final List<BlockPos> affectedBlocks = new ArrayList<>();
    protected final Map<PlayerEntity, Vec3d> affectedPlayers = new HashMap<>();

    public InkExplosion(World world, @Nullable Entity owner, BlockPos startPos, InkColor inkColor, InkType inkType, float size) {
        super(world, owner, SplatcraftDamageSources.ENEMY_INK, null, startPos.getX(), startPos.getY(), startPos.getZ(), size, false, DestructionType.NONE);

        this.world = world;
        this.owner = owner;
        this.startPos = startPos;
        this.inkColor = inkColor;
        this.inkType = inkType;
        this.size = size;
    }

    public static void create(World world, Entity owner, BlockPos pos, InkColor inkColor, InkType inkType, float size, boolean spawnEffects) {
        new InkExplosion(world, owner, pos, inkColor, inkType, size).create(spawnEffects);
    }
    public static void create(World world, Entity owner, BlockPos pos, InkColor inkColor, InkType inkType, float size) {
        InkExplosion.create(world, owner, pos, inkColor, inkType, size, true);
    }
    public InkExplosion create(boolean spawnEffects) {
        this.collectBlocksAndDamageEntities();
        this.affectWorld(spawnEffects);
        return this;
    }

    @Override
    public void affectWorld(boolean spawnEffects) {
        if (spawnEffects && this.world.isClient) {
            this.world.playSound(this.getX(), this.getY(), this.getZ(), SplatcraftSoundEvents.ENTITY_INK_SQUID_SUBMERGE, SoundCategory.BLOCKS, 4.0F, (1.0F + (this.world.random.nextFloat() - this.world.random.nextFloat()) * 0.2F) * 0.7F, false);

            double sizeReciprocal = 3.0d / this.size;
            for (int i = 0; i < this.size * 17; i++) {
                this.world.addParticle(
                    new InkSplashParticleEffect(ColorUtil.getColorsFromInt(this.inkColor.getColorOrLocked()), 0.4f),
                    this.getX(), this.getY(), this.getZ(),
                    (Math.random() / sizeReciprocal) * (this.world.random.nextBoolean() ? -1.0f : 1.0f),
                    0.0D,
                    (Math.random() / sizeReciprocal) * (this.world.random.nextBoolean() ? -1.0f : 1.0f)
                );
            }
        }

        List<BlockPos> affectedBlocks = this.getAffectedBlocks();
        Collections.shuffle(affectedBlocks, this.world.random);
        for (BlockPos pos : affectedBlocks) {
            BlockState blockState = this.world.getBlockState(pos);
            if (!blockState.isAir()) {
                this.world.getProfiler().push(Splatcraft.MOD_ID + ".ink_explosion_blocks");

                if (this.owner instanceof PlayerEntity)
                    InkBlockUtil.inkBlockAsPlayer((PlayerEntity) this.owner, this.world, pos, this.inkColor, this.inkType);
                else {
                    InkBlockUtil.inkBlock(world, pos, this.inkColor, this.inkType);
                }

                this.world.getProfiler().pop();
            }
        }
    }

    @Override
    public void collectBlocksAndDamageEntities() {
        Set<BlockPos> set = Sets.newHashSet();

        int startX = this.getX();
        int startY = this.getY();
        int startZ = this.getZ();

        int x1;
        int x2;
        for (int i = 0; i < 16; ++i) {
            for (x1 = 0; x1 < 16; ++x1) {
                for (x2 = 0; x2 < 16; ++x2) {
                    if (i == 0 || i == 15 || x1 == 0 || x1 == 15 || x2 == 0 || x2 == 15) {
                        double x = (float)i / 15.0F * 2.0F - 1.0F;
                        double y = (float)x1 / 15.0F * 2.0F - 1.0F;
                        double z = (float)x2 / 15.0F * 2.0F - 1.0F;
                        double sqrtPos = Math.sqrt(x * x + y * y + z * z);
                        x /= sqrtPos;
                        y /= sqrtPos;
                        z /= sqrtPos;
                        double iX = startX;
                        double iY = startY;
                        double iZ = startZ;

                        for (float resistance = this.size * (0.7F + this.world.random.nextFloat() * 0.6F); resistance > 0.0F; resistance -= 0.22500001F) {
                            BlockPos blockPos = new BlockPos(iX, iY, iZ);
                            BlockState blockState = this.world.getBlockState(blockPos);
                            FluidState fluidState = this.world.getFluidState(blockPos);
                            Optional<Float> optional = InkExplosion.BEHAVIOR.getBlastResistance(this, this.world, blockPos, blockState, fluidState);
                            if (optional.isPresent()) {
                                resistance -= (optional.get() + 0.3F) * 0.3F;
                            }

                            if (resistance > 0.0F && InkExplosion.BEHAVIOR.canDestroyBlock(this, this.world, blockPos, blockState, resistance)) {
                                set.add(blockPos);
                            }

                            iX += x * 0.30000001192092896D;
                            iY += y * 0.30000001192092896D;
                            iZ += z * 0.30000001192092896D;
                        }
                    }
                }
            }
        }

        this.affectedBlocks.addAll(set);

        float size = this.size * 2.0F;
        x1 = MathHelper.floor(startX - (double)size - 1.0D);
        x2 = MathHelper.floor(startX + (double)size + 1.0D);
        int y1 = MathHelper.floor(startY - (double)size - 1.0D);
        int y2 = MathHelper.floor(startY + (double)size + 1.0D);
        int z1 = MathHelper.floor(startZ - (double)size - 1.0D);
        int z2 = MathHelper.floor(startZ + (double)size + 1.0D);
        List<Entity> list = this.world.getOtherEntities(this.owner, new Box(x1, y1, z1, x2, y2, z2));
        Vec3d vec3d = new Vec3d(startX, startY, startZ);

        for (Entity entity : list) {
            if (!entity.isImmuneToExplosion()) {
                double sqrt = MathHelper.sqrt(entity.squaredDistanceTo(vec3d)) / size;
                if (sqrt <= 1.0D) {
                    double x = entity.getX() - startX;
                    double y = (entity instanceof TntEntity ? entity.getY() : entity.getEyeY()) - startY;
                    double z = entity.getZ() - startZ;
                    double sqrtPos = MathHelper.sqrt(x * x + y * y + z * z);
                    if (sqrtPos != 0.0D) {
                        InkColor targetInkColor = InkColors.NONE;
                        if (entity instanceof LivingEntity) {
                            targetInkColor = ColorUtil.getInkColor(entity);
                        }

                        if (!this.inkColor.matches(targetInkColor.color) && !targetInkColor.equals(InkColors.NONE)) {
                            InkDamage.splat(world, (LivingEntity) entity, this.size * 4.3f, this.inkColor, this.owner, false);
                        }

                        if (entity instanceof SheepEntity) {
                            DyeColor dyeColor = DyeColor.byName(this.inkColor.id.getPath(), ((SheepEntity) entity).getColor());
                            if (dyeColor != null) {
                                ((SheepEntity) entity).setColor(dyeColor);
                            }
                        }

                        if (entity instanceof PlayerEntity) {
                            this.affectedPlayers.put((PlayerEntity) entity, Vec3d.ZERO);
                        }
                    }
                }
            }
        }
    }

    @Override
    public List<BlockPos> getAffectedBlocks() {
        return this.affectedBlocks;
    }

    @Override
    public Map<PlayerEntity, Vec3d> getAffectedPlayers() {
        return this.affectedPlayers;
    }

    public int getX() {
        return this.startPos.getX();
    }
    public int getY() {
        return this.startPos.getY();
    }
    public int getZ() {
        return this.startPos.getZ();
    }
}
